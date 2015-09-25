import scala.io._
import cs162.assign4.syntax._
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

      case Right(program) =>
        Checker(program.typedefs).getType(program.e, TypeEnv())
        println("This program is well-typed")
    }
  }
}

case class Checker(typeDefs: Set[TypeDef]) {
  import Checker.{ TypeEnv, Illtyped }

  // Gets a listing of the constructor names associated with a given
  // type definition. For example, consider the following type
  // definition:
  //
  // type Either['A, 'B] = Left 'A | Right 'B
  //
  // Some example calls to `constructors`, along with return values:
  //
  // constructors("Either") = Set("Left", "Right")
  // constructors("Foo") = a thrown Illtyped exception
  //
  def constructors(name: NameLabel): Set[ConsLabel] =
    typeDefs.
      find(_.name == name).
      map(_.constructors.keySet).
      getOrElse(throw Illtyped)

  // Takes the following parameters:
  // -The name of a user-defined type
  // -The name of a user-defined constructor in that user-defined type
  // -The types which we wish to apply to the constructor
  // Returns the type that is held within the constructor.
  //
  // For example, consider the following type definition:
  //
  // type Either['A, 'B] = Left 'A | Right 'B
  //
  // Some example calls to `constructorType`, along with return values:
  //
  // constructorType("Either", "Left", Seq(NumT, BoolT)) = NumT
  // constructorType("Either", "Right", Seq(NumT, BoolT)) = BoolT
  // constructorType("Either", "Left", Seq(NumT)) = a thrown Illtyped exception
  // constructorType("Either", "Right", Seq(BoolT)) = a thrown Illtyped exception
  // constructorType("Either", "Foo", Seq(UnitT)) = a thrown Illtyped exception
  // constructorType("Bar", "Left", Seq(UnitT)) = a thrown Illtyped exception
  //
  def constructorType(name: NameLabel, constructor: ConsLabel, types: Seq[Type]): Type = 
    (for {
      td <- typeDefs.find(_.name == name)
      rawType <- td.constructors.get(constructor)
      if (types.size == td.tvars.size)
    } yield replace(rawType, td.tvars.zip(types).toMap)).getOrElse(throw Illtyped)

  // Given a type and a mapping of type variables to other types, it
  // will recursively replace the type variables in `t` with the
  // types in `tv2t`, if possible.  If a type variable isn't
  // in `tv2t`, it should simply return the original type.  If a
  // `TFunT` is encountered, then whatever type variables it defines
  // (the first parameter in the `TFunT`) should overwrite whatever is in
  // `tv2t` right before a recursive `replace` call.  In other words,
  // type variables can shadow other type variables.
  //
  def replace( t:Type, tv2t:Map[TVar, Type] ): Type =
    t match {
      case NumT => NumT //| BoolT | UnitT => ??? // FILL ME IN
	  
	  case BoolT => BoolT
	  
	  case UnitT => UnitT

      case FunT(params, ret) => FunT(params.map(replace(_, tv2t)), replace(ret, tv2t)) // FILL ME IN

      case RcdT(fields) => RcdT(fields map {case (label, typ) => (label -> replace(typ, tv2t))}) // FILL ME IN

      case TypT(name, typs) => {
		val typSeq = typs.map(replace(_, tv2t))
		//iterate through all constructors to get types
		TypT(name, typSeq)
	  } // FILL ME IN

      case tv:TVar => tv2t.getOrElse(tv, tv) // FILL ME IN

	  // (foldLeft) and do a replace on funt (after removing given types)
      case TFunT(tvars, funt) => {
		val newtv2t = tvars.foldLeft(tv2t)((b, a) => //b is a Map, a is a TVAR
			b - a)
		replace(funt, newtv2t)
	  } // FILL ME IN
    }

  // HINT - the bulk of this remains unchanged from the previous assignment.
  // Feel free to copy and paste code from your last submission into here.
  def getType( e:Exp, env:TypeEnv ): Type =
    e match {
      case x:Var => env.getOrElse(x, throw Illtyped) // FILL ME IN

      case _:Num => NumT // FILL ME IN

      case _:Bool => BoolT // FILL ME IN

      case _:Unit => UnitT // FILL ME IN

      case Plus | Minus | Times | Divide => FunT(Seq(NumT, NumT), NumT) // FILL ME IN

      case LT | EQ => FunT(Seq(NumT, NumT), BoolT) // FILL ME IN

      case And | Or => FunT(Seq(BoolT, BoolT), BoolT) // FILL ME IN

      case Not => FunT(Seq(BoolT), BoolT) // FILL ME IN

      case Fun(params, body) => {
		val paramMap = params.toMap
		val retType = getType(body, env ++ paramMap)
		FunT(params map (_._2), retType)
	  }  // FILL ME IN

      case Call(fun, args) => {
		getType(fun, env) match {
			case FunT(subargs, subret) => {
				if (subargs != args.map(getType(_, env))) throw Illtyped
				else subret
			}
			case _ => throw Illtyped
		}
	  } // FILL ME IN

      case If(e1, e2, e3) => {
		val t1 = getType(e1, env)
		if (t1 != BoolT) throw Illtyped
		val t2 = getType(e2, env)
		val t3 = getType(e3, env)
		if (t2 != t3) throw Illtyped
		t2
	  } // FILL ME IN

      case Let(x, e1, e2) => {
		val t1 = getType(e1, env)
		val t2 = getType(e2, env + (x -> t1))
		t2
	  } // FILL ME IN

      case Rec(x, t1, e1, e2) => {
		val tcheck1 = getType(e1, env + (x -> t1))
		if (tcheck1 != t1) {
			throw Illtyped
		}
		val t2 = getType(e2, env + (x -> t1))
		t2
	  } // FILL ME IN

      case Record(fields) => {
		RcdT(fields map {case(k,v) => (k, getType(v, env))})
	  }  // FILL ME IN

      case Access(e, field) => {
		getType(e, env) match {
			case RcdT(m: Map[String, Type]) => {
				val t1 = m.getOrElse(field, throw Illtyped)
				t1
			}
			case _ => throw Illtyped
		}
	  } // FILL ME IN

      case c @ Construct(name, constructor, typs, e) => {
		val t1 = constructorType(name, constructor, typs)
		if (t1 != getType(e, env)) throw Illtyped
		TypT(name, typs)
	  } // FILL ME IN

      case Match(e, cases) => { //cases is type Seq[(ConsLabel, Var, Exp)]
		val t1 = getType(e, env)
		t1 match {
			case TypT(name, typs) =>  {
				//make sure 1 to 1 relationship between cases and constructors
				val ctors = constructors(name).toSeq
				if (ctors.size != cases.size) throw Illtyped
				val nameMap = (cases map (_ match {case (a, b, c) => (a, (b,c))})).toMap
				val caseTypes = ctors.map((cons) => {
					val curCase = nameMap.getOrElse(cons, throw Illtyped)
					val curType = constructorType(name, cons, typs)
					//TODO
					getType(curCase._2, env + (curCase._1 -> curType))			
				})
				//make sure each case returns the same type and return that type
				caseTypes.foldLeft(caseTypes.head)((a, b) => {
					if (a != b) throw Illtyped
					a
				})
			}
			case _ => throw Illtyped
		}
	  
	  } // FILL ME IN

      case TAbs(tvars, fun) => {
		val t1 = getType(fun, env)
		t1 match {
			case x: FunT => TFunT(tvars, x)
			case _ => throw Illtyped
		}
	  } // FILL ME IN

      case TApp(e, typs) => {
		val t1 = getType(e, env)
		t1 match {
			case TFunT(tvars, fun) => {
				if (tvars.size != typs.size) throw Illtyped
				//TODO zip tvars and types together in a map and call replace
				replace(fun, (tvars zip typs).toMap)
			}
			case _ => throw Illtyped
		}
	  } // FILL ME IN
    }
}
