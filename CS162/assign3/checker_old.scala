import scala.io._
import cs162.assign3.syntax._
import Aliases._
import scala.io.Source.fromFile

//——————————————————————————————————————————————————————————————————————————————
// Main entry point

object Checker {
  type TypeEnv = Map[Var, Type]
  val TypeEnv = Map
  object Illtyped extends Exception

  def main( args:Array[String] ) {
    val filename = args(0)
    val input = fromFile(filename).mkString

    Parsers.program.run(input, filename) match {
      case Left(e) => // parse error
        println(e)

      case Right(program) => // correctly parsed
        Checker(program.typedefs).getType(program.e, TypeEnv())
        println("This program is well-typed.")
    }
  }
}

case class Checker(typeDefs: Set[TypeDef]) {
  import Checker.{ TypeEnv, Illtyped }

  // Gets all the constructors associated with a given type name.
  // For example, consider the following typedefs:
  //
  // type Either = Left num | Right bool
  // type Maybe = Some num | None
  //
  // With respect to the above typedefs, `constructors` will return
  // the following for the given example arguments:
  //
  // constructors(NameLabel("Either")) =
  //   Map(ConsLabel("Left") -> NumT, ConsLabel("Right") -> BoolT)
  //
  // constructors(NameLabel("Maybe")) =
  //   Map(ConsLabel("Some") -> NumT, ConsLabel("None") -> UnitT)
  //
  // constructors(NameLabel("Fake")) will throw the Illtyped exception
  //
  def constructors(name: NameLabel): Map[ConsLabel, Type] =
    typeDefs.
      find(_.name == name).
      map(_.constructors).
      getOrElse(throw Illtyped)

  // Type-check the given expression using the given type environment;
  // if successful returns the expression's type, if not successful
  // throws an Illtyped exception.
  def getType( e:Exp, env:TypeEnv ): Type =
    e match {
      // variables
      case x:Var => {
		env.getOrElse(x, throw Illtyped)
	  } // FILL ME IN

      // numeric literals
      case _:Num => NumT
	  // FILL ME IN

      // boolean literals
      case _:Bool => BoolT // FILL ME IN

      // `nil` - the literal for unit
      case _: NilExp => UnitT // FILL ME IN

      // builtin arithmetic operators
      case Plus | Minus | Times | Divide => FunT(Seq(NumT, NumT), NumT) // FILL ME IN

      // builtin relational operators
      case LT | EQ => FunT(Seq(NumT, NumT), BoolT) // FILL ME IN

      // builtin logical operators
      case And | Or => FunT(Seq(BoolT, BoolT), BoolT) // FILL ME IN

      // builtin logical operators
      case Not => FunT(Seq(BoolT), BoolT) // FILL ME IN

      // function creation
      case Fun(params, body) => {
		val paramMap = params.toMap
		val retType = getType(body, env ++ paramMap)
		FunT(params map (_._2), retType)
	  } // FILL ME IN

      // function call
      case Call(fun, args) => {
		getType(fun, env) match {
			case FunT(targs, tret) => {
				if (targs != args.map(getType(_, env))) throw Illtyped
				else tret
			}
			case _ => throw Illtyped
		}
	  } // FILL ME IN

      // conditionals 
      case If(e1, e2, e3) => {
		val t1 = getType(e1, env)
		if (t1 != BoolT) throw Illtyped
		val t2 = getType(e2, env)
		val t3 = getType(e3, env)
		if (t2 != t3) throw Illtyped
		t2
	  } // FILL ME IN

      // let binding
      case Let(x, e1, e2) => {
		val t1 = getType(e1, env)
		val t2 = getType(e2, env + (x -> t1))
		t2
	  }// FILL ME IN

      // recursive binding
      case Rec(x, t1, e1, e2) => {
		val tcheck1 = getType(e1, env + (x -> t1))
		if (tcheck1 != t1) {
			throw Illtyped
		}
		val t2 = getType(e2, env + (x -> t1))
		t2
	  } // FILL ME IN

      // record literals
      case Record(fields) => {
		RcdT(fields map {case(k,v) => (k, getType(v, env))})
	  } // FILL ME IN

      // record access
      case Access(e, field) => {
		getType(e, env) match {
			case RcdT(m: Map[String, Type]) => {
				val t1 = m.getOrElse(field, throw Illtyped)
				t1
			}
			case _ => throw Illtyped
		}
	  } // FILL ME IN

      // constructor use
      case Construct(name, constructor, e) => {
		val consMap = constructors(name)
		val t1 = consMap.getOrElse(constructor, throw Illtyped)
		if (t1 != getType(e, env)) throw Illtyped
		TypT(name)
	  } // FILL ME IN

      // pattern matching (case ... of ...)
	  // requirement: number of constructors needs to match number of cases
		// names also need to match names of cases
      case Match(e, cases) => {
		//first, find the type whose constructors we want to use
		val t1 = getType(e, env)
		t1 match {
			case TypT(name) => {
				//make sure 1 to 1 relationship between cases and constructors
				val consSeq = constructors(name).toSeq
				if (consSeq.size != cases.size) throw Illtyped
				val nameMap = (cases map (_ match {case (a, b, c) => (a, (b,c))})).toMap
				val caseTypes = consSeq.map((cons) => {
					val curCase = nameMap.getOrElse(cons._1, throw Illtyped)
					getType(curCase._2, env + (curCase._1 -> cons._2))
				})
				//make sure each case returns the same type and return that type
				caseTypes.foldRight(caseTypes.head)((a, b) => {
					if (a != b) throw Illtyped
					a
				})
			}
			case _ => throw Illtyped
		}
	  } // FILL ME IN
    }
}
