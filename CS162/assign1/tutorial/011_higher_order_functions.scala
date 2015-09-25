// This lesson introduces higher-order functions, which are at the
// heart of any functional language.  Higher-order functions
// enable us to define functions _at runtime_ in a safe manner.
// With higher-order functions, it's possible to abstract away
// _whole computations_, not just data.  Higher-order functions
// are a very powerful feature, but are often difficult to grasp
// at first.

// Before trying to explain higher-order functions as a whole, I'm
// going to take a bit of a digression towards sorting algorithms.
// As mentioned before, with a comparison-based sort, the algorithm
// itself doesn't care what exactly it's sorting.  The sorting
// algorithm need only know how to determine which item comes before
// another in a list, and the algorithm does the rest.  In Java and
// in many other languages, this is abstracted away by placing the
// code for comparison in a separate abstract class.  This allows
// us to do things like the following:

trait Item
trait DoesComparison {
  def comesBefore(first: Item, second: Item): Boolean
}

// partially defined sorting function
//
// def doSort(items: Items, comparer: DoesComparison): Items = {
//   ...
//   if (comparer.comesBefore(item1, item2)) {
//     put item1 before item2 in the list
//   } else {
//     don't put item1 before item2 in the list
//   }
// }

case class MyInteger(val x: Int) extends Item
class IntegerComparer extends DoesComparison {
  def comesBefore(first: Item, second: Item): Boolean =
    first match {
      case MyInteger(x) =>
        second match {
          case MyInteger(y) =>
            x < y
          case _ =>
            throw new Exception("Second parameter isn't an integer")
        }
      case _ =>
        throw new Exception("First parameter isn't an integer")
    }
}

// Now we can call the code below:
// doSort(items, new IntegerComparer)

// The code above works, but it is extremely bulky.  We had to explicitly
// define a whole new class, implement a method, and finally create
// an instance of this new class, just to compare some numbers.
// Really, all the sorting algorithm needs to operate is a function
// that takes two `Item`s and returns whether or not the first
// comes before the second.  Higher-order functions allow us to compactly
// say this directly, without all the mess of creating new subclasses.
// To demonstrate, consider the refactored code below:

// partially defined sorting function
//
// def doSort(items: Items, comparer: (Item, Item) => Boolean): Items = {
//   ...
//   if (comparer(item1, item2)) {
//     put item1 before item2 in the list
//   } else {
//     don't put item1 before item2 in the list
//   }
// }

val function: (Item, Item) => Boolean = 
  (first: Item, second: Item) => {
    first match {
      case MyInteger(x) =>
        second match {
          case MyInteger(y) =>
            x < y
          case _ =>
            throw new Exception("Second parameter isn't an integer")
        }
      case _ =>
        throw new Exception("First parameter isn't an integer")
    }
  }
// calling the sorting function
// doSort(items, function)

// Now we don't need to introduce any sort of `DoesComparison` class
// at all, nor any of the boilerplate necessary to extend it
// so we can work with integers.  As shown, the syntax
// `(variableName1: Type1, variableName2: Type2) => function_body`
// ...is used to create higher-order functions.  The type:
// `(Item, Item) => Boolean` is the type of a function that takes
// two `Item`s and returns a `Boolean`.

// Higher-order functions are more than just a way to reduce boilerplate,
// though.  For example, consider the code below:

def makeFunction(x: Int): Int => Int =
  (y: Int) => x + y

val f1 = makeFunction(3)
val f2 = makeFunction(4)

assert(f1(7) == 10)
assert(f2(7) == 11)

// Take some time to look at the above code; there is a lot going
// on in those six short lines, and it's crucial to understanding
// just about everything moving forward.  Going line-by-line:

// Line 1:
// def makeFunction(x: Int): Int => Int =
//
// This is creating a new method named `makeFunction`, which takes
// an `Int` parameter named `x`.  It returns a function that takes
// an `Int` and returns an `Int`.  That is, every time `makeFunction`
// is called, it returns a new function.

// Line 2:
//   (y: Int) => x + y
//
// This is the body of `makeFunction`; `makeFunction` returns whatever
// this evaluates to.  The notation `(y: Int) => ...` is creating a new
// function which takes an `Int`  parameter named `y`.  As for the
// body of the function being returned, it returns whatever the result
// of `x + y` is.  Here is a point of great interest - the function
// returned takes only a `y`, but nonetheless it uses `x`.  From the
// standpoint of this function returned, `x` is what is known as a
// "free variable" - it refers to a variable outside.  In this case,
// `x` is referring to the `x` passed to `makeFunction`.  Because this
// returned function uses `x`, the function itself internally saves the
// value of `x` for safekeeping so it can be used if and when it is
// called.  Functions that hold onto values from their enclosing scope
// are often referred to as "closures", since they "close over" these
// values to be used later.

// Line 3:
// val f1 = makeFunction(3)
//
// This calls `makeFunction` with the value `3`.  When `makeFunction` is
// executed, the variable `x` will hold the value `3`.  When the function
// is returned from `makeFunction`, it will store the fact that `x` is
// equal to 3.

// Line 4:
// val f2 = makeFunction(4)
//
// Very much like line 3, except now the value of `x` is stored as 4.  That
// is, the closures held in `f1` and `f2` both hold different values of `x`.
// These values correspond to the value of `x` when the function was generated.
// The fact that `makeFunction` was later called again in line 4 with
// a different value of `x` than before means nothing to the function
// created before, since the function created before saved the information that
// `x` was 3 when it was called.

// Line 5:
// assert(f1(7) == 10)
//
// The function `f1` is called with the parameter of 7.  Now `y` has a binding.
// Recall for `f1` that it saved the value of `x` as 3, so now it returns
// the result of `3 + 7` (10).  `10 == 10`, so the assertion holds.

// Line 6:
// assert(f2(7) == 11)
//
// The function `f2` is now called with the parameter 7.  The same process for
// line 5 follows, except that `f2` saved that the value of `x` was 4.  It
// returns the result of `4 + 10` (11), allowing the assertion to succeed.

// While this is an admittedly simplistic example, as a thought excercise,
// try to represent this in a language of your choice.  Is it clean, or
// does it get messy?  Is it even possible?

// Now try out some of this yourself.  Get the assertions below to pass:

def doSomething(x: Int, function: Int => String): String = function(x)

assert(doSomething(5, (i: Int) => i.toString) == "5")
assert(doSomething(7, (i: Int) => (i + 5).toString) == "12")

def retSomething(x: Int): Int => Int = 
	(y: Int) => x * y

// these assertions call `retSomething`, and then call the result of `retSomething`
assert(retSomething(5)(5) == 25)
assert(retSomething(1)(1) == 1)
assert(retSomething(1)(0) == 0)
assert(retSomething(0)(1) == 0)
assert(retSomething(5)(1) == 5)
assert(retSomething(2)(3) == 6)

// One last bit that is specific to higher-order functions in Scala.
// As stated before, higher-order functions are the backbone of
// functional programming, so code oftentimes uses lots of them.
// In fact, higher-order functions are so frequent that the syntax
// explained above is often bulkier than desired.  To this end,
// Scala supports a much more compact form by exploiting `_`.
// Below are two equivalent functions:

val f1a: Object => Int = (o: Object) => o.hashCode
val f1b: Object => Int = _.hashCode

val o = new Object
assert(f1a(o) == f1b(o))

// ...and another two equivalent functions:

val f2a: (Int, Int) => Int = (x: Int, y: Int) => x + y
val f2b: (Int, Int) => Int = _ + _

assert(f2a(4, 5) == f2b(4, 5))

// See the pattern?  The first use of the underscore refers to the
// first parameter, the second use of the underscore refers to the
// second parameter, and so on.

// While this is much shorter, it can be hard to read if the function
// is quite complex.  Additionally, this only works if the type
// inferencer has enough information to infer what the parameter types
// should be.  For example, if you took the type annotations off
// of the above functions, the type inferencer wouldn't be able to
// figure out what the types of the functions that used the underscores
// should be.  On its own, `_ + _` could be `(Int, Int) => Int`,
// `(Double, Double) => Double`, `(String, String) => String`, etc.

// To help improve readability, as long as the type inferencer
// has enough information to work with, it is often possible to
// simply name the function parameters without explicitly giving
// them type annotations, like so:

val f: (Int, Int) => Int = (x, y) => x + y
