package cs162.assign3.syntax

import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical
import scala.io.Source.fromFile

//——————————————————————————————————————————————————————————————————————————————
// Abstract Syntax Tree

object Aliases {
  type NameLabel = String
  type ConsLabel = String
  type FldLabel = String
}
import Aliases._

case class Program( typedefs:Set[TypeDef], e:Exp )
case class TypeDef( name:NameLabel, constructors:Map[ConsLabel, Type] )

sealed abstract class Exp
case class Var( name:String ) extends Exp
case class Num( n:BigInt ) extends Exp
case class Bool( b:Boolean ) extends Exp
case class NilExp() extends Exp
case class Fun( params:Seq[(Var, Type)], body:Exp ) extends Exp
case class Call( fun:Exp, args:Seq[Exp] ) extends Exp
case class If( e1:Exp, e2:Exp, e3:Exp ) extends Exp
case class Let( x:Var, e1:Exp, e2:Exp ) extends Exp
case class Rec( x:Var, typ:Type, e1:Exp, e2:Exp ) extends Exp
case class Record( fields:Map[FldLabel, Exp] ) extends Exp
case class Access( e:Exp, field:FldLabel ) extends Exp
case class Construct( name:NameLabel, constructor:ConsLabel, e:Exp ) extends Exp
case class Match( e:Exp, cases: Seq[(ConsLabel, Var, Exp)] ) extends Exp

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
case class TypT( name:NameLabel ) extends Type

//——————————————————————————————————————————————————————————————————————————————
// Parser

// copied from
// http://jim-mcbeath.blogspot.com/2011/07/debugging-scala-parser-combinators.html
trait DebugStandardTokenParsers extends StdTokenParsers {
    class Wrap[+T](name:String,parser:Parser[T]) extends Parser[T] {
        def apply(in: Input): ParseResult[T] = {
            val first = in.first
            val pos = in.pos
            val offset = in.offset
            val t = parser.apply(in)
            // println(name + ".apply for token " + first +
            //   " at position " + pos + " offset " + offset + " returns " + t)
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

  val lexical = new StdLexical

  lexical.delimiters ++= Seq(".", "(", ")", ":", "=", "->", ",", ";", 
    ";;", "|", "=>", "(|", "|)", "+", "-", "*", "/", "&&", "||", "=", "<", "!")

  lexical.reserved ++= Seq("num", "bool", "let", "rec", "in", "type", "case",
    "of", "true", "false", "if", "then", "else", "nil", "unit")

  class Runnable[A](val xs: Parser[A]) {
    def run(stream: String, filename: String): Either[Error, A] = {
      val commentFree = stream.replaceAll("--.*", "")
      val tokens = new lexical.Scanner(commentFree)

      val retval = phrase(xs)(tokens)
      retval match {
        case e: NoSuccess    => Left(e.toString)
        case Success(res, _) => Right(res)
      }
    }
  }

  implicit def parserToRunnable[A](p: Parser[A]): Runnable[A] = new Runnable(p)

  def duplicates[A](as: Seq[A]): Seq[A] =
    as.groupBy(n => n).values.filter(_.tail.nonEmpty).map(_.head).toSeq

  def ifDuplicates[A, B](items: Seq[A], whatFailed: => String, onSuccess: => B): Parser[B] = {
    val dupGroups = duplicates(items)
    if (dupGroups.nonEmpty) {
      failure("duplicate " + whatFailed + ": " + dupGroups.mkString(", "))
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

  lazy val typedef: Parser[TypeDef] = "typedef" !!! "type" ~ tyname ~ "=" ~ rep1sep(constDef, "|") flatMap {
    case _ ~ name ~ _ ~ constructors => ifDuplicates(constructors.map(_._1),
                                                     "constructor names",
                                                     TypeDef(name, constructors.toMap))
  }

  lazy val constDef: Parser[(ConsLabel, Type)] = tyname ~ tpe ^^ {
    case tname ~ t => (tname, t)
    } | tyname ^^ { t => (t, UnitT) }

  lazy val tpe: Parser[Type] = "tpe" !!! (
      "num" ^^^ NumT
    | "bool" ^^^ BoolT
    | "unit" ^^^ UnitT
    | funType
    | recType
    | ("tpe_ident" !!! ident ^^ (t => TypT(t)))
    | "(" ~> tpe <~ ")"
  )
    
  lazy val funType: Parser[Type] = (
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

  lazy val exp: Parser[Exp] = "exp" !!! (
      caseExp
    | constructor
    | access
    | record
    | rec
    | let
    | ifE
    | call
    | fun
    | binop
    | nil
    | bool
    | num
    | variable
    | "(" ~> exp <~ ")"
  )

  lazy val caseExp: Parser[Exp] = "case" ~ exp ~ "of" ~ (matches+) ^^ {
    case _ ~ scrut ~ _ ~ ms => Match(scrut, ms.toSeq)
  }

  lazy val matches: Parser[(ConsLabel, Var, Exp)] = "|" ~ pattern ~ "=>" ~ exp ^^
    {
      case _ ~ pat ~ _ ~ body =>
        (pat._1, pat._2, body)
    }

  lazy val pattern: Parser[(ConsLabel, Var)] = (
      tyname ~ "(" ~ variable ~ ")" ^^ { case t ~ _ ~ v ~ _ => (t, v) }
    | tyname ^^ { case t => (t, Var("_bogusVariable")) }
  )

  lazy val constructor: Parser[Exp] = tyname ~ "!" ~ ident ~ "(" ~ exp ~ ")" ^^ {
    case tname ~ _ ~ name ~ _ ~ arg ~ _ => Construct(tname, name, arg)
  } | tyname ~ "!" ~ ident ^^ { case t ~ _ ~ n => Construct(t, n, NilExp()) }

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

  lazy val call: Parser[Call] = exp ~ "(" ~ repsep(exp, ",") ~ ")" ^^ {
    case f ~ _ ~ args ~ _ => Call(f, args)
  }

  lazy val fun: Parser[Fun] = "fun" !!! "(" ~ repsep(variable ~ ":" ~ tpe, ",") ~ ")" ~ "=>" ~ exp flatMap {
    case _ ~ args ~ _ ~ _ ~ body => {
      val params = args.map { case l ~ _ ~ t => (l, t) }
      ifDuplicates(params.map(_._1.name),
                   "parameter names",
                   Fun(params, body))
    }
  }

  lazy val variable: Parser[Var] = "variable" !!! varname ^^ (v => Var(v))

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

  lazy val nil: Parser[Exp] = "nil" ^^^ NilExp()

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
    case TypeDef(name, cons) =>
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
    case _: NilExp => "()"
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
    case Construct(name, constructor, e) =>
      s"$name!$constructor ${prettyExp(e)}"
    case Match(e, cases) =>
      s"case ${prettyExp(e)} of\n${prettyCases(cases)}"
    case b: Builtin => prettyBuiltin(b)
  }

  def prettyCases(cases: Seq[(ConsLabel, Var, Exp)]) = {
    val pcases = cases.map {
      case (l, v, e) => s"| $l(${prettyExp(v)}) => ${prettyExp(e)}"
    }
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
    case TypT(name) => name.toString
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
