// `val` (short for value) introduces a new variable.
// For example, we define a variable named `someInteger` of
// type `Int` below, and assign `7` to it:
val someInteger: Int = 7

// The variable introduced by `val` is considered a runtime
// constant.  That is, we cannot reassign `someInteger` above to
// anything else.  For example, the code below would trigger
// a compiler error `error: reassignment to val`:

// someInteger = 8

// Moreover, because the value can never be changed, the compiler
// enforces that we always assign a value upon introduction.
// That is, we can't say something like:

// val undefined: Int

// ...since this does nothing but introduce a variable that doesn't
// hold, nor can never hold, a value.  (There is an exception for
// classes which is beyond the scope here.)

// Now let's put this to the test.  Using `val`, make the following
// assertion run and hold:

val x = 4
val y = 3
assert(x + y == 7)

// `val` is how variables are introduced.  Yawn.  I get it.  But
// did you know you can do this?:

val noAnnotation = 20 // type annotation, shmamotation

// What gives?  Here, the compiler is able to perform "type inference",
// that is, automatically derive what the type of `noAnnotation` should
// be.  It sees that `20` is of type `Int`, and thus infers that
// `noAnnotation` must also be of type `Int`.  In general, the compiler
// is very good at making these sort of inferences, and it frees the
// programmer from the burden of explicitly writing in the types of
// variable declarations.  That said, the compiler can sometimes be a
// little too good, and infer a more specific type (e.g., `Int`) when you want
// something general (e.g., `Number`).  Here an explicit type annotation
// would be required.

// Try it out.  Replace `???` with something that will let the assertion
// below work:

val something = 73
assert(something == 73)

// Now for a quick bit about `==`.  If you're used to Java, you're
// probably used to `==` meaning to compare any two primitive values.
// On references, `==` denotes reference equality in Java.  As such,
// one of the first Java-isms you learn is that if you want to
// compare objects based on their contents, you must call
// the `equals` method.  That is, if you wanted to compare the
// strings `s1` and `s2`, you'd have to do `s1.equals(s2)`.

// Scala does things a bit differently here.  In Scala, there are no
// primitive types, so this same sort of split that Java does isn't
// possible.  In Scala, `==` denotes value equality for _all types_, not
// just the usual integers, booleans, etc.  This works because Scala
// internally calls `equals` for us on any type that has an `equals`
// method.  For example, we can do:

assert("moo" == "moo")
assert("moo" != "cow")

// ...and it will work based on what common-sense tells us.

// So what if you really want reference equality?  For that purpose,
// Scala supports `eq` and `ne`, so we can say something like:

assert("moo" ne "cow")

// For this class, you should never need to worry about reference
// equality.
