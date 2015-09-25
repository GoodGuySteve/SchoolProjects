// You've gotten pretty far now without ever having written `if`.
// That said, you've probably seen `if` already in a few snippets which
// have been provided, and you may have noticed that it works a bit
// differently that the `if` statements you're probably more used to.

// Since Scala is a functional language, nearly everything in the
// language returns something (variable definitions are one big
// exception).  Even method calls that return `Unit` (i.e., `void`)
// return a special `Unit` object, it's just that the object is
// not particularly useful.

// The same goes for `if`.  `if` tends to be used to return something
// of interest quite frequently.  `if` behaves more like the
// ternary operator in most languages 
// (i.e., `(condition) ? ifTrue : ifFalse`).  Some examples are below:

val t = if (true) 1 else 2
val f = if (false) 2 else 3
assert(t == 1)
assert(f == 3)

// `if` can be chained along in the usual way with `else if`, like so:

val random = new scala.util.Random()
val randNum = random.nextInt(3)

val value =
  if (randNum == 0) {
    1
  } else if (randNum == 1) {
    2
  } else if (randNum == 2) {
    3
  } else {
    -1
  }

// the sort of chaining above, while common in many other languages,
// isn't often seen in Scala.  This is because pattern matching is
// usually much more concise, as with:

val value2 = random.nextInt(3) match {
  case 0 => 1
  case 1 => 2
  case 2 => 3
  case _ => -1
}

// That said, sometimes all you need is `if`

// One last important bit with `if`.  If you don't provide an `else`
// case, then it is implied that the `else` case simply returns `Unit`.
// This effectively makes the result from conditionals without an
// `else` useless.  This is to allow programmers to write very
// imperative code if they desire, without worrying about the functional
// aspects.  For example, see the code below:

val retval =
  if (true) 1

// The compiler infers that `retval` is of type `AnyVal` above, which
// is an extremely non-specific type.

// Now try it yourself.  Finish the method below to make the assertions
// hold:

def myMin(x: Int, y: Int): Int = {
	if (x > y) {y}
	else {x}
}

assert(myMin(1, 2) == 1)
assert(myMin(2, 1) == 1)
