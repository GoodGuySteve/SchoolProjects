package cs162.linlapel

//——————————————————————————————————————————————————————————————————————————————
// Abstract syntax

// x ∈ Variable  f ∈ FunctionSymbol  p ∈ PredicateSymbol
//    t ∈ Term    ::= x | f(t⋯)
//    g ∈ Goal    ::= p(t⋯) | g1 ⊗ g2 | g1 & g2 | g1 ⊕ g2 | a ⊸ g | a ⊃ g
//                  | !g | ∃x⋯.g | output
//    a ∈ Assume  ::= p(t⋯) | p(t⋯) ⟜ g | &a⋯ | ∀x⋯.a
// prog ∈ Program ::= a⋯ ⊢ g

sealed abstract class Term
case class Var(name: String) extends Term
case class Function(name: String, args: Seq[Term]) extends Term

// the Output goal isn't part of the concrete syntax; the parser
// inserts it at the end of the initial query to provide the runtime a
// signal indicating the end of a proof. thus Output will always be
// the last goal of a successful proof.
sealed abstract class Goal
case class Predicate(name: String, args: Seq[Term]) extends Goal
case class MulConjunct(g1: Goal, g2: Goal) extends Goal
case class AddConjunct(g1: Goal, g2: Goal) extends Goal
case class Disjunct(g1: Goal, g2: Goal) extends Goal
case class LinHypothetical(a: Assume, g: Goal) extends Goal
case class Hypothetical(a: Assume, g: Goal) extends Goal
case class Escape(g: Goal) extends Goal
case class Exists(vars: Set[Var], g: Goal) extends Goal
case class Output() extends Goal

// we'll assume that all facts and clauses are universally quantified,
// and the ones that aren't will just have the empty set of
// variables. we'll also assume that universal quantification is only
// for facts and clauses, not With.
sealed abstract class Assume
case class Fact(vars: Set[Var], pred: Predicate) extends Assume
case class Clause(vars: Set[Var], hd: Predicate, body: Goal) extends Assume
case class With(as: Seq[Assume]) extends Assume

// all of the given program assumptions are nonlinear; only linear
// implications can add linear assumptions.
case class Program(_Γ: Seq[Assume], query: Goal)


//——————————————————————————————————————————————————————————————————————————————
// Values and equivalence classes

// values are ground terms (functions without any variables) where
// "ground" includes logic variables standing for unknown terms
//
// the 'contains' method returns whether or not a value contains a
// given logic variable; it's used for the unification occurs check
//
sealed abstract class Value {
  def contains(x: LogicV): Boolean
}
case class FunV(name: String, args: Seq[Value]) extends Value {
  def contains(x: LogicV) = args exists (_ contains x)
}
case class LogicV(id: Int) extends Value {
  def contains(x: LogicV) = x == this
}

// create unique logic variables using "LogicV()"; this should be the
// only way that logic variables are ever created during execution in
// order to ensure freshness
object LogicV {
  private var counter = 0
  def apply(): LogicV = { counter += 1; LogicV(counter) }
}

// equivalence classes involving logic variables (functions are always
// the set representative if they are present in an equivalence class)
case class Equiv(eq: Map[LogicV, Value]) {
  // look up a value and return its set representative in the
  // equivalence class
  def apply(v: Value): Value = {
	v match {
		case lv @ LogicV(_) => {
			val mapping = eq.getOrElse(lv, lv)
			if (mapping == lv) lv
			else apply(mapping)
		}
		case FunV(name, args) => FunV(name, args map {apply(_)})
	}
  } // FILL ME IN
  
	//helper function for unify
	def isNotIn(lv: LogicV, v: Value): Boolean = {
		val repV = apply(v)
		repV match {
			case LogicV(id) => lv.id != id
			case FunV(_, args) => args.foldLeft(true)((result, arg) => result && isNotIn(lv, apply(arg)))
		}
	}
	
  // unify two values 
  def unify(v1: Value, v2: Value): Option[Equiv] = {
  
	//check if vals are equal before finding rep! (otherwise might add redundant mappings)
	if (v1 == v2) Some(Equiv(eq))
	val rep1 = apply(v1)
	val rep2 = apply(v2)
	if (rep1 == rep2) Some(Equiv(eq))
	rep1 match {
		case lv1 @ LogicV(id1) => rep2 match {
			case lv2 @ LogicV(id2) => {
				if (id1 == id2) Some(Equiv(eq))
				else Some(Equiv(eq + (lv1 -> rep2)))
			}
			case FunV(name2, args2) => {
				//make sure our logic variable is not in the FunV
				if (isNotIn(lv1, rep2)){
					Some(Equiv(eq + (lv1 -> rep2)))
				}
				else None
			}
			case _ => None
		}
		case FunV(name1, args1) => rep2 match {
			case lv2 @ LogicV(id2) => {
				//make sure our logic variable is not in the FunV
				if (isNotIn(lv2, rep1)) {
					Some(Equiv(eq + (lv2 -> rep1)))
				}
				else None
			}
			case FunV(name2, args2) => {
				if ((name1 == name2) && (args1.size == args2.size)) {
					(args1 zip args2).foldLeft[Option[Equiv]](Some(Equiv(eq)))((acc, pair) => acc match {
						case None => None
						case Some(Equiv(newEq)) => Equiv(newEq).unify(pair._1, pair._2)
					})
				}
				else None
			}
			case _ => None
		}
		case _ => None
	}
  
  }// FILL ME IN

  // translate a value into a string, mapping logic variables to
  // functions wherever possible
  def valToString(v: Value): String =
    apply(v) match {
      case LogicV(id) ⇒ "L" + id
      case FunV(name, args) ⇒
        if (args.size == 0) name
        else name + "(" + (args map valToString).mkString(", ") + ")"
    }
}


//——————————————————————————————————————————————————————————————————————————————
// Interpreter

import scala.io.Source.fromFile
import scala.language.postfixOps

// the main entry point of the interpreter
object Interpreter {
  // environments map variables to logic variables
  type Env = Map[Var, LogicV]

  // Stacker mixes closures with certain steps that the interpreter will
  // need to perform to handle the linear assumption passing style
  sealed abstract class Stacker

  // closures contain a goal, its environment, and the assumptions
  // that can be used to prove the goal. the assumptions are only the
  // nonlinear assumptions Γ; the linear assumptions Δ are treated
  // separately.
  case class Closure(g: Goal, env: Env, _Γ: Seq[Assume]) extends Stacker

  // these classes represent the extra steps that need to be performed
  // for handling linear assumption passing style in the additive
  // conjunction (AC), escape (ESC), and linear implication (LH) cases.
  case class AC_Step1(_Δ: Seq[Assume]) extends Stacker
  case class AC_Step2(_Δ: Seq[Assume]) extends Stacker
  case class ESC_Step(_Δ: Seq[Assume]) extends Stacker
  case class LH_Step(a: Assume, num: Int) extends Stacker

  // entry point
  def main(args: Array[String]) {
    if (args.length < 1) sys.error("need to provide a filename")
    val filename = args(0)
    val input = if (filename == "-") scala.io.Source.stdin.mkString
                else fromFile(filename).mkString

    val prog: Program = LLPLParser.program.run(input, filename) match {
      case Left(error) ⇒ sys.error("parse error: " + error)
      case Right(p) ⇒ p
    }

    eval(
      Seq(Closure(prog.query, Map(), prog._Γ)),
      Seq(),
      Equiv(Map())
    )
  }

  // finds all satisfying solutions to the given stack of Stackers using
  // the given linear assumptions and equivalence relation (where the
  // stack is represented by a sequence, with the head of the sequence
  // being the top of the stack)
  def eval(goals: Seq[Stacker], _Δ: Seq[Assume], eq: Equiv): Unit =
    if (goals nonEmpty) goals.head match {
      // atomic predicate goal
      case goalClo @ Closure(_: Predicate, _, _Γ) ⇒ {
		_Γ map {
			_ match {
				case Fact(vars, pred) => {
					val newClo = Closure(pred, (vars map {_ -> LogicV()}).toMap, _Γ)
					matchPredicates(newClo, goalClo, eq) match {
						case Some(newEq) => eval(goals.tail, _Δ, newEq)
						case None => {}
					}
				}
				case Clause(vars, hd, body) => {
					val newVMap = (vars map {_ -> LogicV()}).toMap
					val newClo = Closure(hd, newVMap, _Γ)
					matchPredicates(newClo, goalClo, eq) match {
						case Some(newEq) => eval(Closure(body, newVMap, _Γ) +: goals.tail, _Δ, newEq)
						case None => {}
					}
				}
				case With(as) => {} //shouldn't have a With statement in nonlinear assumptions
			}
		}
		_Δ map {
			(a) => a match {
				case Fact(vars, pred) => {
					val newClo = Closure(pred, (vars map {_ -> LogicV()}).toMap, _Γ)
					matchPredicates(newClo, goalClo, eq) match {
						case Some(newEq) => eval(goals.tail, _Δ diff Seq(a), newEq)
						case None => {}
					}
				}
				case Clause(vars, hd, body) => {
					val newVMap = (vars map {_ -> LogicV()}).toMap
					val newClo = Closure(hd, newVMap, _Γ)
					matchPredicates(newClo, goalClo, eq) match {
						case Some(newEq) => eval(Closure(body, newVMap, _Γ) +: goals.tail, _Δ diff Seq(a), newEq)
						case None => {}
					}
				}
				case w @ With(as) => as map {
					case Fact(vars, pred) => {
						val newClo = Closure(pred, (vars map {_ -> LogicV()}).toMap, _Γ)
						matchPredicates(newClo, goalClo, eq) match {
							case Some(newEq) => eval(goals.tail, _Δ diff Seq(w), newEq)
							case None => {}
						}
					}
					case Clause(vars, hd, body) => {
						val newVMap = (vars map {_ -> LogicV()}).toMap
						val newClo = Closure(hd, newVMap, _Γ)
						matchPredicates(newClo, goalClo, eq) match {
							case Some(newEq) => eval(Closure(body, newVMap, _Γ) +: goals.tail, _Δ diff Seq(w), newEq)
							case None => {}
						}
					}
				}
				case _ => {}
			}
		}
	  }
      // FILL ME IN

      // multiplicative conjunction goal
      case Closure(MulConjunct(g1, g2), env, _Γ) ⇒ {
		eval(Closure(g1, env, _Γ) +: Closure(g2, env, _Γ) +: goals.tail, _Δ, eq)
	  }
      // FILL ME IN

      // additive conjunction goal; we insert AC_Step1 in-between
      // proving g1 and g2 to handle linear assumption passing style.
      case Closure(AddConjunct(g1, g2), env, _Γ) ⇒ {
		eval(Closure(g1, env, _Γ) +: AC_Step1(_Δ) +: Closure(g2, env, _Γ) +: goals.tail, _Δ, eq)
	  }
      // FILL ME IN

      // disjunct goal
      case Closure(Disjunct(g1, g2), env, _Γ) ⇒ {
	    eval(Closure(g1, env, _Γ) +: goals.tail, _Δ, eq)
		eval(Closure(g2, env, _Γ) +: goals.tail, _Δ, eq)
	  }
      // FILL ME IN

      // linear hypothetical implication goal; we insert LH_Step after
      // proving g to handle linear assumption passing style.
      case Closure(LinHypothetical(a, g), env, _Γ) ⇒ {
		val num = _Δ.count((b) => a == b)
		eval(Closure(g, env, _Γ) +: LH_Step(a, num) +: goals.tail, a +: _Δ , eq)
	  }
      // FILL ME IN

      // classical hypothetical implication goal
      case Closure(Hypothetical(a, g), env, _Γ) ⇒ {
		eval(Closure(g, env, a +: _Γ) +: goals.tail, _Δ, eq)
	  }
      // FILL ME IN

      // escape goal; we insert ESC_Step after proving g to handle
      // linear assumption passing style.
      case Closure(Escape(g), env, _Γ) ⇒ {
		eval(Closure(g, env, _Γ) +: ESC_Step(_Δ) +: goals.tail, Seq[Assume](), eq)
	  }
      // FILL ME IN

      // existential quantification goal
      case Closure(Exists(vars, g), env, _Γ) ⇒ {
		val newEnv = vars.foldLeft(env)((tmpEnv, curVar) => tmpEnv + (curVar -> LogicV()))
		eval(Closure(g, newEnv, _Γ) +: goals.tail, _Δ, eq)
	  }
      // FILL ME IN

      // output goal
      case Closure(_:Output, env, _Γ) ⇒
        if (_Δ isEmpty) {
          if (env nonEmpty) env map {
            case (x, v) ⇒ println(x.name + " ↦ " + eq.valToString(v))
          }
          else println("true")
          println("========")
        }

      // first step of handling linear assumption passing style for
      // additive conjunction goals g1 & g2: we've just proven g1 and
      // gotten the new linear assumptions _Δ, now we want to save _Δ
      // and prove g2 using the original linear assumptions _Δ1, being
      // sure to take AC_Step2 when we're done proving g2.
      case AC_Step1(_Δ1) ⇒ {
		(goals.tail).head match {
			case myclo:Closure => {
				eval(myclo +: AC_Step2(_Δ) +: (goals.tail).tail, _Δ1, eq)
			}
			case _ => {}
		}
	  }
      // FILL ME IN

      // second step of handling linear assumption passing style for
      // additive conjunction goals g1 & g2: we've proven both g1
      // (getting new linear assumptions _Δ1) and g2 (getting new
      // linear assumptions _Δ); we need to check and make sure they
      // are the same new linear assumptions, i.e., _Δ == _Δ1.
      case AC_Step2(_Δ1) ⇒ {
		if (_Δ1 == _Δ) {
			eval(goals.tail, _Δ, eq)
		}
	  }
      // FILL ME IN

      // handling linear assumption passing style for escape goals !g;
      // we need to restore the linear assumptions that we removed
      // when proving the escaped goal g.
      case ESC_Step(_Δ1) ⇒ {
		eval(goals.tail, _Δ1, eq)
	  }
      // FILL ME IN

      // handling linear assumption passing style for linear
      // hypothetical goals a ⊸ g; we need to check to be sure that
      // the added linear assumption a was used while proving g.
      case LH_Step(a, num) ⇒ {
		val newnum = _Δ.count((b) => a == b)
		if (newnum <= num) {
			eval(goals.tail, _Δ, eq)
		}
	  }
      // FILL ME IN
    }

  // HELPER FUNCTION USED BY THE "ATOMIC PREDICATE GOAL" CASE ABOVE
  //
  // given an "assumption closure" for a predicate coming from a fact
  // or the head of a clause (aClo) and a "goal closure" for the
  // current goal (gClo) and the current equivalence relation (eq),
  // try to match up the assumption and the goal, potentially creating
  // a new equivalence relation in the process
  //
  // this will be the method by which we determine whether a given
  // assumption can help us prove a given atomic predicate goal
    def matchPredicates(aClo: Closure, gClo: Closure, eq: Equiv): Option[Equiv] = {
	    val pred1 = aClo.g
		val pred2 = gClo.g
		(pred1, pred2) match {
			case (Predicate(name1, args1), Predicate(name2, args2)) => {
				if (name1 == name2 && args1.size == args2.size) {
					val logicMap = ((args1 map {termToValue(_, aClo.env)}) zip (args2 map {termToValue(_, gClo.env)}))
					logicMap.foldLeft[Option[Equiv]](Some(eq))((acc, pair) => 
					acc match {
						case Some(middleEq) => middleEq.unify(pair._1, pair._2)
						case None => None
					})
				}
				else None
			}
			case _ => None
		}
  } // FILL ME IN


  // HELPER FUNCTION USED BY "matchPredicates"
  //
  // transform a term (i.e., variable or function on terms) into a
  // value (i.e., logic variable or function on values)
  def termToValue(t: Term, env: Env): Value =
    t match {
      case x:Var ⇒ env(x)
      case Function(name, args) ⇒ FunV(name, args map (termToValue(_, env)))
    }
}


//——————————————————————————————————————————————————————————————————————————————
// Parser
//
// — see the test programs test〈X〉.llpl for examples of concrete syntax.

import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical
import scala.language.implicitConversions

object LLPLParser extends StdTokenParsers with PackratParsers {
  type Parser[T] = PackratParser[T]
  type Error = String
  type Tokens = StdLexical

  val lexical = new StdLexical

  lexical.delimiters ++= Seq(".", "(", ")", ":-", ",", ";", "{", "}", "?", "=>", "+", "-o", "!", "&")
  lexical.reserved ++= Seq("output")

  class Runnable[A](val xs: Parser[A]) {
    def run(stream: String, filename: String): Either[Error, A] = {
      val commentFree = stream.replaceAll("--.*", "")
      val tokens = new lexical.Scanner(commentFree)

      phrase(xs)(tokens) match {
        case e: NoSuccess    ⇒ Left(e.toString)
        case Success(res, _) ⇒ {/*println(res);*/ Right(res)}
      }
    }
  }

  implicit def parserToRunnable[A](p: Parser[A]): Runnable[A] = new Runnable(p)

  lazy val program: Parser[Program] =
    (assumption*) ~ "?" ~ goal ~ "." ^^ {
      case fcs ~ "?" ~ g ~ "." ⇒ {
        val newg = g match {
          case Exists(vars, goal) => Exists(vars, MulConjunct(goal, Output()))
          case x => MulConjunct(x, Output())
        }
        //println(PrettyPrint.progToString(Program(fcs, newg)))
        Program(fcs, newg)
      }
    }

  lazy val assumption: Parser[Assume] =
    fact | clause | witha

  lazy val fact: Parser[Fact] =
    ( vars ~ predicate ~ "." ^^ { case v ~ p ~ "." ⇒ Fact(v, p) } |
      predicate ~ "." ^^ { case p ~ "." ⇒ Fact(Set(), p) }
    )

  lazy val clause: Parser[Clause] =
    ( vars ~ predicate ~ ":-" ~ goal ~ "." ^^ {
        case v ~ p ~ ":-" ~ g ~ "." ⇒ Clause(v, p, g) } |
      predicate ~ ":-" ~ goal ~ "." ^^ {
        case p ~ ":-" ~ g ~ "." ⇒ Clause(Set(), p, g)
      }
    )

  lazy val assumptionNoDot: Parser[Assume] =
    clauseNoDot | factNoDot | "(" ~> assumptionNoDot <~ ")" | withaNoDot

  lazy val witha: Parser[With] = 
    "&" ~> "(" ~> repsep(assumptionNoDot, ",") <~ ")" <~ "." ^^ {case as => With(as)}

  lazy val withaNoDot: Parser[With] = 
    "&" ~> "(" ~> repsep(assumptionNoDot, ",") <~ ")" ^^ {case as => With(as)}

  lazy val factNoDot: Parser[Fact] =
    ( vars ~ predicate ^^ { case v ~ p ⇒ Fact(v, p) } |
      predicate ^^ { case p ⇒ Fact(Set(), p) })

  lazy val clauseNoDot: Parser[Clause] =
    ( vars ~ predicate ~ ":-" ~ goal ^^ {
        case v ~ p ~ ":-" ~ g ⇒ Clause(v, p, g) } |
      predicate ~ ":-" ~ goal ^^ {
        case p ~ ":-" ~ g ⇒ Clause(Set(), p, g) }
    )

  lazy val vars: Parser[Set[Var]] =
    "{" ~> repsep(upperid, ",") <~ "}" ^^ {
      case vs ⇒ vs.map(v ⇒ Var(v)).toSet
    }

  lazy val goal: Parser[Goal] =
    mulconjunct | addconjunct | disjunct | hypothetical | linhypothetical | escape /*| output*/ | predicate | exists | parens

  lazy val parens: Parser[Goal] =
    "(" ~> goal <~ ")"

  lazy val predicate: Parser[Predicate] =
    ( ident2 ~ "(" ~ repsep(term, ",") ~ ")" ^^ {
          case id ~ "(" ~ tms ~ ")" ⇒ Predicate(id, tms)
        } |
      ident2 ^^ {id ⇒ Predicate(id, Seq())}
    )

  lazy val mulconjunct: Parser[MulConjunct] =
    goal ~ "," ~ goal ^^ { case g1 ~ "," ~ g2 ⇒ MulConjunct(g1, g2) }

  lazy val addconjunct: Parser[AddConjunct] =
    goal ~ "+" ~ goal ^^ { case g1 ~ "+" ~ g2 ⇒ AddConjunct(g1, g2) }

  lazy val disjunct: Parser[Disjunct] =
    goal ~ ";" ~ goal ^^ { case g1 ~ ";" ~ g2 ⇒ Disjunct(g1, g2) }

  lazy val hypothetical: Parser[Hypothetical] =
    assumptionNoDot ~ "=>" ~ goal ^^ { case a ~ _ ~ g ⇒ Hypothetical(a, g) }

  lazy val linhypothetical: Parser[LinHypothetical] =
    assumptionNoDot ~ "-o" ~ goal ^^ { case a ~ _ ~ g ⇒ LinHypothetical(a, g) }

  lazy val escape: Parser[Escape] =
    "!" ~> goal ^^ {g => Escape(g)}

  lazy val exists: Parser[Exists] =
    vars ~ goal ^^ { case vs ~ g ⇒ Exists(vs, g) }

  // lazy val output: Parser[Output] =
  //   "output" ^^^ Output()

  lazy val term: Parser[Term] =
    function | ident2 ^^ {
      case s ⇒ 
        if (Character.isUpperCase(s.charAt(0))) Var(s) else Function(s, Seq())
    }

  lazy val function: Parser[Function] =
    ident2 ~ "(" ~ repsep(term, ",") ~ ")" ^^ {
      case id ~ "(" ~ tms ~ ")" ⇒ Function(id, tms)
    }

  lazy val ident2: Parser[String] =
    numericLit ~ ident ^^ { case n ~ i ⇒ n + i } | ident | numericLit

  lazy val upperid: Parser[String] =
    ident flatMap { s ⇒ {
      if (Character.isUpperCase(s.charAt(0)) || s.charAt(0) == '_')
        success(s)
      else
        failure("unexpected lowercase letter")
    }}
}


//——————————————————————————————————————————————————————————————————————————————
// Pretty printer for ASTs

object PrettyPrint {
  def progToString(prog: Program): String = {
    val as = (prog._Γ map (assumeToString(_))).mkString("\n")
    val g = goalToString(prog.query)
    as + "\n? " + g
  }

  def assumeToString(a: Assume): String =
    a match {
      case Fact(vs, p) ⇒
        vsToString(vs) + goalToString(p) + "."

      case Clause(vs, hd, body) ⇒
        vsToString(vs) + goalToString(hd) + " ⟜ " + goalToString(body) + "."

      case With(as) ⇒
        as.map(assumeToString(_)).mkString("\n  & ") + "."
    }

  def goalToString(g: Goal): String =
    g match {
      case Predicate(name, args) ⇒
        if (args.length > 0)
          name + "(" + args.map(termToString(_)).mkString(",") + ")"
        else name

      case MulConjunct(g1, g2) ⇒
        "(" + goalToString(g1) + " ⊗ " + goalToString(g2) + ")"

      case AddConjunct(g1, g2) ⇒
        "(" + goalToString(g1) + " & " + goalToString(g2) + ")"

      case Disjunct(g1, g2) ⇒
        "(" + goalToString(g1) + " ⊕ " + goalToString(g2) + ")"

      case LinHypothetical(a, g) ⇒
        "(" + assumeToString(a) + " ⊸ " + goalToString(g) + ")"

      case Hypothetical(a, g) ⇒
        "(" + assumeToString(a) + " ⊃ " + goalToString(g) + ")"

      case Escape(g) ⇒
        "!(" + goalToString(g) + ")"

      case Exists(vs, g) ⇒
        vsToString(vs) + "(" + goalToString(g) + ")"

      case _:Output ⇒
        "output"
    }

  def termToString(t: Term): String =
    t match {
      case Var(name) ⇒ name
      case Function(name, args) ⇒
        if (args.length > 0)
          name + "(" + args.map(termToString(_)).mkString(",") + ")"
        else name
    }

  def vsToString(vs: Set[Var]): String =
    if (vs.size > 0) "{" + vs.map(termToString(_)).mkString(",") + "}"
    else ""
}
