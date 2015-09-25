// Now that you've seen case classes, you're ready to tackle one of the
// most powerful features of Scala: pattern matching.  Pattern matching
// behaves much like a super-powered `switch` statement in other languages.
// I say "super-powered" because:
// 1.) `switch` generally only tests specific values, whereas pattern
//     matching can test if whole structures are there (even nested structures)
// 2.) `switch` generally doesn't allow you to check arbitrary conditions, just
//     values.  Pattern matching can check arbitrary conditions in addition
//     to comparing values
// 3.) Pattern matching can be used to concisely extract values of interest
//     out of a class.  `switch` generally doesn't have anything close to
//     this behavior.

// Without further ado, let's see some pattern matching.  First, a very
// `switch`-like example:

// More on Random is at: http://www.scala-lang.org/api/2.10.3/index.html#scala.util.Random
val random = new scala.util.Random

random.nextInt(3) match {
  case 0 => println("Saw 0")
  case 1 => println("Saw 1")
  case 2 => println("Saw 2")
  case _ => println("Don't know what I saw")
}

// In the above code, `_` is used as a default case.  This looks a
// lot like a C `switch` statement, except there isn't any nastiness of
// needing `break` to separate out cases (they are always separate with
// `match`).

// So integers?  Not so impressive.  Let's try strings and see what
// happens:

random.nextString(3) match {
  case "moo" => println("Sounds like a cow.")
  case "baa" => println("Probably a sheep.")
  case "zzz" => println("You're putting me to sleep.")
  case _ => println("No idea.")
}

// It's extremely unlikely you'll get those strings, so let's cut
// to the chase: pattern matching lets us compare strings, as well.
// In fact, all the primitive types in Java can be directly compared
// (integers, strings, booleans, doubles, etc.).  If you're used to
// JavaScript's `switch` as opposed to C's, this is probably
// not all that impressive.  But let's make this a bit more complex,
// shall we?

// match on a random string of 0-3 letters
random.nextString(random.nextInt(4)) match {
  case "hat" => println("Clothing")
  case "dog" => println("Animal")
  case "" => println("Empty string")
  case s if s.charAt(0) == 'a' => println("Starts with a")
  case s if s.length == 2 => println("Has length 2")
  case _ => println("It has been scientifically proven that " +
                    "this string is uninteresting")
}

// This deserves a bit of explanation.  The cases with the constant
// strings are pretty straightforward.  It's when we get to the
// whole `if` part that things start getting dicey.  When we say
// `s`, it means to bind whatever is being matched here to `s`.
// That is, this binds `s` to the random string we are matching
// on.  Note that this simply binds the result here, it doesn't
// trigger reevaluation of the expression we are matching on
// or anything like that.  If we add in an `if`, we can check
// if some arbitrary condition holds.  A case matches only
// if the pattern next to the case holds _and_ the condition
// in the `if` holds.

// Another point of interest with the above example is that cases
// are tried _in order_, and the first case that matches wins.
// Once a case matches, we can never jump to another case.  This
// is why in the above example, it tests for the empty string
// _before_ the `charAt` case, since the `charAt` case would throw
// an exception on an empty string.  By testing for the empty
// string first, we guarantee that if we ever reach the `charAt`
// case, then the string is non-empty.

// Now try it yourself.  Fill in the following match so that
// the first case only applies to the empty string, the second
// case to the string "foo", the third case to strings
// that end with 'z', and the fourth case to everything else.

random.nextString(random.nextInt(4)) match {
  case "" => println("Empty string")
  case "foo" => println("Saw foo")
  case s if s.last == 'z' => println("Ends with z")
  case _ => println("Something else")
}

// Now to add a bit of complexity, let's define a method that
// works in much the same way as our previous example, but
// it takes a special string that we want to check for.  A
// first attempt is like so:

def specialMethod1(special: String) {
  random.nextString(3) match {
    case special => println("Found the special string!")
    case "moo" => println("Sounds like a cow.")
    case "baa" => println("Probably a sheep.")
    case "zzz" => println("You're putting me to sleep.")
    case _ => println("No idea.")
  }
}

specialMethod1("Well this is odd")

// If you try running the above code, you'll see the compiler
// spit out a bunch of warnings, and ultimately the program
// says that it found the special string.  The problem is
// that this is impossible - the string passed has more than 3
// letters, so there's no way the randomly generated string
// could be the special string.  What gives?

// The problem here is that `special` is no different from
// `s` in the previous example.  Is is simply a variable name
// that is bound to what is being matched on.  What we
// want is to compare to `special`.  A second attempt is like
// so:

def specialMethod2(special: String) {
  random.nextString(3) match {
    case s if s == special => println("Found the special string!")
    case "moo" => println("Sounds like a cow.")
    case "baa" => println("Probably a sheep.")
    case "zzz" => println("You're putting me to sleep.")
    case _ => println("No idea.")
  }
}

// This works, but it still could be a bit cleaner.  To demonstrate,
// a much shorter version is below:

def specialMethod3(special: String) {
  random.nextString(3) match {
    case `special` => println("Found the special string!")
    case "moo" => println("Sounds like a cow.")
    case "baa" => println("Probably a sheep.")
    case "zzz" => println("You're putting me to sleep.")
    case _ => println("No idea.")
  }
}

// See the difference?  `special` now has backticks around it.  This
// says to compare the string being matched to whatever the value
// of `special` is.  This is very different from the semantics of
// introducing a new variable and binding something to it.

// It was previously stated that case classes have something to do
// with pattern matching, so let's jump on in.  First, let's
// create a class hierarchy below:

sealed trait Base
case class Foo(x: Int) extends Base
case class Bar(x: Int, y: Boolean) extends Base
case class OtherBase(b: Base) extends Base

// Above, the reserved word `sealed` tells the compiler that everything
// that extends `Base` is defined in this same file.  The fact that
// everything that extends `Base` is a case class is significant - since
// case classes automatically have `unapply` methods defined in their
// (implicit) companion objects, they can be used with pattern matching.

// Without further ado, let's pattern match on something:

// just to make the match more interesting, we randomly generate
// Base objects.  Don't worry too much about this code.
def randomBase(): Base = {
  val randomNum = random.nextInt(10) + 1
  if (randomNum <= 4)
    Foo(random.nextInt(5))
  else if (randomNum <= 8) 
    Bar(random.nextInt(5), random.nextBoolean)
  else
    // recursively apply
    OtherBase(randomBase)
}

// the actual pattern match
randomBase match {
  case Foo(x) => println("Saw a Foo containing: " + x)
  case Bar(x, true) => println("Saw a Bar containing: " + x + " where y was true")
  case Bar(x, _) => println("Saw some other Bar containing: " + x)
  case OtherBase(b) => println("Some other base: " + b)
}

// As shown, we can pattern match over case classes simply by
// using their name in a pattern.  Additionally, this "destructs"
// the case - we extract out the parameters passed to the constructor,
// allowing us to easily manipulate them.  Here, we are also
// using `_` to mean something a bit more specific than a default
// case - it's being used to indicate that we don't care what the
// value of `y` is in `Bar` for the second case on `Bar`.  In this
// way, `_` can be viewed as a pattern that matches everything it's
// applied to.

// Now is a good time to mention a semantic difference between pattern
// matching and `switch`.  Generally, with `switch`, if no case 
// applies (including the default), then nothing happens.  However,
// with pattern matching, an exception is thrown at runtime (specifically
// `MatchError`) if no case matches.  For this reason, the compiler will
// try to determine as accurrately as possible if there exists an input
// that wouldn't match.  If it can find such an input, it will issue
// a warning along with the input.  Otherwise, everything checks out
// and the compiler goes on its merry way.  This is important when
// it comes to case classes, since oftentimes we want to match on
// every possible case (i.e., when matching on `Base`, we want to have
// a case for `Foo`, `Bar`, and `OtherBase`).  If we miss a case, the
// compiler will issue a warning that the "match is not exhaustive".
// Normally, it would be impossible for the compiler to determine this,
// since there could be another class that extends `Base` in some other
// file, or even outside the program files.  However, by saying `sealed`,
// the compiler knows that all the subclasses of `Base` must be
// in this file, and so it can check if the match is exhaustive or not
// for us.

// As hinted by the use of `_` in the above example, patterns can be
// applied recursively.  `_` doesn't mean "the default case", it means
// "I don't care about whatever is at this position".  It can behave
// like a default case, but it's really just a placeholder.  So now
// that we know that patterns can be applied recursively, this means
// we can do clever little things like the following:

randomBase match {
  case OtherBase(OtherBase(OtherBase(_))) =>
    println("Nested at least three levels deep")
  case OtherBase(OtherBase(_)) =>
    println("Nested two levels deep")
  case OtherBase(_) =>
    println("Nested one level deep")
  case _ =>
    println("Not nested at all")
}

// The above code checks how deeply nested a particular
// instance of `Base` is.  The first case sees that it's
// at least three levels (`_` could very well be `OtherBase(...)`).
// The second case knows that it must be exactly two, since
// if it were 3 or more it would match the first case.  The
// third case knows it must be one, because the previous
// cases rule out everything >= 2.  In the last case, we
// know that there mustn't be any `OtherBase` at all, so
// we know it's not nested.

// Up until now, it looks like pattern matching behaves like a
// statement, much like with `switch`.  That is, the whole
// pattern match doesn't return anything.  However, this
// seems out of place - functional programming discourages
// mutable state, but any sort of statement implies that
// we need mutable state to make something work.

// In fact, pattern matching does not behave like a statement,
// but an expression.  A pattern match expression returns whatever
// the case that matched returned.  This is preciesly why
// we get a runtime error if no case matched - there is literally
// nothing to return if no case matched.  To show this off,
// consider the pattern match below:

val number =
  randomBase match {
    case Foo(x) => x
    case Bar(x, true) => x
    case Bar(x, false) => -x
    case OtherBase(_) => -2000
  }

// `number` ends up being assigned an `Int` when the above code is run.

// Before moving on, there are two more patterns of interest.
// To motivate the first of these patterns, consider the code below,
// which has had a case intentionally commented out:

def takesBar(b: Bar) {
  println(b)
}

randomBase match {
//  case Bar(_, true) => takesBar(...)
  case _ => () // Unit - this pattern match doesn't return anything
}

// In the code above, we only want to call `takesBar` if we have a
// `Bar`, and that `Bar` holds `true`.  The problem is that we have
// destructed away the `Bar`, so now we don't have anything to pass to
// `takesBar`.  We might tempted to do something like this (again, a
// case is commented out)

val temp = randomBase
temp match {
  //case Bar(_, true) => takesBar(temp)
  case _ => ()
}

// ...but this doesn't work either.  The problem is that `temp` is
// of type `Base`, but `takesBar` takes something of type `Bar`.
// As such, the code above would yield a type error, even though
// the only possible way to reach the call to `takesBar` is if
// `temp` is of type `Bar`.  We might be tempted to do a cast,
// but this code is getting ugly enough as it is.

// A better solution is to use a pattern with an `@` symbol, like this:

randomBase match {
  case b@Bar(_, true) => takesBar(b)
  case _ => ()
}

// With the above pattern, `b` is bound to the underlying `Bar` object,
// and `b` has type `Bar`.  Now we can call `takesBar` directly.
// By using `@`, we can both destruct and object and get a handle on
// the object at the exact same time.

// Now for one last pattern.  Sometimes all that is necessary is to
// know the underlying type of a value.  Here, we can use colon to 
// get at this information, like so:

// Generates very random values
def somethingRandom(): Any = {
  (random.nextInt(8) + 1) match {
    case 1 => random.nextBoolean
    case 2 => random.nextDouble
    case 3 => random.nextFloat
    case 4 => random.nextInt
    case 5 => random.nextLong
    case 6 => random.nextPrintableChar
    case 7 => random.nextString(random.nextInt(5))
    case 8 => randomBase
  }
}

val someValue: Any = somethingRandom
someValue match {
  case b: Boolean => println("Found a boolean: " + b)
  case _: Double => println("Found some double")
  case _ => println("Don't know what I found")
}

// Now try yourself.  Write a pattern match that has the following
// cases:
// -A long (Long)
// -The integer 5
// -An `OtherBase` object that contains a `Foo`
// -An `OtherBase` object that contains a `Bar` holding the integer 7
// -A `Bar`, which uses an `@` to pass along the new variable `b` to
//  the `takesBar` method
// -A catch-all case (i.e., a default)

somethingRandom match {
  case x: Long => println("Found a long value")
  case x: Integer if x == 5 => println("Found 5")
  case OtherBase(Foo(_)) => println("Found an OtherBase holding a Foo")
  case OtherBase(Bar(7, _)) => println("Found an OtherBase holding a Bar, which " +
                      "itself was holding 7")
  case b@Bar(_, _) => takesBar(b)
  case _ => println("Hit the default case")
}

// In this class, we will frequently use pattern matching in the context
// of programming languages.  For example, say we want to write a simple
// calculator of arithemtic expressions, such as `5`, `1 + 2`, and
// `2 * (3 + 4)`.  First, we explicitly define the syntax that we
// consider:
//
// `n` refers to an integer
//
// `op` is one of `+`, `-`, `*`, or `/`
//
// `exp` is an expression, which is either `n` or `exp1 op exp2`.  That is,
// expressions are recursively defined.

// We can implement this syntax in scala, like so:

sealed trait Op
case object Plus extends Op
case object Minus extends Op
case object Mul extends Op
case object Div extends Op

sealed trait Exp
case class Num(n: Int) extends Exp
case class Operation(exp1: Exp, op: Op, exp2: Exp) extends Exp

// From there, we can implement an interpreter for our language, like so:

def interpret(exp: Exp): Int = {
  exp match {
    case Num(n) => n
    case Operation(e1, op, e2) => {
      val v1 = interpret(e1)
      val v2 = interpret(e2)
      op match {
        case Plus => v1 + v2
        case Minus => v1 - v2
        case Mul => v1 * v2
        case Div if v2 != 0 => v1 / v2
        case _ => {
          throw new Exception("Division by zero attempted")
        }
      }
    }
  }
}

assert(interpret(Num(5)) == 5)
assert(interpret(Operation(Operation(Num(1), Plus, Num(2)), Plus, Num(3))) == 6)
assert(interpret(Operation(Num(2), Mul, Operation(Num(3), Plus, Num(4)))) == 14)
