// In this lesson, we will cover Scala's `Seq` trait, which is one
// of the most common traits you will work with in this class.
// As a warning, you may find this lesson to be quite difficult,
// as it brings together many of the concepts we've already discussed.
// Additionally, we start to dive into "loops" here, which hasn't
// been discussed yet.  I put "loops" in quotes because you won't
// actually write any loops, but more on that later.

// In Scala, `Seq` is a trait that represents a sequence of something.
// Oftentimes, if you need a sequence with no special requirements,
// `Seq` is the trait you want.  `Seq` has a companion class defined
// with an `apply` method, allowing you to do things like this:

val s1: Seq[Int] = Seq(1, 2)
val s2: Seq[String] = Seq("moo", "cow", "bull")
val s3: Seq[Double] = Seq(7.5)
val s4: Seq[Char] = Seq[Char]()
val s5: Seq[Any] = Seq(5, new Object, 2.7, "what")

// In the above examples, the type annotations are provided only
// for clarity; the type inferencer can figure all of these out
// automatically.

// Coming from an imperative background, you may think that
// you can add or remove elements from sequences as you want.
// For example, a very common pattern is something like the following,
// commented-out because it's psuedocode:

// List[String] asStrings(ints: List[Int]) {
//   List[String] retval = newEmptyList();
//   for(int x = 0; x < ints.length(); x++) {
//     retval.addElement(ints.getElement(x).toString());
//   }
//   return retval;
// }

// Using only `Seq`, it's not possible to implement the above
// pattern directly in Scala.  This is because `Seq` is immutable;
// that is, once you create a sequence, it cannot be changed.
// This is probably the greatest blessing and curse with functional
// programming - it's a blessing because you never need to worry
// about something else modifying the sequence out from under you,
// but it's a curse because, well, how do you do anything if you
// can't modify it?  This lesson drives at answering this question.

// For one, the loops you're probably used to, like `for` and `while`,
// are not going to be particularly helpful here.  Scala doesn't
// even support `for` loops (at least not like anything you're probably
// used to), and for this class we don't want you writing any `while`
// loops.  Why aren't these normal staples of programming not very
// useful here?  Because by their very design, they assume there
// is some mutable state somewhere.  Because `while` and `for` are
// both statements, in order for them to compute any sort of result,
// they must modify state somewhere.  Otherwise it's not possible
// to pass the result of the loop to some other bit of code.  In
// Scala, we strongly discourage mutating state, so suddenly
// these loops become largely useless.

// In functional programming, instead of loops, recursion is the
// standard tool to perform any sort of iteration.  Recursion
// operates over functions, and functions return things, so
// therefore we can return something if we can iterate via recursion.

// To demonstrate this, let me first introduce a new Scala data structure:
// `List`.  `generics.scala` first introduced lists, via an inductive
// definition (a list is either an element and the rest of a list, or
// an empty list).  `List` is Scala's implementation of these sort of
// lists, and they come equipped with a large number of methods.
// Additionally, `List` actually implements `Seq` - it is a specific
// form of sequences.  The reason we're using them here is that
// `List` allows us to put an element at the front of the list
// in constant time, resulting in a new `List`.  `Seq` does not have
// such a guarantee.  For example, we can do this:

val l1 = List(1, 2)
val l2 = 0 :: l1  // 0 :: list prepends 0 onto list, resulting in a new list

assert(l1 == List(1, 2))
assert(l2 == List(0, 1, 2))

// It is worth nothing here that `::` is just a method with a special name,
// it takes two arguments, a single element, and a list.
//
// As shown above this means that `1 :: 2 :: Nil` is equivalent to `List(1, 2)`
// This operator is referred to as `cons` because it constructs a new list
// given a head element, and tail list.
//
// This is equivalent to constructing a `Cell` value in the list representation
// we presented in `generics.scala`.

// Note that the original list `l1` isn't modified when 0 is prepended
// onto it.  Rather than destructively changing the original list,
// :: results in a new list.  Because everything is immutable anyway,
// there is no need to copy anything - copies are only needed when
// something could change, but we've restructed ourselves so that this
// cannot be changed.  In memory, there is only one list [0, 1, 2] here,
// but `l1` and `l2` index into different parts of it.

// Now that we've introduced `List`, we're ready to show how recursion
// could be used here.  Let's implement the pseudocode above in Scala,
// using recursion instead of iteration:

def asStrings(ints: List[Int]): List[String] =
  if (ints.isEmpty) List() else ints.head.toString :: asStrings(ints.tail)

assert(asStrings(List(1, 2, 3)) == List("1", "2", "3"))

// There is a lot going on here, so this is worth explaining.  For
// one, because this is recursion, we need a base case and a recursive
// case.  The base case here is when we are told to process an empty list.
// Given an empty list of integers, we know that we should get an
// empty list of strings, so this is simple enough.

// Now for the recursive case.  In the recursive case, we know we have
// a non-empty list.  This means we have an element (denoted by `head`),
// along with the rest of the list (denoted by `tail`).  In this case,
// we know that the resulting list should begin with the head element
// as a string, hence `ints.head.toString`.  As for the rest of
// the list, we can recursively process this, hence `asStrings(ints.tail)`.
// Finally, we put the two together with `::`.  That is, we prepend
// the integer we converted onto the result of the recursive call.
// With inductively-defined lists, this pattern of checking for empty in
// the base case and recursively processing in the recursive case is
// extremely common.

// Now you try.  Replace `???` with code below to allow the assertions to
// pass.  The code should return a list of the same length as the input
// list, where each element is the result of adding 1 to the element in
// the other list

def addOneToEach(ints: List[Int]): List[Int] = 
	if (ints.isEmpty) List() else (ints.head + 1) :: addOneToEach(ints.tail)

assert(addOneToEach(List()) == List())
assert(addOneToEach(List(1)) == List(2))
assert(addOneToEach(List(1, 2)) == List(2, 3))

// In writing your implementation of `addOneToEach`, you may have noticed
// that it looks a whole lot like `asStrings`.  In fact, there is a ton
// of shared behavior here:
// 1.) Checking if the input list is empty or not
// 2.) Returning an empty list if the input list is empty
// 3.) Recursively calling yourself if the input list is non-empty.
// 4.) Building up a result from the recursive call and performing
//     some operation on the head of the input list.

// The only real difference here is in the operation performed on
// the head of the list, that is, the computation performed.
// Higher-order functions let us abstract whole computations, so
// we can define something like this:

def myMap[A, B](input: List[A], function: A => B): List[B] =
  if (input.isEmpty) List() else function(input.head) :: myMap(input.tail, function)

assert(myMap(List(1, 2, 3), (i: Int) => i + 1) == List(2, 3, 4))
assert(myMap(List("moo", "cow", "bull"), (s: String) => s.length) == List(3, 3, 4))
assert(myMap(List(2.5, 3.0, 4.2), (d: Double) => d.floor) == List(2.0, 3.0, 4.0))

// Now instead of writing out a recursive method every time, we can
// simply reuse the definition of `myMap`.

// You may wonder why this method was named `myMap`, of all things.
// Well, this is not the first time someone has noticed this sort
// of pattern.  This operation is commonly referred to as `map`, so
// named because it maps elements of some input list to elements of
// some output list.  It is so common that Scala defines it for us
// in `Seq`, allowing us to say things like:

assert(List(1, 2, 3).map(_ + 1) == List(2, 3, 4))
assert(List("moo", "cow", "bull").map(_.length) == List(3, 3, 4))
assert(List(2.5, 3.0, 4.2).map(_.floor) == List(2.0, 3.0, 4.0))

// Try it yourself.  Fill in the `???` with code below to make the
// assertions hold:

assert(List(1, 2, 3).map(_ + 5) == List(6, 7, 8))
assert(List(1, 2, 3).map(_.toString) == List("1", "2", "3"))
assert(List(Some(1), Some(2), Some(3)).map(_.getOrElse(0)) == List(1, 2, 3))

// Overall, `map` often tends to be the most common operation used over
// `Seq`, both for this class and beyond.  Whenever we have the general
// problem of wanting to go from a `Seq` containing one type to a
// `Seq` containing another type, `map` almost assurredly is what you
// want.  This tends to be true of more than just functional
// programming, as `map` abstracts the following imperative-style
// psuedocode:

// Given: input is a List[A]
//
// List[B] result = new List[B];
// for each item in input:
//   B converted = doSomeOperationOn(item);
//   result.add(converted);
// return result

// `map` is applicable as long as all the following are true:
//
// 1.) Each item can be processed independently.  For example, I don't
//     need to know anything about the first item in order to process
//     the second item.
// 2.) Each input item has exactly one output item.

// Now for another kind of common pattern.  Say we have some input
// list of integers, and we want to return a list of all the numbers
// that are even.  Restricting ourselves so that everything is
// immutable, we may come to this implementation (now using
// pattern matching to destruct the input list):

def evens(input: List[Int]): List[Int] =
  input match {
    // case that we have an empty list, referred to as `Nil`
    case Nil => List()

    // case of a non-empty list, where the head is even
    case head :: tail if head % 2 == 0 =>
      head :: evens(tail)

    // case of a non-empty list, where the head must be odd
    case _ :: tail => evens(tail)
  }

assert(evens(List(1, 2, 3, 4)) == List(2, 4))
assert(evens(List(1, 3, 5)) == List())
assert(evens(List(2, 4, 6)) == List(2, 4, 6))

// A bit of explanation with the above code.  For one, we are
// pattern matching on the list itself.  When we say `Nil`, it
// refers to an empty list.  With the notation `a :: b`, we are
// stating that the list if of an element `a` followed by the
// rest of the list `b`.  Recall that we can nest patterns,
// so we could even pattern match of lists of length at least
// 2, like: `a :: b :: c`, where `a` and `b` are the first two
// elements of the list, and `c` is the rest of the list.

// There is only a single base case: when the list is empty, return
// an empty list.  That is, if we have an empty list, then we no that
// we have no elements which are even in the list.  There are
// two recursive cases: when the list starts with an even number,
// and when it doesn't.  If it starts with an even number, then
// the result list starts with the same even number, and we recursively
// process the tail of the input list.  If the input list doesn't
// start with an even number, then the result list should be the result
// of recursively processing the tail.

// Now for another problem: what if we are given a list of
// strings, and we want to return only those strings of length 3?
// Implement this below, in order to allow the following assertions
// to pass

def lengthThree(input: List[String]): List[String] = 
	input match {
	    case Nil => List()
   
		case head :: tail if head.length == 3 =>
			head :: lengthThree(tail)

		case _ :: tail => lengthThree(tail)
	}

assert(lengthThree(List("moo", "cow", "bull")) == List("moo", "cow"))
assert(lengthThree(List("foo", "bar", "baz")) == List("foo", "bar", "baz"))
assert(lengthThree(List("This", "doesn't", "have", "anything")) == List())

// See a pattern forming?  The only difference between `evens` and `lengthThree`
// is the predicate used to test if a given list element is interesting.  That is,
// the test used to see whether or not the result list should begin with
// the head element or not.  With the help of a higher-order function, we
// can abstract this out, like so:

def myFilter[A](input: List[A], predicate: A => Boolean): List[A] =
  input match {
    case Nil => List()
    case head :: tail if predicate(head) =>
      head :: myFilter(tail, predicate)
    case _ :: tail => myFilter(tail, predicate)
  }

assert(myFilter(List(1, 2, 3), (i: Int) => i % 2 == 0) == List(2))
assert(myFilter(List("moo", "cow", "bull"), (s: String) => s.length == 3) == List("moo", "cow"))

// `filter` is another method defined on `Seq` in Scala, so we can do things like:

assert(List(1, 2, 3).filter(_ % 2 == 0) == List(2))
assert(List("moo", "cow", "bull").filter(_.length == 3) == List("moo", "cow"))

// Try it yourself.  Replace the `???` below with code in order to make
// the assertions pass:

assert(List(1, 2, 3).filter(_ % 2 != 0) == List(1, 3))
assert(List("apple", "asparagus", "bannana").filter(_.head == 'a') == List("apple", "asparagus"))
assert(List(2, 5, 7, 9, 10).filter(_ < 6) == List(2, 5))

// `filter` is another very common operation, commonly used in a variety of situations.
// `filter` abstracts over the following imperative-style pattern:

// Given: input is a List[A]
//
// List[A] result = new List[A];
// for each item in input:
//   if (some condition is true for the item):
//     result.add(item);
// return result

// `filter` is applicable so long as all the following are true:
//
// 1.) Each item can be processed independently.  For example, I don't
//     need to know anything about the first item in order to process
//     the second item.
// 2.) No item transformation needs to occur.  Unlike `map`, which is used to
//     transform items, `filter` is used to select particular items of interest,
//     without modifying those items.

// Now for a third common pattern.  Say we have a customer database, where each
// customer is associated with some history of purchased items.  We may represent
// this information like so:

case class Item(name: String, cost: Double)
case class Customer(name: String, purchaseHistory: List[Item])

val item1 = Item("Desk Calculator", 8.00)
val item2 = Item("Lamp", 20.00)
val item3 = Item("Notebook", 3.00)
val item4 = Item("Pens, 10 pack", 4.00)

val database = List(Customer("Alice", List(item1, item3, item4)),
                    Customer("Bob", List(item2, item3)))

// Say we want to record all items that have been purchased between all customers.
// To accomplish this, we want to define a function with the following signature:
//
// def allItemsPurchased(customers: List[Customer]): List[Item]

// Now we want to implement this function.  At first, `map` seems to come
// to mind, so we try that out:
//
// def allItemsPurchased(customers: List[Customer]): List[Item] =
//   customers.map(customer =>
//     customer.purchaseHistory)

// Unfortunately, the code above does not compile.  We have a type error involving
// `customers.map(...)`, in that we end up getting back `List[List[Item]]`, as opposed
// to the `List[Item]` that we want.  Fundamentally, the problem is that we are,
// in essence, trying to return multiple items at a time from `map` given a single
// input item, and `map` does not combine results.  Instead, `map` gives us back
// a bunch of item sequences on a per-customer basis.  `map` assumes that we only
// get one item back per input item, but here we are trying to return multiple
// items back per input item.  As such, `map` is not applicable.  Instead, we must
// implement our own function:

def allItemsPurchased(customers: List[Customer]): List[Item] =
  if (customers.isEmpty) {
    List()
  } else {
    // `++` appends lists together, so List(1, 2) ++ List(3, 4) == List(1, 2, 3, 4)
    customers.head.purchaseHistory ++ allItemsPurchased(customers.tail)
  }

assert(allItemsPurchased(database) == List(item1, item3, item4, item2, item3))

// Compare the code above to the code for `asStrings`, duplicated below for convenience:

// def asStrings(ints: List[Int]): List[String] =
//   if (ints.isEmpty) List() else ints.head.toString :: asStrings(ints.tail)

// The two bits of code should look very similar.  There are only two notable
// differences:
//
// 1.) What we do per item differs.  In `allItemsPurchased`, we get the purchase
//     history of the current item.  In `asStrings`, we convert the item to a string.
//
// 2.) We use append to build up the result in `allItemsPurchased`, whereas we use
//     list cons (`::`) to build it up in `asStrings`.  This corresponds to the fact
//     that with `allItemsPurchased`, processing one item results in an arbitrary
//     number of items, whereas in `asStrings` processing one item always results
//     in exactly one new item.

// With the first difference, we can abstract this out using higher-order functions.
// That is, instead of hardcoding what operation to perform on each item, we can
// take a function that performs the operation.  As for the second difference, this
// is more substantial, and implies that we're working with a different kind of
// operator altogether.  In Scala, this operator is called `flatMap`, which
// is implemented below:

def myFlatMap[A, B](input: List[A], function: A => List[B]): List[B] =
  if (input.isEmpty) List() else function(input.head) ++ myFlatMap(input.tail, function)

assert((myFlatMap(List(1, 2, 3), (i: Int) => List(i - 1, i, i + 1)) ==
        List(0, 1, 2, 1, 2, 3, 2, 3, 4)))

// `flatMap` behaves much like `map`, except that it lifts the restriction that
// for every input element, there is exactly one output element.  Instread,
// `flatMap` allows for an arbitrary number of elements to be returned per input
// element.  This is reflected in the fact that the function it takes returns
// `List[B]` as opposed to just `B`, as with `map`.  In this way, `flatMap` is
// more general than `map`.

// Now try it yourself.  Fill in the `???` below to make the assertion pass.

def allItemsPurchased2(customers: List[Customer]): List[Item] =
  myFlatMap(customers, (c: Customer) => c.purchaseHistory)

assert(allItemsPurchased(database) == allItemsPurchased2(database))

// `flatMap` is so general, in fact, that it can be used to implement both
// `map` and `filter`.  Try this yourself.  Fill in the `???` below to get
// the assertions to pass.

// HINT: if `flatMap` were to always return only single-element lists, then it
//       would behave just like `map` would.
def myMapUsingFlatMap[A, B](input: List[A], function: A => B): List[B] =
  myFlatMap(input, (item: A) => List(function(item)))

// HINT: if `flatMap` were to return either empty lists or single-element lists
//       depending on what the provided function returns for a given element, then
//       it would behave just like `filter` would.
def myFilterUsingFlatMap[A](input: List[A], function: A => Boolean): List[A] =
  myFlatMap(input, (item: A) => if (function(item)) List(item) else List())

assert((myMap(List(1, 2, 3), (i: Int) => i + 1) ==
        myMapUsingFlatMap(List(1, 2, 3), (i: Int) => i + 1)))
assert((myMap(List("moo", "cow", "bull"), (s: String) => s.length) ==
        myMapUsingFlatMap(List("moo", "cow", "bull"), (s: String) => s.length)))
assert((myMap(List(2.5, 3.0, 4.2), (d: Double) => d.floor) ==
        myMapUsingFlatMap(List(2.5, 3.0, 4.2), (d: Double) => d.floor)))
assert((myFilter(List(1, 2, 3), (i: Int) => i % 2 == 0) ==
        myFilterUsingFlatMap(List(1, 2, 3), (i: Int) => i % 2 == 0)))
assert((myFilter(List("moo", "cow", "bull"), (s: String) => s.length == 3) ==
        myFilterUsingFlatMap(List("moo", "cow", "bull"), (s: String) => s.length == 3)))


// Even though `flatMap` is clearly more powerful than either `map` or `filter`, it
// still has a major restriction on it: each item is processed independently of all
// others.  To see why this is such a major restriction, consider the problem of
// getting the summation of the elements of a list.  This function has a signature
// like the following:

// def mySum(ints: List[Int]): Int

// By the return type alone, we can immediately see that `foldLeft` is unapplicable.
// `flatMap` always returns a `List`, but here we need an `Int`.  Since `flatMap`
// is more general than `map` and `filter` (read: it can do everything those can
// do, plus more), we transitively also know that both `map` and `filter` cannot
// possibly work here.  Because of the type system, we don't even need to understand
// what these operators do to know they are unapplicable.  (This happens surprisingly
// often!)  For the moment, because we can't use any of the operators we've used so
// far, we have to define our own recursive function, like so:

def mySum(ints: List[Int]): Int =
  if (ints.isEmpty) 0 else ints.head + mySum(ints.tail)

assert(mySum(List(1, 1, 1)) == 3)
assert(mySum(List(1, 2, 3)) == 6)

// For the base case, we simply return 0, since given no numbers we have no
// value.  For the recursive case, we add the current element to the sum
// of the rest of the elements.

// Now consider another operation: concatenating a list of strings
// together.  That is, given List("moo", "cow", "bull"), we want to
// produce "moocowbull".  This has a signature like so:

// def concatAll(strings: List[String]): String

// Once again, because the return type isn't a list, we know immediately
// that `flatMap` is unapplicable.  Instead, we turn to a custom recursive
// function:

def concatAll(strings: List[String]): String =
  if (strings.isEmpty) "" else strings.head + concatAll(strings.tail)

assert(concatAll(List("moo", "cow", "bull")) == "moocowbull")

// Now the base case is an empty string, just to satisfy the return type.
// The empty string acts like 0 in this case.  In the recursive case, we
// concatenate the head element with the concatenation of the rest.

// Now for a third operation: getting the length of a list.
// This has a signature like the following:

// def myLength[A](list: List[A]): Int

// Because we can determine that `flatMap` is unapplicable from the return
// type, we again have to resort to using recursion, like so:

def myLength[A](list: List[A]): Int =
  if (list.isEmpty) 0 else 1 + myLength(list.tail)

assert(myLength(List(0, 1, 2)) == 3)
assert(myLength(List("moo", "cow", "bull", "foo", "bar", "baz")) == 6)

// Here the base case is an empty list, which is defined to have length 0.
// The recursive case is a non-empty list, which we know has length at least one.
// In the non-empty list case, if we know the length of the rest of the list,
// then we know that the length of the same list plus the additional element
// will add one to the overall length.  Note that we don't actually care
// about what element is on the front of the list, just that there is
// an element.  This makes sense, because if we say a list has length
// `5` then we don't really care if it contains `String`s or `Int`s.

// A pattern is beginning to emerge.  The only differences between
// `mySum` and `concatAll` are:
// 1.) The value returned when we reach the base case
// 2.) The operation to perform on the head of the list and the recursively
//     processed tail of the list.

// A first attempt at implementing this pattern is below:
def attempt1[A](items: List[A], f: (A, A) => A, base: A): A =
  if (items.isEmpty) base else f(items.head, attempt1(items.tail, f, base))

assert(attempt1(List(1, 1, 1), (i1: Int, i2: Int)=> i1 + i2, 0) == 3)
assert(attempt1(List(1, 2, 3), (i1: Int, i2: Int)=> i1 + i2, 0) == 6)
assert(attempt1(List("moo", "cow", "bull"),
                (s1: String, s2: String) => s1 + s2, "") == "moocowbull")
assert(attempt1(List(0, 1, 2), (i1: Int, i2: Int) => 1 + i2, 0) == 3)
// assert(attempt1(List("moo", "cow", "bull", "foo", "bar", "baz"),
//                 (i1: String, i2: Int) => 1 + i2, 0) == 6)

// This almost works.  As shown above, there is a commented-out assertion,
// as it contains a type error.  With `attempt1`, we specifically stated that
// the provided function takes two arguments of the same type, but for
// calculating the length of a list of `String`s, this assumption isn't true.
// As such, currently we can calculate the length of a list of `Int`s because
// the length (the returned value) is itself an `Int`, but we cannot calculate
// the length of a list of `String`s as the return type differs from the
// content type of the list.

// Something here should seem a little broken.  In our definition of `myLength`,
// we said that we didn't care what the list contained, but it matters to
// our `attempt1` definition.  If we look closely at the definition of
// `attempt1`, we can see that we could actually make it a bit more generic.
// Currently it enforces that the return type is the same as the content type
// of the list, but these two types can be independent.  As such, we can
// introduce another generic type variable, like so:

def attempt2[A, B](items: List[A], f: (A, B) => B, base: B): B =
  if (items.isEmpty) base else f(items.head, attempt2(items.tail, f, base))

assert(attempt2(List(1, 1, 1), (i1: Int, i2: Int)=> i1 + i2, 0) == 3)
assert(attempt2(List(1, 2, 3), (i1: Int, i2: Int)=> i1 + i2, 0) == 6)
assert(attempt2(List("moo", "cow", "bull"),
                (s1: String, s2: String) => s1 + s2, "") == "moocowbull")
assert(attempt2(List(0, 1, 2), (i1: Int, i2: Int) => 1 + i2, 0) == 3)
assert(attempt2(List("moo", "cow", "bull", "foo", "bar", "baz"),
                (i1: String, i2: Int) => 1 + i2, 0) == 6)

// With this more generic definition of `attempt2`, we can now handle all the
// assertions.  In fact, this is a much, much more powerful operator than
// anything we've shown previously.  This operator, can, in fact, implement
// `map`:

def myMap2[A, B](input: List[A], function: A => B): List[B] =
  attempt2(input, (head: A, rest: List[B]) => function(head) :: rest, List())

assert(myMap2(List(1, 2, 3), (i: Int) => i + 1) == List(2, 3, 4))
assert(myMap2(List("moo", "cow", "bull"), (s: String) => s.length) == List(3, 3, 4))
assert(myMap2(List(2.5, 3.0, 4.2), (d: Double) => d.floor) == List(2.0, 3.0, 4.0))

// This was made possible by allowing the result type to differ by
// the input type.  We can implement `filter` too:

def myFilter2[A](input: List[A], predicate: A => Boolean): List[A] =
  attempt2(input,
           (head: A, rest: List[A]) =>
             if (predicate(head)) head :: rest else rest,
           List())

assert(myFilter2(List(1, 2, 3), (i: Int) => i % 2 == 0) == List(2))
assert(myFilter2(List("moo", "cow", "bull"), (s: String) => s.length == 3) == List("moo", "cow"))

// `flatMap` is within our grasp:

def myFlatMap2[A, B](input: List[A], function: A => List[B]): List[B] =
  attempt2(input,
           (head: A, rest: List[B]) =>
             function(head) ++ rest,
           List())

assert((myFlatMap(List(1, 2, 3), (i: Int) => List(i - 1, i, i + 1)) ==
        myFlatMap2(List(1, 2, 3), (i: Int) => List(i - 1, i, i + 1))))

// Of course, we could also have implemented `myFlatMap2` first
// in terms of `attempt2`, and then implemented `myMap2` and `myFilter2` in
// terms of `myFlatMap2`.  Overall, `attempt2` is a very general operator, and
// can do a whole lot more than just what's shown here.

// There is one last detail to attend to (hinted by the fact that this
// operator was called `attempt2` instead of some funky-sounding name).
// You may have tried out a call to `attempt2` with a particularly large
// input, like (intentionally commented-out):

// attempt2(1.to(50000).toList, (i1: Int, i2: Int) => i1 + i2, 0)

// If you try running the above code, you'll almost assurredly be met
// with a stack overflow error.  Wow, recursion sucks.  We should use
// loops with mutable state for everything.

// Wrong!  Recursion doesn't suck, the recursive definition we're using
// here sucks.  Look again at the definition of `attempt2`:

// def attempt2[A, B](items: List[A], f: (A, B) => B, base: B): B =
//   if (items.isEmpty) base else f(items.head, attempt2(items.tail, f, base))

// Currently, we're calling `attempt2` recursively before we call `f`.
// This means that before any call to `f` actually occurs, at some point
// we must reach the base case first.  Mathematically there is nothing
// wrong with this, but this means extra work for a computer.  To make
// this recursive call, the system pushes onto the call stack, and the
// process repeats itself.  With a particularly large input, the call stack
// ends up becoming very deeply nested - specifically, for a list
// of length N, we end up with a call stack that is nested N levels deep.

// If you're used to recursion in many other languages, you probably
// saw this coming.  It is somewhat ingrained that recursion blows the
// stack, end of story.  This appears to be evidence to this fact -
// after all, we blew the stack.

// However, there is more to this story, and it's in an area that a lot
// of languages fall short.  Consider the following recursive definition
// of summation below:

def mySum2(ints: List[Int]): Int = {
  def usesRecursion(ints: List[Int], accum: Int): Int = {
    ints match {
      case Nil => accum
      case head :: tail => usesRecursion(tail, head + accum)
    }
  }

  usesRecursion(ints, 0)
}

assert(mySum2(1.to(50000).toList) > 0)

// Here, we define a nested function named `usesRecursion`, and call
// it recursively instead of `mySum2`.  `usesRecursion` has been
// instrumented with an additional parameter, which represents
// the sum as we are computing it.  This parameter is named
// `accum`, short for "accumulator".  Initially, the sum is 0.

// If you run this code you'll notice that nothing bad happens.  The
// stack doesn't blow up, and the sum is happily returned.  Why isn't
// this blowing the stack?

// The answer here is that the compiler is able to do a trick.
// In the recursive case, the last operation that must be
// performed is the recursive call.  There are no variables
// that need saving, nor any partial result that must be kept around.
// We could create a new stack frame, but this is kind of silly to do,
// since we have nothing to save on the frame.  As such, the compiler
// here is able to perform what is known as "tail call optimization":
// it can make execution jump back to the start of `usesRecursion`,
// without making a new stack frame.  With this optimization,
// `usesRecursion` needs only a constant amount of stack space,
// irrespective of the size of the input.  Neat!

// Many languages do not support this optimization, which is why
// recursion pretty much always can blow the stack in those languages.
// For functional langauges, this optimization is extremely important,
// since without it there is really no way to process large inputs
// recursively.

// This optimization, as the name suggests, only works if all recursive
// calls are "tail" calls.  That is, all recursive calls must be the last
// thing done.

// The name "accumulator" is very commonly used for any sort of variable
// which holds the result while a recursive computation is being performed.
// Oftentimes this refactor can make code a bit less clear, and so
// the name accumulator helps to point out to the reader what you're
// really trying to do here.

// Now that we've introduced tail call optimization and accumulators,
// we can go about fixing the issue where `attempt2` blows the stack on
// large inputs.  So let's add an accumulator:

def attempt3[A, B](items: List[A], f: (A, B) => B, base: B): B = {
  def usesRecursion(items: List[A], accum: B): B =
    if (items.isEmpty) accum else usesRecursion(items.tail, f(items.head, accum))
  usesRecursion(items, base)
}

assert(attempt3(List(1, 2, 3), (i1: Int, i2: Int)=> i1 + i2, 0) == 6)
assert(attempt3(1.to(50000).toList, (i1: Int, i2: Int) => i1 + i2, 0) > 0)
// assert(attempt3(List("moo", "cow", "bull"), (s1: String, s2: String) => s1 + s2, "") == "moocowbull")

// The good news is that we can handle large inputs now.  The
// bad news is that one of our previous tests broke.  What's happening is that
// before we were processing the list from right to left.  That is, calls
// to `f` were starting from the righthand side of the list.  Now, everything
// is happening from the lefthand side.  For summation, this is an irrelevant detail.
// However, for list concatentation, we end up getting a completely different result.

// It's easy enough to fix this by first reversing the input list, like so:

assert(attempt3(List("moo", "cow", "bull").reverse,
                (s1: String, s2: String) => s1 + s2, "") == "moocowbull")

// Now even though the list is still processed from left to right, we've
// reversed the ordering, and so we effectively process it from right
// to left.  This is an extra step, and oftentimes you're stuck with
// having to do an extra traversal somewhere to fix this problem.  That
// said, as shown by the summation, sometimes ordering doesn't matter,
// so you might as well process from the left.  Maybe your particular
// operation, in fact, must work from the left.  In this way, one
// operator isn't particularly better than another: they are used
// for different purposes.  In practice, oftentimes it's completely fine
// to process from the left to the right, so this is not at all a problem.

// As an aside, in the particular example above, we could have simply
// changed the function passed to `attempt3` a bit:

assert(attempt3(List("moo", "cow", "bull"),
                (s1: String, s2: String) => s2 + s1, "") == "moocowbull")

// ...though this won't work in general.

// If you're still in the imperative mindset, `foldLeft` is abstracting
// a particular loop pattern, shown below in pseudocode:

// retval = base
// for each element in list:
//   retval = operation(element, retval)
// return retval

// The above pseudocode is equivalent to the following call to `attempt3`:

// attempt3(list, operation, base)

// Now for getting back to what Scala gives.  This operator we've been
// calling `attempt` is really `fold`.  There is both a `foldLeft` and
// a `foldRight` operator, so named by the order in which the process
// list elements (`foldLeft` goes left to right, whereas `foldRight` goes
// right to left).  `foldRight` corresponds to `attempt2`, and `foldLeft`
// corresponds to `attempt3`. Both operators are defined in a curried way,
// where the first argument is the base value to operate on, and the
// second argument is a higher-order function.  For example:

assert(Seq(1, 2, 3, 4).foldLeft(0)(_ + _) == 10)

// For `foldLeft`, the higher-order function passed takes the accumulator
// and the current list value in that order, and `foldRight` takes the
// current list value and the accumulator in that order.

// Now try for yourself.  Fill in the `???` below to make the assertions pass:

// hint: List[T](), makes an empty list of type T

assert(Seq("moo", "cow", "bull").foldRight("")(_ + _) == "moocowbull")
assert(Seq(1, 2, 3).foldLeft(List[Int]())((b: List[Int], a: Int) => a :: b  ) == List(3, 2, 1)) // don't use reverse

// hint: Strings have a `length` method
assert(Seq("moo", "cow", "bull").foldRight(List[Int]())( (s: String, i: List[Int]) => s.length :: i) == List(3, 3, 4))


// One last operator is worth discussing explicitly on `Seq`.  Say we
// have two sequences of the same length, like so:

val l1_2 = Seq(1, 2, 3)
val l2_2 = Seq("moo", "cow", "bull")

// Say we wish to process these together.  That is, the fact that
// `1` is lined up with `"moo"` is significant, `2` with `"cow"`
// is significant, and so on.  With `foldLeft` alone, this isn't
// possible - `foldLeft` operates over a single sequence, whereas
// here we need to operate over multiple sequences.

// The solution here is a new operator, known as `zip`.  What
// `zip` does is combine two lists into a single list, where the
// result list is composed of tuples.  For example:

assert(l1_2.zip(l2_2) == Seq((1, "moo"), (2, "cow"), (3, "bull")))

// The syntax (a, b, ... z) creates a new tuple, a container with
// a fixed length holding items with possibly different types.  Now,
// thanks to `zip`, we have a single list that `foldLeft` or
// `foldRight` can operate over.

// NOTE ON GENERALITY:
//
// Throughout this section, we have discussed that certain operators
// are more general than others.  Specifically, `flatMap` is more
// general than either `filter` and `map`, `foldLeft`/`foldRight`
// are more general than `flatMap`, and explicitly writing
// a recursive function is more general than `foldLeft`/`foldRight`.
// More general operators can do more than less general operators.
// That said, if a less general operator works, then you should
// use the less general operator.  This point bears repeating:
//
// ---Use only the operator that gives you exactly as much power as you need.---
//
// For example, if there is a situation in which `map` could work, then
// use `map` instead of `flatMap`.  There are a multitude of reasons
// why:
//
// 1.) If a less general operator would work, then necessarily you are
//     duplicating code with the less general operator.  Duplicating code
//     is almost always a bad idea, not only because it bloats things, but
//     it is difficult to maintain.
//
// 2.) More general operators mean writing more code and more complex code -
//     there will be more parameters, more type parameters, and more
//     boilerplate.
//
// 3.) For someone reading the code, less general operators have immediately
//     known meanings, so there isn't much thinking through them.  In
//     contrast, more general operators take time to reason through, perhaps
//     significantly more time.
//
// Historically, students tend to write `foldRight` when `map` is applicable.
// Not only does this mean a lot more code, it's more error-prone, and people
// tend to define much, much less efficient versions than what `map` gives you.
// Given that `map` is what you typically end up wanting around 80% of the
// time, writing anything more complex has a multiplicative effect on overall
// code complexity.

