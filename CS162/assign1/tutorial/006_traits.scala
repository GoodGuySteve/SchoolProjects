// In `objects.scala`, we showed some basic inheritance at work.
// Inheritance allows us to reuse existing code without doing
// nasty things like copying and pasting code around.  However,
// inheritance tends to be kind of limited.  Inheritance often
// assumes that objects fall into a strict tree-like hierarchy,
// which is not particularly realistic.  For example, consider
// a comparison-based sorting algorithm.  Such an algorithm 
// only cares about whether or not for two given inputs, which
// one comes first.  For example:

// All the algorithm cares about
abstract class ComparableItem {
  // returns -1 for before, 0 for equal, or 1 if after.
  // No need to say `abstract` - the compiler can tell already
  // because the method has no body.
  def compare(first: ComparableItem, second: ComparableItem): Int
}

class MyInteger(val value: Int) extends ComparableItem {
                                                            // YOU DON'T HAVE TO FILL THIS ONE IN
  def compare(first: ComparableItem, second: ComparableItem): Int = ??? // compare the integers
}

// The above code works great, assuming you're ok with enforcing that
// `ComparableItem` acts as the base class for anything you'd ever want
// to compare.  This doesn't work well for most things, including integers:

// Defining a hierarchy for Numbers
abstract class MyNumber
class MyInteger2(val value: Int) extends MyNumber // extends ComparableItem ?

// Now we have a problem.  `MyInteger` should extend both `ComparableItem` and
// `MyNumber`.  However, strict inheritance doesn't let us do this.
// We could do something like making `MyNumber` extend `ComparableItem`, but this
// isn't quite right either - conceivably, we could devise numbers for which
// there is no real ordering, like credit card numbers.

// Scala solves this problem by the use of traits, introduced with the
// `trait` reserved word.  Traits behave much like abstract classes, except
// their constructors are not permitted to take parameters.  In return,
// Scala allows classes to inherit from multiple traits.  For example, we could do:

trait ComparableItemTrait {
  def compare(first: ComparableItemTrait, second: ComparableItemTrait): Int
}

trait MyNumberTrait

class MyIntegerClass(val value: Int) extends ComparableItemTrait with MyNumberTrait {
  def compare(first: ComparableItemTrait, second: ComparableItemTrait): Int = ???
}

// We must first use `extends`, and then `with`.  If we wanted to extend from
// multiple things, we'd do something like:

// class MyClass extends First with Second with Third

// This begs the question: how is this different from interfaces in Java?
// The difference is that traits also allow concrete members to be defined.
// For example, we can do:

trait ComparableItemTrait2 {
  def compare(first: ComparableItemTrait2, second: ComparableItemTrait2): Int

  def lessThan(first: ComparableItemTrait2, second: ComparableItemTrait2): Boolean = 
    compare(first, second) == -1

  def greaterThan(first: ComparableItemTrait2, second: ComparableItemTrait2): Boolean = 
    compare(first, second) == 1

  def equal(first: ComparableItemTrait2, second: ComparableItemTrait2): Boolean = 
    compare(first, second) == 0
}

// Let's put all this to work.  Write a trait named `MyTrait` that would make the
// code below work (i.e., not throw any assertions when run).  `MyTrait` should
// inherit from `Base`

trait Base {
  def unknown(): Int

  def known(): Int =
    unknown + 10
}

// DEFINE MyTrait BELOW
trait MyTrait extends Base {
	def unknown(): Int = 9
}

class Working extends MyTrait {
  def result(): Int = known
}

assert((new Working).result == 19)

