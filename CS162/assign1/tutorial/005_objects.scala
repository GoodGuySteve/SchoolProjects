// Scala is purely object-oriented.  This means everything is an object.
// This is unlike Java, which has both primitive types and static functions, both
// of which escape the object-oriented mentality.  While this makes Scala more
// consistent with how the language behaves, it also can be annoying.  There are
// plenty of times when one needs a helper function somewhere, and the function
// doesn't really belong in any particular existing class.

// To this end, Scala has the `object` reserve word.  `object` tells the compiler
// that some given code acts both as part of a class definition, and as an instance
// of said class.  It quite intentionally blurs the line between class and instance.
// For example, consider the code below:

object MyObject {
  def helper(x: Int): Int = x + 12
}

// Now I can later call:

MyObject.helper(8)

// MyObject refers to both a class definition and a single instance of that class.
// By having `object`, we can introduce what look and behave largely like static
// functions, without actually breaking the purely object-oriented model.

// Try it out yourself.  Define an object below named `NewObject` and a method
// `myMethod` that allows the assertion below to pass:

object NewObject {
	def myMethod(x: Int): Int = x
}

assert(NewObject.myMethod(7) == 7)

// Because `object` is really just defining a class, we can still extend other
// classes here and do other classy things.  For example:

class Base(val x: Int)
object Top1 extends Base(7)
object Top2 extends Base(14)

assert(Top1.x == 7)
assert(Top2.x == 14)
