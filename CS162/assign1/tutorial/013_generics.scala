// Many statically-typed languages have some concept of
// parametric polymorphism, i.e., using type variables
// as opposed to using concrete types.  For example, Java
// has generics, and C++ has templates.  Type variables allow
// us to describe code that works the same way regardless of
// the types involved.  This is useful for container-like
// data structures, which behave the same way regardless of
// what exactly they contain (e.g., the code to get the
// first element of a list of integers and a list of
// strings is the same).

// Scala supports generic types, using square brackets.
// To demonstrate, we will create a very simple linked list
// data structure.  First, recall the inductive definition
// of a list - a list is either an empty list or a cell
// holding an item and a pointer to the rest of the list.
// From here, let's define a list of integers, like so:

sealed trait MyList {
  def isEmpty(): Boolean
  def head(): Int
  def tail(): MyList
}
case class Cell(item: Int, restList: MyList) extends MyList {
  def isEmpty(): Boolean = false
  def head(): Int = item
  def tail(): MyList = restList
}
case class Empty() extends MyList {
  def isEmpty(): Boolean = true
  def head(): Int =
    throw new Exception("Head called on empty list")
  def tail(): MyList =
    throw new Exception("Tail called on empty list")
}

// This is a good time to remind you about virutal dispatch. Virtual dispatch
// moves part of method selection from compile time to runtime. In C++
// you opt-ed into this behavior with the virtual keyword. When a method
// was marked virtual it means that instead of calling a method directly,
// the method will be looked up in the object's vtable, an array of methods
// that the object holds a reference to.
//
// This means at runtime the runtime representation of the object determines
// what method is invoked. In Java all instance method dispatch is virtual,
// meaning that you *always* get virtual dispatch, unlike C++.
//
// This is critical to supporting polymorphism in the language as we want
// and object to invoke its specialized code regardless of what we believe its
// static type to be.
//
// For example in:
// Shape s = new Circle();
// s.draw() // we invoke Circle's draw method not Shape
//
// Scala is identical in this regard and it is important to keep in mind that
// calling `isEmpty` on `Empty` will invoke the code that returns `true`, while
// invoking it on `Cell` will invoke the code that returns `false`.
//
// This behavior is critical to case classes working like we demonstrate above.
// The class defines a common interface but we invoke the code corresponding to
// object we allocated.
//
// So:
// Empty().isEmpty() == false
// Cell(1, Empty()).isEmpty() == true

// Now let's play with it a bit:

assert(Cell(1, Cell(2, Cell(3, Empty()))).head == 1)
assert(Cell(1, Cell(2, Empty())).tail == Cell(2, Empty()))
assert(Empty().isEmpty)

// The fact that this list only holds integers is kind
// of annoying.  There is nothing in this structure that
// is specialized towards integers.  That is, we could
// simply find-and-replace `Int` with `Double` and
// still have a valid list definition.  As such, this list
// is best made generic, like so:

sealed trait MyList2[T] {
  def isEmpty(): Boolean
  def head(): T
  def tail(): MyList2[T]
}
case class Cell2[T](item: T, restList: MyList2[T]) extends MyList2[T] {
  def isEmpty(): Boolean = false
  def head(): T = item
  def tail(): MyList2[T] = restList
}
case class Empty2[T]() extends MyList2[T] {
  def isEmpty(): Boolean = true
  def head(): T =
    throw new Exception("Head called on empty list")
  def tail(): MyList2[T] =
    throw new Exception("Tail called on empty list")
}

// Now we can play with it just like before:

assert(Cell2(1, Cell2(2, Cell2(3, Empty2()))).head == 1)
assert(Cell2(1, Cell2(2, Empty2())).tail == Cell2(2, Empty2()))
assert(Empty2[Int]().isEmpty)

// Note that we only needed to specify the type for the last assertion.
// This is because in the cases with Cell2, the compiler
// is able to infer that the type variable `T` should be an
// integer from the fact that the list items are all integers.
// With the last assertion, because the list doesn't contain
// anything, we needed to specify the type explicitly.

// Now that we have a generic list, we can do more than just
// work with integers:

assert(Cell2("foo", Cell2("bar", Cell2("baz", Empty2()))).head == "foo")
assert(Cell2(1.5, Cell2(2.5, Cell2(3.5, Empty2()))).head == 1.5)
assert(Cell2('a', Cell2('b', Cell2('c', Empty2()))).head == 'a')

// Now that we have a polymorphic list, let's add in something
// useful.  A common idiom is to get the head element of
// a list and then apply a given operation on it.  Rather
// than force programmers to extract the head and apply the function,
// let's define a method named `applyHead` that will call a
// function on the head directly (intentionally commented-out below):

// def applyHead(list: MyList2[T], function: T => T): T = function(list.head)

// If you attempt to run the code above, the compiler will error out
// saying `error: not found: type T`.  Right now, the compiler looks
// for a type literally named `T`, and fails to find one, triggering
// an error.  This can be easily fixed by telling the compiler that
// `T` is a type variable specific to the context of `applyHead`:

def applyHead[T](list: MyList2[T], function: T => T): T = function(list.head)

// The above code works correctly, but it is a tad constricting: the
// function must return something of the same type as the element type of
// the list.  This means that given a list of integers, we couldn't
// convert the first integer to a string or anything like that.
// This restriction isn't actually required, though: the function can
// return anything, and we simply return whatever the passed function
// returns.  As such, we can adjust `applyHead` like so:

def applyHead2[T, U](list: MyList2[T], function: T => U): U = function(list.head)

// Now we've added a second type variable: `U`.  The function passed
// now returns something of type `U`, and `applyHead2` as a whole
// also returns something of type `U`, namely whatever the passed
// function returned.  This is strictly more general than the original
// `applyHead` method - there is no restriction here that `T` and `U`
// cannot be the same type, merely that `T` and `U` _may_ differ.

// Now try it out yourself.  Define a method named `applySecond` that works
// like `applyHead2`, but operates on the second element on the list.
// As a hint, you will need to call both `head` and `tail` to get the
// second element of the input list.  `applySecond`'s definition should
// allow the following assertions to pass:

// DEFINE `applySecond` HERE

def applySecond[T, U](list: MyList2[T], function: T => U): U = function(list.tail.head)

assert(applySecond(Cell2("foo", Cell2("bar", Cell2("baz", Empty2()))),
                   (s: String) => s.charAt(1)) == 'a')
assert(applySecond(Cell2(1.5, Cell2(2.5, Cell2(3.5, Empty2()))),
                   (d: Double) => d.floor) == 2.0)
assert(applySecond(Cell2('a', Cell2('b', Cell2('c', Empty2()))),
                   (c: Char) => (c.toInt + 1).toChar) == 'c')
