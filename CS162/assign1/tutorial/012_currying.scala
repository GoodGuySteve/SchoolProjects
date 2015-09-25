// In `higher_order_functions.scala`, higher-order functions were introduced.
// This lesson expands upon higher order functions, along with discussing
// a relevant Scala-specific feature.

// Consider the function below:

val add1 = (x: Int, y: Int) => x + y

assert(add1(1, 2) == 3)

// This defines a function that, if given an `Int` `x` and an `Int` `y`, then
// it will return `x + y`.  One can think of this as saying that if
// two `Int`s are provided simultaneously, then I can get an `Int`.

// Here is a thought question: what if I only have one of the two `Int`s
// available?  Why can't I provide one, and then provide the other one
// later?  The answer is that you can, as long as `add` is defined a
// little bit differently:

val add2 =
  (x: Int) => (y: Int) => x + y

assert(add2(1)(2) == 3)

// When provided with one `Int`, `add2` returns a function that takes an
// `Int` and returns an `Int`.  In this way, we return a closure that
// saves that the first `Int` has been given, and now all that is necessary
// is one more `Int`.

// Try it yourself.  Use the same process to implement `mul`:

val mul1 = (x: Int, y: Int) => x * y
val mul2: Int => (Int => Int) = (x: Int) => (y: Int) => x * y
assert(mul1(2, 3) == 6)
assert(mul2(2)(3) == 6)

// This idea of chaining along functions in the above manner is referred
// to as "Currying", so named after one of the people who originally
// worked on it.

// Scala has built-in support for dealing with currying.  For example,
// consider the method below:

def concat1(s1: String, s2: String): String = s1 + s2

assert(concat1("moo", "cow") == "moocow")

// We can define this in a way that can automatically use currying like so:

def concat2(s1: String)(s2: String): String = s1 + s2

assert(concat2("moo")("cow") == "moocow")

// Note the slight change in syntax, both for the method definition and the call.

// Now let's say that we only have one string `s1`, and we want to generate
// a function that will take a string and concatenate it onto the end
// of `s1`.  We can do that like so:

val concatMoo = concat2("moo") _

// The underscore is necessary to tell the compiler that we want to treat this
// as a curried function.  Now we can call it, like so:

assert(concatMoo("cow") == "moocow")
assert(concatMoo("two") == "mootwo")

// Try it yourself.  Define a method named "sum2" that will allow the following
// assertions to pass:

// DEFINE `sum2` HERE
def sum2(x: Int)(y: Int): Int = x + y

assert(sum2(1)(2) == 3)
assert((sum2(1) _)(2) == 3)

