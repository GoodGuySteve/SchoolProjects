// For this portion of the assigmment, you will implement a generic list,
// complete with some of the operations that already exist on Scala's `List`
// class.  Many portions of this assignment are covered directly in the
// tutorial, and you're free to copy code over here as you see fit.  That
// said, do take the time to understand how these different operations work -
// simply blindly copying may get you through this assignment, but it will
// leave you behind in subsequent assignments.    

// Companion class to MyList.  This defines an `apply` method which
// can be used to construct new lists succinctly
object MyList {
  // creates a new list (convenience method)
  def apply[A](items: A*): MyList[A] =
    items.foldRight(MyNil[A](): MyList[A])((cur, res) =>
      MyCons(cur, res))
}

// thrown on invalid operations
case class InvalidOperationException(msg: String) extends Exception(msg)

// This is a basic list trait from which everything else inherits from.
// You should _not_ modify anything within this trait.
sealed trait MyList[A] {
  // The `map` method, described at length in `seq.scala`.
  // Given an existing list, this should return a new list, where
  // each element of the new list is derived by calling the provided
  // function `f` on each element of the old list.  For example:
  //
  // MyList(1, 2, 3).map((i: Int) => i + 1) == MyList(2, 3, 4)
  def map[B](f: A => B): MyList[B]

  // `flatMap` behaves much like `map`, except it requires that
  // the passed function return a list.  These resulting lists
  // are then appended to each other.  It can be thought of
  // performing a `map`, and then finally appending together
  // the results.  For example:
  //
  // MyList(1, 2).flatMap((i: Int) => MyList(i, i + 1))
  //
  // ...should result in:
  //
  // MyList(1, 2, 2, 3)
  def flatMap[B](f: A => MyList[B]): MyList[B]

  // The `filter` method, described at length in `seq.scala`.
  // Given an existing list and a predicate, it will return a
  // new list which contains all elements of the old list that
  // were true according to the provided predicate
  def filter(pred: A => Boolean): MyList[A]

  // Appends two lists together.  For example:
  //
  // MyList(1, 2, 3).append(MyList(8, 9, 10))
  //
  // ...should result in:
  //
  // MyList(1, 2, 3, 8, 9, 10)
  def append(other: MyList[A]): MyList[A]

  // The `foldLeft` method, described at length in `seq.scala`.
  // Given some initial value (`initial`), it will process the
  // list from left to right, incrementally applying the
  // provided function `f`
  def foldLeft[B](initial: B)(f: (B, A) => B): B

  // The `foldRight` method, described at length in `seq.scala`.
  // `foldRight` behaves much like `foldLeft`, except that it
  // processes the list from right to left
  def foldRight[B](initial: B)(f: (A, B) => B): B

  // Takes up to `n` elements from the front of the list.
  // If the list is of length < `n`, then it returns the
  // original list.  For example:
  //
  // MyList(2, 3).take(1) == MyList(2)
  // MyList(2, 3).take(3) == MyList(2, 3)
  def take(n: Int): MyList[A]

  // Drops up to the first `n` elements from the front
  // of the list.  If the list is of length < `n`, then
  // it returns the empty list.  For example:
  //
  // MyList(2, 3).drop(1) == MyList(3)
  // MyList(2, 3).drop(2) == MyList()
  def drop(n: Int): MyList[A]

  // Gets the first element of the list.
  // Throws an `InvalidOperationException` if the list is empty.
  // For example:
  //
  // throw InvalidOperationException("head called on empty list")
  def head: A

  // Gets the tail of the list.
  // For example:
  //
  // MyList(1, 2, 3).tail == MyList(2, 3)
  //
  // If called on an empty list, it throws an `InvalidOperationException`
  def tail: MyList[A]

  // Gets the "init" of a list, which is every element of the list
  // _except_ for the last element.  For example:
  //
  // MyList(1, 2, 3).init == MyList(1, 2)
  //
  // If called on an empty list, it throws an `InvalidOperationException`
  def init: MyList[A]

  // Gets the last element of a list.  For example:
  //
  // MyList(1, 2, 3).last == 3
  //
  // If called on an empty list, it throws an `InvalidOperationException`
  def last: A

  // Returns `Some(head)`, where `head` is the head element of the list.
  // Returns `None` if called on an empty list.  For example:
  //
  // MyList(1, 2, 3).safeHead == Some(1)
  // MyList().safeHead == None
  def safeHead: Option[A]

  // Returns `Some(tail)`, where `tail` is the tail of the list.
  // Returns `None` if called on an empty list.  For example:
  //
  // MyList(1, 2, 3).safeTail == Some(MyList(2, 3))
  // MyList(1).safeTail == Some(MyList())
  // MyList().safeTail == None
  def safeTail: Option[MyList[A]]

  // Returns true if the list is empty, else false.  For example:
  //
  // MyList(1, 2, 3).isEmpty == false
  // MyList().isEmpty == true
  def isEmpty: Boolean

  // Returns the length of the list, i.e., the number of elements
  // contained within.  For example:
  //
  // MyList(1, 2, 3).length == 3
  // MyList().length == 0
  def length: Int
}

// To implement this, you will use the inductive definition of
// a list.  That is, a list is either a cell containing an element and the
// rest of the list, or an empty list.  To be consistent with existing
// documentation on lists, we will call the non-empty case `Cons`, short
// for "construct" for constructing lists.
case class MyCons[A](x: A, xs: MyList[A]) extends MyList[A] {
  // Note: all the methods below are implicitly specialized for
  // non-empty lists, since these are defined on a Cons.

  def map[B](f: A => B): MyList[B] = 
	MyCons[B](f(x), xs.map(f))
  
  def flatMap[B](f: A => MyList[B]): MyList[B] = 
	f(head).append(tail.flatMap(f))

  def filter(pred: A => Boolean): MyList[A] = 
	if (pred(x)) MyCons[A](x, xs.filter(pred))
	else xs.filter(pred)
  
  def append(other: MyList[A]): MyList[A] = {
  
  if (tail.isEmpty) MyCons[A](head, other)
  else MyCons[A](head, tail.append(other))
  
  }
	

  def foldLeft[B](initial: B)(f: (B, A) => B): B = {

	def usesRecursion(items: MyList[A], accum: B): B =
		if (items.isEmpty) accum else usesRecursion(items.tail, f(accum, items.head))
	usesRecursion(this, initial)
	
  }
  
  def foldRight[B](initial: B)(f: (A, B) => B): B = {
	f(x, xs.foldRight(initial)(f))
  }
  
  def take(n: Int): MyList[A] = {
  
  if (n > 0) MyCons[A](head, tail.take(n-1))
  else MyNil[A]
  }

  def drop(n: Int): MyList[A] = {
  
  if (n > 0) tail.drop(n-1)
  else this
  }

  def head: A = x

  def tail: MyList[A] = xs

  def init: MyList[A] = {
  
  if (tail.isEmpty) MyNil[A]()
  else MyCons[A](head, tail.init)
  }

  def last: A = {
  
	if (xs.isEmpty) x
	else xs.last
  }
  
  def safeHead: Option[A] = Some(x)

  def safeTail: Option[MyList[A]] = Some(xs)

  def isEmpty: Boolean = false

  def length: Int = 1 + xs.length
}

// This is the empty list case, AKA Nil.
case class MyNil[A]() extends MyList[A] {
  // Note: all the methods below are implicitly specialized for
  // empty lists, since these are defined on Nil

  def map[B](f: A => B): MyList[B] = 
	MyList()
  
  def flatMap[B](f: A => MyList[B]): MyList[B] = MyNil[B]()

  def filter(pred: A => Boolean): MyList[A] = MyNil[A]()

  def append(other: MyList[A]): MyList[A] = other

  def foldLeft[B](initial: B)(f: (B, A) => B): B = initial
  
  def foldRight[B](initial: B)(f: (A, B) => B): B = initial

  def take(n: Int): MyList[A] = MyNil[A]()

  def drop(n: Int): MyList[A] = MyNil[A]()

  def head: A = throw new InvalidOperationException("head called on empty list")

  def tail: MyList[A] = throw new InvalidOperationException("tail called on empty list")

  def init: MyList[A] = throw new InvalidOperationException("init called on empty list")

  def last: A = throw new InvalidOperationException("last called on empty list")

  def safeHead: Option[A] = None

  def safeTail: Option[MyList[A]] = None

  def isEmpty: Boolean = true

  def length: Int = 0
}

object Tester {
  case class Test[T](name: String, f: () => T, compare: T) {
    lazy val saw = try {
      f()
    } catch {
      case _: NotImplementedError => "NOT IMPLEMENTED"
    }
    lazy val passed = saw == compare

    def asString(): String = {
      val passedString = 
        if (passed) "passed" else "FAILED"
      val afterHeader =
        if (passed) "" else "\n\tSaw: " + saw + "\n\tExpected: " + compare
      name + ": " + passedString + afterHeader
    }
  }

  case class TestSuite(tests: Seq[Test[_]]) {
    def runSuite() {
      tests.foreach(test => println(test.asString))
    }
  }

  val l1 = MyList(1, 2, 3)
  val l2 = MyList("moo", "cow", "bull")
  val empty = MyList[Int]()

  def mapTests(): Seq[Test[_]] = {
    val add = (x: Int) => x + 1

    Seq(
      Test("map works on empty lists",
           () => empty.map(add), empty),
      Test("map works with numbers",
           () => l1.map(add), MyList(2, 3, 4)),
      Test("map works with strings",
           () => l2.map((s: String) => s.length),  MyList(3, 3, 4)))
  }

  def flatMapTests(): Seq[Test[_]] = {
    val flatAdd = (i: Int) => 
      MyList(i - 1, i, i + 1)
    val nums = 
      MyList(0, 1, 2, 1, 2, 3, 2, 3, 4)
    val strs =
      MyList("", "moo", "", "cow", "", "bull")

    Seq(
      Test("flatMap works on empty lists",
           () => empty.flatMap(flatAdd), empty),
      Test("flatMap works with numbers",
           () => l1.flatMap(flatAdd), nums),
      Test("flatMap example",
           () => MyList(1, 2).flatMap((i: Int) => MyList(i, i + 1)),
           MyList(1, 2, 2, 3)),
      Test("flatMaps works with strings",
           () => l2.flatMap((s: String) => MyList("", s)), strs))
  }
  
  def filterTests(): Seq[Test[_]] = {
    val nums = MyList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val pred = (i: Int) => i > 5

    Seq(
      Test("filter works on empty lists",
           () => empty.filter(pred), empty),
      Test("filter works on nums",
           () => nums.filter(pred), MyList(6, 7, 8, 9, 10)),
      Test("filter works on strings",
           () => l2.filter((s: String) => s.length == 3),  MyList("moo", "cow")))
  }

  def appendTests(): Seq[Test[_]] = {
    val strs =
      MyList("moo", "cow", "bull", "moo", "cow", "bull")

    Seq(
      Test("append works with two empty lists",
           () => empty.append(empty), MyList[Int]()),
      Test("append works with an empty list on the left",
           () => empty.append(l1), l1),
      Test("append works with an empty list on the right",
           () => l1.append(empty), l1),
      Test("append works with the same numbers",
           () => l1.append(l1), MyList(1, 2, 3, 1, 2, 3)),
      Test("append works with different numbers",
           () => l1.append(MyList(8, 9, 10)), MyList(1, 2, 3, 8, 9, 10)),
      Test("append works with strings",
           () => l2.append(l2), strs))
  }

  val sum = (x: Int, y: Int) => x + y
  val concat = (x: String, y: String) => x + y

  def foldLeftTests(): Seq[Test[_]] = {
    Seq(
      Test("foldLeft works with an empty list of integers",
           () => MyList[Int]().foldLeft(5)(sum), 5),
      Test("foldLeft works with an empty list of strings",
           () => MyList[String]().foldLeft("foo")(concat), "foo"),
      Test("foldLeft works with a non-empty list of integers",
           () => l1.foldLeft(0)(sum), 6),
      Test("foldLeft works with a non-empty list of strings",
           () => l2.foldLeft("")(concat), "moocowbull"),
      Test("foldLeft works with a Map",
           () => MyList(1 -> 2, 1 -> 3).foldLeft(Map[Int, Int]())((res, cur) => res + cur),
           Map(1 -> 3)))
  }

  def foldRightTests(): Seq[Test[_]] = {
    Seq(
      Test("foldRight works with an empty list of integers",
           () => MyList[Int]().foldRight(5)(sum), 5),
      Test("foldRight works with an empty list of strings",
           () => MyList[String]().foldRight("foo")(concat), "foo"),
      Test("foldRight works with a non-empty list of integers",
           () => l1.foldRight(0)(sum), 6),
      Test("foldRight works with a non-empty list of strings",
           () => l2.foldRight("")(concat), "moocowbull"),
      Test("foldRight works with a Map",
           () => MyList(1 -> 2, 1 -> 3).foldRight(Map[Int, Int]())((cur, res) => res + cur),
           Map(1 -> 2)))
  }

  val e = MyList(2, 3)

  def takeTests(): Seq[Test[_]] = {
    Seq(
      Test("take works on an empty list",
           () => empty.take(7), empty),
      Test("take works on an exact list",
           () => l1.take(3), l1),
      Test("take works on an inexact list",
           () => l2.take(2), MyList("moo", "cow")),
      Test("take example 1",
           () => e.take(1), MyList(2)),
      Test("take example 2",
           () => e.take(3), MyList(2, 3)))
  }

  def dropTests(): Seq[Test[_]] = {
    Seq(
      Test("drop works on an empty list",
           () => empty.drop(7), empty),
      Test("drop works on an exact list",
           () => l1.drop(3), empty),
      Test("drop works on an inexact list",
           () => l2.drop(2), MyList("bull")),
      Test("drop example 1",
           () => e.drop(1), MyList(3)),
      Test("drop example 2",
           () => e.drop(2), MyList[Int]()))
  }

  def testThrowsInvalid(name: String, operation: () => Unit): Test[_] = {
    Test(name, () => try { 
      operation()
    } catch { 
      case _: InvalidOperationException => 1
    }, 1)
  }

  def headTests(): Seq[Test[_]] = {
    Seq(
      Test("head works on a non-empty list",
           () => l1.head, 1),
      testThrowsInvalid("head throws an exception on an empty list",
                        () => MyList[String]().head))
  }

  def tailTests(): Seq[Test[_]] = {
    Seq(Test("tail works on a length 3 list",
             () => l1.tail, MyList(2, 3)),
        Test("tail works on a length 1 list",
             () => MyList("moo").tail, MyList[String]()),
        testThrowsInvalid("tail throws an exception on an empty list",
                          () => empty.tail))
  }

  def initTests(): Seq[Test[_]] = {
    Seq(Test("init works on a length 3 list",
             () => l1.init, MyList(1, 2)),
        Test("init works on a length 1 list",
             () => MyList("moo").init, MyList[String]()),
        testThrowsInvalid("init throws an exception on an empty list",
                          () => empty.init))
  }

  def lastTests(): Seq[Test[_]] = {
    Seq(Test("last works on a length 3 list",
             () => l1.last, 3),
        Test("last works on a length 1 list",
             () => MyList("moo").last, "moo"),
        testThrowsInvalid("last throws an exception on an empty list",
                          () => empty.last))
  }

  def safeHeadTests(): Seq[Test[_]] = {
    Seq(Test("safeHead works on a length 3 list",
             () => l1.safeHead, Some(1)),
        Test("safeHead works on a length 1 list",
             () => MyList("moo").safeHead, Some("moo")),
        Test("safeHead works on an empty list",
             () => empty.safeHead, None))
  }

  def safeTailTests(): Seq[Test[_]] = {
    Seq(Test("safeTail works on a length 3 list",
             () => l1.safeTail, Some(MyList(2, 3))),
        Test("safeTail works on a length 1 list",
             () => MyList("moo").safeTail, Some(MyList[String]())),
        Test("safeTail works on an empty list",
             () => empty.safeTail, None))
  }

  def isEmptyTests(): Seq[Test[_]] = {
    Seq(Test("isEmpty works on a non-empty list",
             () => MyList("moo").isEmpty, false),
        Test("isEmpty works on an empty list",
             () => empty.isEmpty, true))
  }

  def lengthTests(): Seq[Test[_]] = {
    Seq(Test("length works on a list of length 3",
             () => l1.length, 3),
        Test("length works on a list of length 1",
             () => MyList("moo").length, 1),
        Test("length works on an empty list",
             () => empty.length, 0))
  }

  def allTests(): Seq[Test[_]] = {
    (mapTests ++ flatMapTests ++ filterTests ++ appendTests ++
     foldLeftTests ++ foldRightTests ++ takeTests ++ dropTests ++
     headTests ++ tailTests ++ initTests ++ lastTests ++
     safeHeadTests ++ safeTailTests ++ isEmptyTests ++ lengthTests)
  }

  def main(args: Array[String]) {
    TestSuite(allTests).runSuite()
  }
}
