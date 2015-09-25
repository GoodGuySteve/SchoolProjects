// At this point we've discussed `class` and `object`.  This lesson builds off
// on top of these, introducing what is known in Scala as a "Companion Object".

// To run these, you will need to use `:paste` at the Scala REPL as opposed
// to load, and copy-and-paste the code into it.  For technical reasons,
// the REPL doesn't recognize companion classes correctly when `:load` is used.

// As mentioned before, `object` is often used to create helper methods and
// data for some other class.  If you're used to Java, this may seem a bit
// hacky at first.  Java lets you define all the helpers in the same class,
// so they are closely tied together.  With Scala, we must introduce them 
// elsewhere, so there is less of a strong link between the two.

// This is where companion objects come into play in Scala.  A companion object
// is an `object` that has the _exact same name_ as some other class defined
// _in the same file_.  Preferably, companion objects are defined before
// their corresponding classes in the file.  This looks like the following:

// Creates an empty class and an empty companion
object MyClass
class MyClass

// Companion objects allow us to clearly denote that a helper is for a specific
// class.  For example:

object OtherClass {
  def helper(state: Int): String = state.toString
}
class OtherClass(val state: Int) {
  val someInternalValue = OtherClass.helper(state)
}

// This may look a little klunky, since we needed to say `OtherClass.helper`
// as opposed to just helper.  We can fix this problem with `import`:

object OtherClass2 {
  def helper(state: Int): String = state.toString
}
class OtherClass2(val state: Int) {
  import OtherClass2._
  val someInternalValue = helper(state)
}

// Companion objects are also special in that they have access to private members
// of the class they are a companion for.  However, this is beyond the scope of
// this class.

// Now for some Scala-specific idioms which are related to companion objects.
// `apply` is a special method name that can exist on any class/trait/object, 
// and is implicitly called whenever a programmer tries to call the class/trait/object
// by itself.  If you're coming from a C++ background, implementing the
// `apply` method is much like overloading the call operator.  To see this
// in action, consider the code below:

class HasApply {
  def apply(): Int = 7
}

val hasApplyInstance = new HasApply
assert(hasApplyInstance() == 7)

// So what does this have to do with companion objects?  By convention, when
// `apply` is defined on a companion object, it usually returns an instance
// of the class it's a companion for.  For example:

object CompHasApply {
  def apply(state: Int): CompHasApply =
    new CompHasApply(state)
}

class CompHasApply(val state: Int)

// Now we can say:

val instance1 = new CompHasApply(7)
val instance2 = CompHasApply(7)
assert(instance1.state == instance2.state)

// Using the above pattern, we can save some typing when we make instances
// (i.e., we don't need to say `new` anymore), and this also allows us to
// easily intercept attempts to create instances of a particular object.
// This latter advantage is useful if we wanted to do extra error checking,
// track the number of objects made, etc.

// Now try it yourself.  Consider the code below:

object TrackingApply {
  // can't simply adjust _count
  private var _count = 0

  def count(): Int = _count

  def apply(): TrackingApply = {
    _count += 1
    new TrackingApply
  }
}
class TrackingApply

// Make the assertion below hold by replacing `???` with code
val first: TrackingApply = TrackingApply()
val second: TrackingApply = TrackingApply()
assert(TrackingApply.count == 2)

// In addition to `apply`, there is also `unapply`, which, from an intuitive
// standpoint, performs `apply` in reverse.  That is, given a class, it will
// get the contents of the class out.  `unapply` is used in pattern matching
// (more on that later).  For this class, you will never be required to write
// your own `unapply` method.  Just know that `unapply` is also defined
// on a companion class.
