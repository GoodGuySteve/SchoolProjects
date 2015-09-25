// At this point we've discussed `class` and `object`, as well as a sort fusion
// of the two with companion objects.  This lesson introduces `case` classes, which
// automatically do a lot of this work for you.

// In `companions_apply.scala`, you saw a companion object defined that had
// an `apply` method on it.  This `apply` method simply returned a new
// instance of the class the `object` was a companion for.  You may have noticed
// that this was extremely repetitive.  If you wanted to make 10 classes
// and give them companion objects, then you'd have to write 10 companion objects
// that did basically the same thing for their respective clases.  Blech.

// To address this issue, along with a bunch of other issues that frequently
// come up with creating classes, Scala has something called a "case class".
// Case classes are just like regular classes, except the compiler automatically
// generates a lot for you.  Just to list some of the things the compiler
// generates:
// -A companion object with `apply` and `unapply` defined
// -A `hashCode` method that's correct with respect to the constructor parameters
// -A `toString` method that displays the name of the class and its contents
// -An `equals` method that's correct with respect to the constructor parameters
// -Accessors
// -A whole lot more that is beyond this course

// In saying "correct with respect to the constructor parameters", I mean that
// these methods only consider the parameters passed to the constructor.  If your
// `equals` definition, for example, was based on something more than just the
// constructor parameters, you would need to define your own `equals` explicitly.

// To see case classes in action, see below:

case class Foo(x: Int)

// That's it!  The code above is equivalent to the following (commented-out because
// it's incomplete)

// object Foo {
//   def apply(x: Int): Foo = new Foo(x)
//   def unapply(...) = ...
// }

// class Foo(val x: Int) {
//   // `override` is needed because we are overriding methods that have already
//   // been defined on `AnyRef`.  `AnyRef`, by the way, is synonymous with Java's
//   // `Object`.
//   override def hashCode(): Int = x

//   override def equals(other: AnyRef): Boolean =
//     if (other.isInstanceOf[Foo])
//       other.asInstanceOf[Foo].x == x
//     else
//       false

//   override def toString(): String =
//     "Foo(" + x + ")"
  
//   // Much more code
// }

// I don't know about you, but personally I prefer the former definition.

// Now try it out yourself.  Define a case class below named `MyCaseClass`
// that will allow the assertions below to hold.

// DEFINE THE CLASS HERE
// NOTE: Since class uses companion object, must use paste mode on interpreter
object MyCaseClass {
	def apply(x: Int): MyCaseClass =
		new MyCaseClass(x)
}

class MyCaseClass(val field: Int) {
	override def hashCode(): Int = field
}

val c1: MyCaseClass = MyCaseClass(7)
val c2: MyCaseClass = MyCaseClass(7)

assert(c1.field == c2.field)
assert(c1.hashCode == c2.hashCode)


// Occasionally, there are situations in which there is only one member of
// a given class.  For these, we can use `case object`, like so:

case object Bar
