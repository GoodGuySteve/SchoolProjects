// This lesson introduces Option.  While the basic idea behind Option
// is relatively simple, really using it properly requires the majority
// of the topics we've discussed so far.  We only scratch the surface
// with Option here; you may which to peruse:
// http://www.scala-lang.org/api/2.10.2/index.html#scala.Option
// ...to read more about option.

// Consider this snippet of Java code below:
//
// String doSomething(Object o) {
//    return o.toString;
// }
//
// Question: does this code contain any bugs?  At first, it seems to
// be an obvious "no", since all this code does is call `toString`
// on some given object.  How could this possibly fail?
//
// Well, here is how:
//
// doSomething(null);
//
// Curse you, null!  Null is a major blessing and curse.  While
// null makes for a really easy sentinel value (i.e., a value
// to indicate that something special happened), it makes it
// too easy.  Null can be used in place of any object, and the
// compiler will happily work with this (type theoretically, we
// say that null "inhabits" every Object type).  Because of
// null, anyplace that uses an Object potentially could have
// null passed.  This means that anyplace an object is used, we
// could also use null.

// This is especially dangerous when it comes to working with
// poorly documented code (but that never happens, right?).
// If you don't check for null and you get null, then this is
// almost certainly going to end in a nasty bug.  If you're not
// checking for null, it only becomes obvious that null is in
// play when the dreaded null pointer exception occurs, which
// could be long after something unexpectedly returned null.

// Taking the opposite approach of assuming everything returns
// null is also not a great idea.  Those null checks do have a
// performance penalty associated with them.  More importantly,
// this means spending time writing error-handling code
// for conditions which may not even be possible, which is
// a complete waste.

// Scala offers a solution to this problem: Option.  Option encodes
// the possibility that something can be null directly in the type.
// That is, variables that could be null have a type that is distinct
// from variables that cannot be null.  This means that we encode
// whether or not something can be null directly in the type system,
// allowing the compiler to make sure we have null checks in all
// the right places, and only in the right places.

// To show how this works, a simplified version of Scala's Option
// type is represented below:

sealed trait MyOption[T] {
  def isEmpty(): Boolean
  def get(): T
}
case class MySome[T](get: T) extends MyOption[T] {
  def isEmpty(): Boolean = false
}
case class MyNone[T]() extends MyOption[T] {
  def isEmpty(): Boolean = true
  def get(): T =
    throw new Exception("Tried to get the item out of null")
}

// In the above code, `isEmpty` returns true if we have null
// (represented by `MyNone`), and false if we have a non-null object
// (represented by `MySome`).  `MySome` is a wrapper around a
// non-null object, whereas `MyNone` represents null.

// With The above code definition, we can say directly in the type
// if something can be null or not.  For example:

def doSomething(o: Object): String = o.toString

// The fact that `o` isn't wrapped in a `MyOption` indicates that
// this should take a non-null value.  However, if we had:

def doSomething2(o: MyOption[Object]): String =
  if (o.isEmpty) "" else o.get.toString

// Now we explicitly state that this might take null.  In order
// to call `toString` on the underlying object, we must call
// `get` first.  The contract on MyOption is that if you want
// to call get, you should first check if it's null or not.

// You may have noticed that the solution presented is only
// partial in nature.  Now we can encode if something could be
// null, but there is nothing actually enforcing that we perform
// the check.  A particularly lazy programmer might be tempted
// to simply put `.get` after everything that could be null
// (has type `MyOption[T]`), which largely defeats the purpose.

// For the above reason, it's generally discouraged to use a
// conditional to perform this check.  It's far too easy to
// forget to check, so it's usually considered bad practice to
// check for null this way.  A slightly better approach is to
// use pattern matching here, as with:

// Generate a random object to work with
val random = new scala.util.Random
def randomMyOption(): MyOption[Object] =
  if (random.nextBoolean) MySome(new Object) else MyNone[Object]

randomMyOption match {
  case MySome(o) => println("Got an object: " + o)
  case MyNone() => println("Didn't get an object")
}

// As explained in pattern_matching.scala, the compiler can
// at least warn us here if we forgot the null check.

// The pattern matching approach is still not very idiomatic,
// largely because it's so bulky.  We need a better way to perform
// this checking.

// An observation is that a common code pattern is to perform
// an operation only when something isn't null.  If something
// is null, then we simply want to carry the null through without
// touching it.  While this doesn't cover every single possible
// use of null, it covers the vast majority of cases, so this
// would serve as a nice abstraction.

// Using higher-order functions, we can achieve the above
// abstraction without a lot of effort.  The idea is to define
// a method that takes an Option and a function, and applies
// the provided function _only if_ the Option isn't null.
// To be consistent with the types, we wrap the result in an
// Some if it can be applied.  Without further ado, let's see this
// in action, with a method called `map`:

def map[T, U](o: MyOption[T], f: T => U): MyOption[U] =
  o match {
    case MySome(item) => MySome(f(item))
    case MyNone() => MyNone[U]()
  }

// Two example calls to `map`:
val option1 = map(randomMyOption, (o: Object) => o.toString)
val option2 = map(randomMyOption, (o: Object) => o.hashCode)

// While we still had to perform a null check, we needed only
// to perform the check in `map`.  Now other code can use
// `map` and avoid the need for any explicit null checking.

// Now that we've seen the basic idea of `Option` in action,
// let's start using the real thing.  First, a function to
// generate random Scala `Option`s that contain something:

def randomOption[T](t: T): Option[T] =
  if (random.nextBoolean) Some(t) else None

// Now to show off `map`.  In Scala, `map` is defined as a
// method directly on `Option`, as opposed to something
// floating out in the ether.  So we can write code like
// this:

randomOption(5).map(i => assert(i == 5))
randomOption("moo").map(s => s + "cow").map(s => assert(s.endsWith("cow")))

// In the first case, the assertion is only called if we 
// have something that's non-null, in which case it must be
// 5, the value passed to `randomOption`.  In the second
// case, the only way the last assertion could be hit is if
// the first `map` operation were actually performed that
// concatenated the strings.  If the first operation couldn't
// be performed, then `None` would be returned.  `map`
// is defined to simply return `None` in the case that
// it received `None`, so the assertion isn't run - the
// function is created but never called in this case.

// Now try it yourself.  Fill in the `???` below to make the assertions
// always hold.

randomOption(7).map(i => i * 2).map(i => assert(i == 14))
randomOption("foo").map(s => s + s).map(s => assert(s.length == 6))

// `map` is useful for chaining calls which behave on null.
// But what if we reach a point where we want a non-null value?
// That is, we can guarantee that `None` won't happen or it is
// otherwise senseness, and therefore we shouldn't return
// something of type `Option` at all?

// For example, consider a method which takes an optional
// integer and does the following:
// -If it's non-None, it returns 5 + whatever the integer is.
// -If it's None, it returns 5

// Here, we can't simply use `map` - map returns something of
// type `Option[Int]`, but we want something of type `Int`
// directly.  We could use pattern matching here, shown with
// a first attempt:

def fiveOrAddFive1(i: Option[Int]): Int =
  i match {
    case Some(num) => num + 5
    case None => 5
  }

// ...but the problem here is again, this is quite bulky.

// Here is where another method on `Option` comes in:
// `getOrElse`.  `getOrElse` takes a value and returns 
// one of two things:
// -The provided value, if called on `None`
// -The value contained within the `Some`, if it is
//  a `Some`.  In this case, the value passed is ignored
//  entirely.

// To see this in action, see below:

assert(Some(5).getOrElse(7) == 5)
assert(None.getOrElse(10) == 10)

// In the first assertion, `getOrElse` sees that it has
// been called on a `Some`, so it returns the result of
// calling `get` (`5` in this example).  In the second
// assertion, `getOrElse` is called on `None`, so the
// parameter passed to `getOrElse` is used.

// Using `getOrElse`, we can more elegantly implement
// `fiveOrAddFive`, shown below:

def fiveOrAddFive2(i: Option[Int]): Int =
  i.map(_ + 5).getOrElse(5)

// Now try it yourself.  Fill in `???` below to make the
// assertions pass:

val obj1: Option[Int] = Some(1)
val obj2: Option[Int] = None

assert(obj1.map(i => i + 2).getOrElse(1) == 3)
assert(obj2.map(i => i + 2).getOrElse(3) == 3)

// Before we end our lesson on `Option`, let's take a 
// short digression to experiment with `getOrElse` a bit
// more.  Consider the program below:

var mutable = 0

Some(1).getOrElse( { 
  mutable += 1
  mutable
})

// After this snippet of code runs, what should the value of
// the mutable variable `mutable` be?  Two assertions below
// represent the possibilities; comment-out the failing
// assertion.

assert(mutable == 0)
//assert(mutable == 1)

// Surprised?  The snippet of code above doesn't change the
// value of `mutable`.  It's as if the code passed to
// `getOrElse` was never run at all.  In fact, it wasn't!

// Note that `getOrElse` only needs its parameter if it is
// called on `None`.  If it is called on `Some`, evaluating
// the parameter is a total waste, because it won't be called.
// With most languages, you tend to be stuck here - you only
// want to evaluate the parameter if called on `None`, but
// thanks to call-by-value or call-by-reference semantics, 
// we won't know if we should evaluate the parameter until
// after we've done so.  Oops.

// If we have higher-order functions available, we can do 
// something a bit more clever here.  Instead of passing
// a value directly to `getOrElse`, we can pass a function
// that can be later evaluated by `getOrElse`, but, only
// if it is called on `None`.  That way, if it is called
// on `Some`, we simply never call the function.  To
// illustrate this, consider the snippet below:

def myGetOrElse[T](o: Option[T], function: () => T): T =
  o match {
    case Some(t) => t
    case None => function() // () needed to force the call
  }

assert(myGetOrElse(None, () => 7) == 7)

// The above shows a method definition that takes an option and
// a function.  The function doesn't take any parameters, as
// indicated by the fact that it's input type is `Unit` (`()`).
// With the above definition, we call the provided function only
// if we have `None`, avoiding the evaluation of the argument
// to `getOrElse`.

// So that's all fine and dandy, but this still doesn't answer why
// we could pass values directly to Scala's `getOrElse`, since
// we didn't need to add in any sort of `() => ...` to the parameter.
// The answer is that Scala can actually handle this for us
// _automatically_, as long as we tell it that's what we want.  To
// do this, a `=>` is put after the colon for whatever parameter
// we want treated this way.  For example:

def myGetOrElse2[T](o: Option[T], otherwise: => T): T =
  o match {
    case Some(t) => t
    case None => otherwise
  }

assert(myGetOrElse2(None, 7) == 7)

// Note the `=>` right before the type of `otherwise` above, and
// that we didn't need to make the second parameter to `myGetOrElse2`
// a function.  By adding `=>` it tells the compiler that this 
// parameter is special, in that it is only evaluated if we
// explicitly use it.  With the definition of `myGetOrElse2`, we
// only use it in the `None` case, so in the `Some` case, the
// corresponding code isn't evaluated.

// An additional note about this is that the code is evaluated each
// time the parameter used.  To illustrate, consider the code below:

def runTwice(p: => Unit) {
  p
  p
}

var newMutable = 0

runTwice(newMutable += 1)

assert(newMutable == 2)

// This is an extremely powerful feature that can be used to
// define operators that are sensitive to control flow.  For example,
// we can define our own `while` loop:

def myWhile(condition: => Boolean, doThis: => Unit) {
  if (condition) {
    doThis
    myWhile(condition, doThis)
  }
}

var iterator = 0

myWhile(iterator < 10, iterator += 1)

assert(iterator == 10)

// By the way, the shortest way to explain `=>` is to say it
// specifies that the given parameter should be call-by-name,
// as opposed to call-by-value.

// Now try it yourself.  Fix the `broken` method below to
// make the assertions pass.  You should modify _only_ `broken`:

def broken[T](i: Int, ifLess: => T, ifMore: => T): T =
  if (i < 10) ifLess else ifMore

var brokenMutable = 0

broken(5, brokenMutable += 1, brokenMutable += 10)
broken(11, brokenMutable += 100, brokenMutable += 1000)

assert(brokenMutable == 1001)

// One last note about `Option`.  In order to interoperate with
// Java and other languages on the JVM, Scala does, in fact, support
// the same direct form of `null` that you are used to.  However, it's
// extremely non-idomatic to use it; the only time it is intended to
// be used is when one interfaces to existing Java code. In this class,
// you should never use `null` or compare against it in any way.
