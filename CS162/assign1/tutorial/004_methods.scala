// From `class.scala`, we  learned how to define new classes.
// Now let's put some methods on those classes.  For starters,
// let's define a method that's not particularly useful:

class Useless {
  def pointless() {}
  def anotherPointLess(x: Int) {}
}

// As shown, methods are introduced via the `def` reserved word.
// Methods can be called in the usual way:

val useless = new Useless
useless.pointless()

// ...but that's not all!  We can also call them like so:

useless.pointless // Look Mom: no parentheses!

// This does the exact same thing as the version with
// parentheses.  Why have both notations?  Well, programmers
// use this as a flag to other programmers to indicate if the
// called method is pure or not.  It is assumed that if you do:

useless.pointless()

// ...`pointless` is impure, whereas if you do:

useless.pointless

// ...`pointless` is pure.  This is not enforced by the compiler
// (it's actually quite difficult in general to check this), but
// still serves as a useful flag to other programmers.  In code
// that's mostly pure, `()` sticks out like a sore thumb, and
// is a concise way to say "This may violate some assumptions you're
// making about the program's state"

// There is a final syntactical variant on method calls. One is able
// to omit the `.` and parenthesis if nothing else follows on the
// line.

useless pointless

// It is also possible to provide a single argument with this syntax.

useless anotherPointLess 1

// This is only a sytlistic change, and does not effect the semantics
// of the method call. Often times Scala programmers will use this
// feature in constructing embedded Domain Specific Languages
// that have a certain appearance. For example this feature
// was used to emulate the syntax of the BASIC programming
// language inside of Scala.

// `pointless` is still a pretty useless method.  Right now it returns
// `Unit`, AKA `()`, AKA `void`, which doesn't let us do much.
// Let's at least make it return something useful, so it will stop
// embarrasing itself:

class SlighlyMoreUseful {
  def lessPointless(): Int = 42
}

// Here, we use `=` to specify where the body of the method is.
// Methods implicitly return whatever their bodies evaluate to.

// So what if we have something more complex in a method body that
// simply demands multiple statements, as with a variable introduction?
// For that we need braces, like so:

class GettingComplex {
  def somewhatComplex(): Int = {
    val temp = 42 // no semicolon required - the compiler infers it!  (seriously)
    56 * temp
  }
}

// Now `somewhatComplex` assigns `42` to `temp`, and finally returns
// the result of `56 * temp`.

// Let's add some method parameters into the mix:

class WithParams {
  def withParams(x: Int, y: Int): Int = x + y
}

// Now we have a method that takes some parameters and returns the
// result of `x + y`.  Let's add some state into the mix:

class WithState(val state: Int) {
  def withState(x: Int, y: Int): Int = x + y + state
}

// Now we can make instances of WithState like so:

new WithState(78)

// Let's test out what we can do with this.  Replace `???` below to
// make the assertions hold:

val obj1 = new WithState(1)
val obj2 = new WithState(20)

assert(obj1.withState(1, 2) == 4)
assert(obj2.withState(5, 10) == 35)
