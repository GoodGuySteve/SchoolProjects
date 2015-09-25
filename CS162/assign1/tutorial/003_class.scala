// There are several ways to introduce new types in Scala. We will cover
// each one in the following sections. We begin our discussion with the simplest
// way to introduce a new type, via a type alias.

type MyNewName = Int

// This declares an alias for Int that we can use anywhere we could use a
// type. This type is only different in name, and is identical to Int.
// For example:

val x: Int = 1
val i: MyNewName = x

// In the REPL it is possible to declare a type alias anywhere, but
// in compiled Scala code you must place these definitions inside of
// an object, class, or trait. This limitation while annoying is
// due to the fact that like Java all Scala code must exist inside an object.

// `class` is the word used to define a new class.
// For example, the line below introduces a class named `Empty`.
class Empty

// Below, we make a new instance of Empty.  Note the lack of
// parentheses
new Empty

// `Empty` is not a particularly interesting class.
// It doesn't have any methods, nor does it contain any state.
// Let's make a class with a constructor that takes an integer, in
// an attempt to make things a tad more interesting
class ATadMoreInteresting(intValue: Int)

// This might be a bit different than what you're used to.
// In Scala, all classes have a primary constructor, which is
// merged in with the definition of the class itself.
// Secondary constructors are possible, but they are beyond
// the scope of this tutorial.  The reasoning behind this
// decision is that oftentime classes have only a single
// constructor, and so the above notation saves some typing.

// Now we can make instances of `ATadMoreInteresting`, like
// the following:
new ATadMoreInteresting(7)

// Unfortunately, this is still not very interesting.  The constructor
// takes a parameter, but that's it - the class doesn't really do
// anything or hold anything.  Let's make it hold onto that value
// so we can access it later:
class HoldValue(val value: Int)

// That's it!  By prefixing `val` before the name,
// this tells the compiler to hold onto the value.  We still use the
// same synax to instantiate the class:

new HoldValue(7)

// Now, how do we go about accessing the value held?  If you're
// coming from a Java background, you might be tempted to modify
// the class and add an accessor.  However, Scala does this all
// automatically for you.  How?  Well, it's already done it!
// See below:

val instance = new HoldValue(8)
assert(instance.value == 8)
assert(instance.value + 2 == 10)

// The names of the parameters become the names of the accessors.

// Now you try.  Define a class below named `Foo` that takes a parameter
// `fooValue` of type `Int`.  This definition should make the
// assertion below hold.

class Foo(val fooValue: Int)
assert(new Foo(8).fooValue == 8)

// Now, what if we wanted a constructor that does something more than set
// some internal values?  We can do that to, like so:

class UsefulConstructor(val field: Int) {
  // println is short for `System.out.println`
  println("Field is: " + field)
  val fieldPrime = field + 10
}

// Below, we make an instance of `UsefulConstructor`.  Note that when this
// happens, a string is print out:
val instance3 = new UsefulConstructor(2)

// Plus, there is one more point of interest here.  We declared `fieldPrime`
// in the scope of the class.  Scala sees this and automatically makes
// an accessor for this field.  So now we can do this:

assert(instance3.fieldPrime == 12)

// Try it yourself.  Make an instance of `UsefulConstructor` that would
// allow the assertion below to hold

val instance2: UsefulConstructor = new UsefulConstructor(-5)

assert(instance2.fieldPrime == 5)

// As we noted above you can declare type aliases inside of classes as well,
// like so:
class IHaveAnAlias {
    type Key = Int
}
