package cs162.assign4.syntax

import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical

object Aliases {
  type NameLabel = String
  type ConsLabel = String
  type FldLabel = String
}

import Aliases._

case class Program( typedefs:Set[TypeDef], e:Exp )
case class TypeDef( name:NameLabel, tvars:Seq[TVar], constructors:Map[ConsLabel, Type] )

sealed abstract class Exp
case class Var( name:String ) extends Exp
case class Num( n:BigInt ) extends Exp
case class Bool( b:Boolean ) extends Exp
case class Unit() extends Exp
case class Fun( params:Seq[(Var, Type)], body:Exp ) extends Exp
case class Call( fun:Exp, args:Seq[Exp] ) extends Exp
case class If( e1:Exp, e2:Exp, e3:Exp ) extends Exp
case class Let( x:Var, e1:Exp, e2:Exp ) extends Exp
case class Rec( x:Var, typ:Type, e1:Exp, e2:Exp ) extends Exp
case class Record( fields:Map[FldLabel, Exp] ) extends Exp
case class Access( e:Exp, field:FldLabel ) extends Exp
case class Construct( name:NameLabel, constructor:ConsLabel, typs:Seq[Type], e:Exp ) extends Exp
case class Match( e:Exp, cases:Seq[(ConsLabel, Var, Exp)] ) extends Exp
case class TAbs( tvars:Seq[TVar], fun:Fun ) extends Exp
case class TApp( e:Exp, typs:Seq[Type] ) extends Exp

// builtin functions
sealed abstract class Builtin extends Exp
case object Plus extends Builtin
case object Minus extends Builtin
case object Times extends Builtin
case object Divide extends Builtin
case object LT extends Builtin
case object EQ extends Builtin
case object And extends Builtin
case object Or extends Builtin
case object Not extends Builtin

sealed abstract class Type
case object NumT extends Type
case object BoolT extends Type
case object UnitT extends Type
case class FunT( params:Seq[Type], ret:Type ) extends Type
case class RcdT( fields:Map[FldLabel, Type] ) extends Type
case class TypT( name:NameLabel, typs:Seq[Type] ) extends Type
case class TVar( name:String ) extends Type
case class TFunT( tvars:Seq[TVar], funt:FunT ) extends Type

// copied from:
// http://jim-mcbeath.blogspot.com/2011/07/debugging-scala-parser-combinators.html
trait DebugStandardTokenParsers extends StdTokenParsers {
    class Wrap[+T](name:String,parser:Parser[T]) extends Parser[T] {
        def apply(in: Input): ParseResult[T] = {
            val first = in.first
            val pos = in.pos
            val offset = in.offset
            val t = parser.apply(in)
//             println(name+".apply for token "+first+
//                     " at position "+pos+" offset "+offset+" returns "+t)
            t
        }
    }

  implicit def toWrapped(name:String) = new {
    def !!![T](p:Parser[T]) = new Wrap(name,p)
  }
}

object Parsers extends StdTokenParsers with PackratParsers with DebugStandardTokenParsers {
  type Parser[T] = PackratParser[T]

  type Error = String
  type Tokens = StdLexical
  
  class FunLexical extends StdLexical {
     override def token: Parser[Token] = '\'' ^^ (c => Keyword(c.toString)) | super.token
  }

  val lexical = new FunLexical

  lexical.delimiters ++= Seq(".", "(", ")", ":", "=", "->", ",", ";",
                             ";;", "|", "=>", "(|", "|)", "+", "-", "*", "/", "&&", "||", "=", "<", ">", "!", "[", "]", "'")

  lexical.reserved ++= Seq("num", "bool", "unit", "let", "rec", "in", "type", "case", "of", "true", "false", "if", "then", "else", "nil", "unit")
  

  class Runnable[A](val xs: Parser[A]) {
    def run(stream: String, filename: String): Either[Error, A] = {
      val commentFree = stream.replaceAll("--.*", "")
      val tokens = new lexical.Scanner(commentFree)

      phrase(xs)(tokens) match {
        case e: NoSuccess    => Left(e.toString)
        case Success(res, _) => Right(res)
      }
    }
  }

  implicit def parserToRunnable[A](p: Parser[A]): Runnable[A] = new Runnable(p)

  def duplicates[A](as: Seq[A]): Seq[A] =
    as.groupBy(n => n).values.filter(_.tail.nonEmpty).map(_.head).toSeq

  def ifDuplicates[A, B](items: Seq[A], whatFailed: => String, onSuccess: => B, guard: => Option[String] = None): Parser[B] = {
    val dupGroups = duplicates(items)
    if (dupGroups.nonEmpty) {
      failure("duplicate " + whatFailed + ": " + dupGroups.mkString(", "))
    } else if (guard.isDefined) {
      failure(guard.get)
    } else {
      success(onSuccess)
    }
  }

  lazy val program: Parser[Program] = "program" !!! typedefs ~ exp ^^ {
    case tdefs ~ exp => Program(tdefs.toSet, exp)
  }

  lazy val typedefs: Parser[Set[TypeDef]] = rep(typedef) flatMap(typedefs =>
    ifDuplicates(typedefs.map(_.name),
                 "type names",
                 typedefs.toSet))

  lazy val typedef: Parser[TypeDef] = "type" ~ tyname ~ opt(tyabs) ~ "=" ~ rep1sep(constDef, "|") flatMap {
    case _ ~ name ~ tvars ~ _ ~ constructors => {
      lazy val tvarDups = duplicates(tvars.get)
        
      ifDuplicates(constructors.map(_._1),
                   "constructor names",
                   tvars match {
                     case None => TypeDef(name, Nil, constructors.toMap)
                     case Some(ts) => TypeDef(name, ts, constructors.toMap)
                   },
                   if (tvars.isDefined && tvarDups.nonEmpty) {
                     Some("duplicate generic types in typevars")
                   } else {
                     None
                   })
    }
  }

  lazy val constDef: Parser[(ConsLabel, Type)] = tyname ~ tpe ^^ {
    case tname ~ t => (tname, t)
    } | tyname ^^ { t => (t, UnitT) }

  lazy val tpe: Parser[Type] = (
      "num" ^^^ NumT
    | "bool" ^^^ BoolT
    | "unit" ^^^ UnitT
    | tyvar
    | funType
    | recType
    | ident ~ opt(tyapp) ^^ {
      case t ~ typarams =>
        typarams match {
          case None => TypT(t, Nil)
          case Some(ts) => TypT(t, ts)
        }
    }
    | "(" ~> tpe <~ ")"
  )

  lazy val funType: Parser[Type] =
    opt(tyabs) ~ simpleFunType flatMap {
      case tyvars ~ ft => 
        ifDuplicates(tyvars.getOrElse(Seq()),
                     "generic types in function",
                     tyvars.map(tv => TFunT(tv, ft)).getOrElse(ft))
    }
  
  lazy val simpleFunType: Parser[FunT] = (
    "(" ~ repsep(tpe, ",") ~  ")" ~ "=>" ~ tpe ^^ { case _ ~ args ~ _ ~ _ ~ rtype => FunT(args, rtype) }
    | tpe ~ "=>" ~ tpe ^^ { case argT ~ _ ~ rtype => FunT(Seq(argT), rtype) }
  )

  lazy val recType: Parser[Type] =
    "(|" ~ repsep(varname ~ ":" ~ tpe, ",") ~ "|)" flatMap {
      case _ ~ fields ~ _ => {
        val pairs = fields.map({ case fname ~ _ ~ ftype => (fname, ftype) })
        ifDuplicates(pairs.map(_._1),
                     "record type fields",
                     RcdT(pairs.toMap))
      }
    }
  
  lazy val exp: Parser[Exp] = (
      caseExp
    | not
    | constructor
    | access
    | record
    | rec
    | let
    | ifE
    | call
    | fun
    | binop
    | unit
    | bool
    | num
    | variable
    | "(" ~> exp <~ ")"
  )

  lazy val caseExp: Parser[Exp] = "case" ~ exp ~ "of" ~ (matches+) ^^ {
    case _ ~ scrut ~ _ ~ ms => Match(scrut, ms.toSeq)
  }

  lazy val matches: Parser[(ConsLabel, Var, Exp)] = "|" ~ pattern ~ "=>" ~ exp ^^ {
    case _ ~ pat ~ _ ~ body =>
      (pat._1, pat._2, body)
  }

  lazy val pattern: Parser[(ConsLabel, Var)] = (
      tyname ~ "(" ~ variable ~ ")" ^^ { case t ~ _ ~ v ~ _ => (t, v) }
    | tyname ^^ { case t => (t, Var("_bogusVariable")) }
  )

  lazy val constructor: Parser[Exp] = tyname ~ "!" ~ ident ~ opt(tyapp) ~  "(" ~ exp ~ ")" ^^ {
    case tname ~ _ ~ name ~ tvars ~ _ ~ arg ~ _ => tvars match {
      case None => Construct(tname, name, Nil, arg)      
      case Some(ts) => Construct(tname, name, ts, arg)  
    }} | tyname ~ "!" ~ ident ~ opt(tyapp) ^^ { 
        case t ~ _ ~ name ~ tyvar => tyvar match { 
          case None => Construct(t, name, Nil, Unit())
          case Some(tys) => Construct(t, name, tys, Unit())
        }
    }

  lazy val access: Parser[Exp] = exp ~ "." ~ varname ^^ {
    case recv ~ _ ~ f => Access(recv, f)
  }

  lazy val record: Parser[Exp] = 
    "(|" ~ rep1sep(varname ~ ":" ~ exp, ",") ~ "|)" flatMap {
      case _ ~ fields ~ _ => {
        ifDuplicates(fields.map({ case fname ~ _ ~ _ => fname }),
                     "record fields",
                     Record(fields.map({ case fname ~ _ ~ v => (fname, v) }).toMap))
      }
    }

  lazy val rec: Parser[Exp] = "rec" ~ variable ~ ":" ~ tpe ~ "=" ~ exp ~ "in" ~ exp ^^ {
    case _ ~ v ~ _ ~ t ~ _ ~ e1 ~ _ ~ e2 => Rec(v, t, e1, e2)
  }

  lazy val let: Parser[Exp] = "let" ~ rep1sep(binding, ",") ~ "in" ~ exp ^^ {
    case _ ~ bs ~ _ ~ e => desugarLet(bs, e)
  }

  def desugarLet(xs: List[(Var, Exp)], e: Exp): Exp = xs match {
    case Nil => sys.error("If you see this there is a mistake here")
    case (v, e1) :: Nil => Let(v, e1, e)
    case (v, e1) :: xs =>
      Let(v, e1, desugarLet(xs, e))
  }

  lazy val binding: Parser[(Var, Exp)] = variable ~ "=" ~ exp ^^ {
    case v ~ _ ~ e => (v, e)
  }

  lazy val ifE: Parser[Exp] = "if" ~ exp ~ "then" ~ exp ~ "else" ~ exp ^^ {
    case _ ~ guard ~ _ ~ trueB ~ _ ~ falseB =>
      If(guard, trueB, falseB)
  }

  lazy val call: Parser[Exp] = variable ~ tyapp ~ "(" ~ repsep(exp, ",") ~ ")" ^^ {
    case v ~ typs ~ _ ~ args ~ _ =>
      Call(TApp(v, typs), args)
  } | exp ~ "(" ~ repsep(exp, ",") ~ ")" ^^ {
    case f ~ _ ~ args ~ _ =>
      Call(f, args)
  }

  lazy val fun: Parser[Exp] =
    opt(tyabs) ~ "(" ~ repsep(variable ~ ":" ~ tpe, ",") ~ ")" ~ "=>" ~ exp flatMap {
      case tvs ~ _ ~ args ~ _ ~ _ ~ body => {
        val params = args.map { case l ~ _ ~ t => (l, t) }
        ifDuplicates(params.map(_._1.name),
                     "parameter names",
                     tvs match {
                       case None =>
                         Fun(params, body)
                       case Some(tparams) =>
                         TAbs(tparams, Fun(params, body))
                     })
      }
    }

  lazy val tyapp: Parser[Seq[Type]] =
    "<" ~> rep1sep(tpe, ",") <~ ">"

  lazy val tyabs: Parser[Seq[TVar]] = "[" ~> rep1sep(tyvar, ",") <~ "]"

  lazy val tyvar: Parser[TVar] = "'" ~ ident ^^ { case _ ~ t => TVar(t) }

  lazy val variable: Parser[Var] = varname ^^ (v => Var(v))

  lazy val binop: Parser[Exp] = (
      multOrDiv
    | addOrSub
    | compare
    | logical
    | not
  )

  lazy val addOrSub: Parser[Exp] = (
      exp ~ "+" ~ exp ^^ { case e1 ~ _ ~ e2 => Call(Plus, List(e1, e2)) }
    | exp ~ "-" ~ exp ^^ { case e1 ~ _ ~ e2 => Call(Minus, List(e1, e2)) }
  )

  lazy val multOrDiv: Parser[Exp] = (
      exp ~ "*" ~ exp ^^ { case e1 ~ _ ~ e2 => Call(Times, List(e1, e2)) }
    | exp ~ "/" ~ exp ^^ { case e1 ~ _ ~ e2 => Call(Divide, List(e1, e2)) }
  )

  lazy val compare: Parser[Exp] = (
      exp ~ "<" ~ exp ^^ { case e1 ~ _ ~ e2 => Call(LT, List(e1, e2)) }
    | exp ~ "=" ~ exp ^^ { case e1 ~ _ ~ e2 => Call(EQ, List(e1, e2)) }
  )

  lazy val logical: Parser[Exp] = (
      exp ~ "&&" ~ exp ^^ { case e1 ~ _ ~ e2 => Call(And, List(e1, e2)) }
    | exp ~ "||" ~ exp ^^ { case e1 ~ _ ~ e2 => Call(Or, List(e1, e2)) }
  )

  lazy val not: Parser[Exp] = "!" ~ exp ^^ { case _ ~ e => Call(Not, e :: Nil) }

  lazy val varname: Parser[String] = lowerCaseIdent

  lazy val tyname: Parser[String] = upperCaseIdent

  lazy val num: Parser[Num] = numericLit ^^ {
    case i => Num(i.toInt)
  }

  lazy val bool: Parser[Bool] = ("true" ^^^ Bool(true)) | ("false" ^^^ Bool(false))

  lazy val unit: Parser[Exp] = "nil" ^^^ Unit()

  lazy val lowerCaseIdent: Parser[String] = ident flatMap { i =>
    val isUpper = (c: Char) => ('a' to 'z').contains(c)
    if (isUpper(i.charAt(0)))
      success(i)
    else
      failure("unexpected uppercase letter")
  }

  lazy val upperCaseIdent: Parser[String] = ident flatMap { i =>
    val isUpper = (c: Char) => ('A' to 'Z').contains(c)
    if (isUpper(i.charAt(0)))
      success(i)
    else
      failure("unexpected lowercase letter")
  }
}


object Pretty {
  def prettySyntax(p: Program): String = p match {
    case Program(tdefs, exp) =>
      tdefs.map(prettyTypeDef(_)).mkString("\n\n") + "\n\n" + prettyExp(exp)
  }

  def prettyTypeDef(td: TypeDef): String = td match {
    case TypeDef(name, tyvars, cons) =>
      val pcons = prettyCons(cons)
      s"type $name = $pcons"
  }

  def prettyCons(cons: Map[ConsLabel, Type]) = {
    val pcons = cons.toList.map { case (x, t) => s"$x ${prettyType(t)}" }
    pcons.mkString("\n\t| ")
  }

  def prettyExp(e: Exp): String = e match {
    case Var(name) => name
    case Num(n) => n.toString
    case Bool(b) => b.toString
    case u: Unit => "nil"
    case Fun(params, body) =>
      val pparams = params.map {
        case (v, t) => s"${prettyExp(v)}: ${prettyType(t)}"
      }.mkString(", ")
      s"(($pparams) => ${prettyExp(body)})"
    case Call(fun, args) =>
      val pargs = args.map(prettyExp).mkString(", ")
      s"${prettyExp(fun)}($pargs)"
    case If(e1, e2, e3) =>
      s"if ${prettyExp(e1)} then ${prettyExp(e2)} else ${prettyExp(e3)}"
    case Let(x, e1, e2) =>
      s"let ${prettyExp(x)} = ${prettyExp(e1)} \n\tin ${prettyExp(e2)}"
    case Rec(x, typ, e1, e2) =>
      s"rec ${prettyExp(x)} : ${prettyType(typ)} = ${prettyExp(e1)} \n\tin ${prettyExp(e2)}"
    case Record(fields) =>
      val pfields = fields.map { case (f, e) => s"$f: ${prettyExp(e)}" }
      s"(| ${pfields.mkString(", ")} |)"
    case Access(e, field) =>
      s"${prettyExp(e)}.$field"
    case Construct(name, constructor, tyvars, e) =>
      s"$name!$constructor ${prettyExp(e)}"
    case Match(e, cases) =>
      s"case ${prettyExp(e)} of\n${prettyCases(cases)}"
    case b: Builtin => prettyBuiltin(b)
    case TAbs(tyvars, fun) =>
      s"[${tyvars.map(prettyType).mkString(", ")}]${prettyExp(fun)}"
    case t @ TApp(Var(v), tyvars) =>
      s"$v<${tyvars.map(prettyType).mkString(", ")}>"
    case anything => anything.toString
  }

  def prettyCases(cases: Seq[(ConsLabel, Var, Exp)]) = {
    val pcases = cases.map { case (l, v, e) => s"| $l(${prettyExp(v)}) => ${prettyExp(e)}" }
    pcases.mkString("\n")
  }

  def prettyType(t: Type): String = t match {
    case NumT => "num"
    case BoolT => "bool"
    case UnitT => "unit"
    case FunT(params, ret) =>
      val pparams = params.map(prettyType).mkString(", ")
      s"($pparams) => ${prettyType(ret)}"
    case RcdT(fields) =>
      val pfields = fields.map { case (f, t) => s"$f: ${prettyType(t)}" }
      s"(| ${pfields.mkString(", ")} |)"
    case TypT(name, vars) => vars match {
      case Nil => name.toString
      case xs  => s"$name<${xs.map(prettyType).mkString(", ")}>"
    }
    case TFunT(tyvars, funt) =>
      s"[${tyvars.map(prettyType).mkString(", ")}]${prettyType(funt)}"
    case TVar(v) => v.toString
  }

  def prettyBuiltin(b: Builtin): String = b match {
    case Plus => "+"
    case Minus => "-"
    case Times => "*"
    case Divide => "/"
    case LT => "<"
    case EQ => "="
    case And => "&&"
    case Or => "||"
    case Not => "!"
  }
}
