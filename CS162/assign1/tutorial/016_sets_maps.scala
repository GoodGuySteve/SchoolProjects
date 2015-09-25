// This lesson discusses Sets and Maps in Scala, two very commonly
// used data structures. You're probably already familiar with sets
// and maps from other languages, but they work a little differently
// than you might expect in Scala.

// If you're coming from an imperative background, you're probably
// used to the idea of adding elements to a set.  In Scala, you can
// add or remove elements from a set, per se, but the behavior
// is a little different.  To demonstrate:

val s1 = Set(1, 2, 3)
s1 - 2 // removes 2 from the set
assert(s1 == Set(1, 2, 3))

// The assertion above holds.  How?

// The answer is that sets, much like sequences, are immutable.  You
// cannot add or remove elements to an existing set; once a set has
// been made, it will always remain in the state it was when it
// was first made.  This may sound very restrictive, but in practice,
// I assure you it's not.  The usual set operations to add and remove
// elements are still there, only now they return _new sets_, as opposed
// to simply being `void`.  For example:

assert((s1 - 2) == Set(1, 3))
assert((s1 + 4) == Set(1, 2, 3, 4))

// As shown, `+` is a method that adds an element to a set, and `-`
// is a method that removes an element from a set.  To test if
// a set contains an element, use `contains`:

assert(s1.contains(1))

// Now let's do something more complex.  Let's add multiple elements
// to an existing set:

def addMultiple[T](existing: Set[T], items: Seq[T]): Set[T] =
  items.foldLeft(existing)(_ + _)

assert(addMultiple(Set(1, 2, 3), Seq(4, 5, 6)) == Set(1, 2, 3, 4, 5, 6))

// There is also an existing `++` method on `Set` that does the same thing:

assert((Set(1, 2, 3) ++ Seq(4, 5, 6)) == Set(1, 2, 3, 4, 5, 6))

// Try it yourself.  Add or remove elements to `baseSet` to make the following
// assertions hold:

val baseSet = Set(1, 2, 3)
val set1: Set[Int] = baseSet + 7
val set2: Set[Int] = baseSet - 1
val set3: Set[Int] = baseSet

assert(set1.contains(7))
assert(!set2.contains(1))
assert(set3.contains(1))

// Just like `Seq`, `Set` has its own version of `map`, `filter`, and `foldLeft`.
// For example:

assert(Set(1, 2, 3).map(_ + 1) == Set(2, 3, 4))
assert(Set(1, 2, 3, 4).filter(_ < 3) == Set(1, 2))
assert(Set(1, 2, 3, 4).foldLeft(0)(_ + _) == 10)

// The only notable difference between these operations on `Set` and these
// operations on `Seq` is that basic `Set`'s have no defined ordering, which
// may be important for your particular application.

// Now for Maps.  Maps consist of a series of key/value pairs.  While
// manipulating maps, these pairs can be specified directly using tuples
// holding two elements, like so:

val m1 = Map((1, 2), (3, 4))
m1 + ((5, 6)) // extra parentheses needed

// Sometimes all those extra parentheses get annoying, so Scala has
// an alternative form for specifying pairs, using `->`:

assert(((5, 6)) == 5 -> 6)

// It's beyond the scope of this introduction to explain exactly how
// `->` works, but it's safe to think of it as just another way to
// make a tuple of two elements.  (For the curious, `->` is not
// a special operator or anything like that, which may be surprising.)

// To add an element to a Map, then `+` is used, like so:
assert((Map(1 -> 2) + (3 -> 4)) == Map(1 -> 2, 3 -> 4))

// `+` will return a new map that overwrites whatever the old
// binding for the key was.  With this in mind, `+` can also
// be used to update keys in existing maps:

assert((Map(1 -> 2) + (1 -> 3)) == Map(1 -> 3))

// To remove an element from a Map, then you can use `-`, like so:

assert((m1 - 1) == Map(3 -> 4))

// You need only specify the key to remove when removing an element.

// For testing if a given map holds a given key, use the `contains`
// method, like so:

assert(m1.contains(1))

// For the Java folks, the method to use really is `contains`, not
// `containsKey` (which doesn't exist on Scala's `Map`).

// Now for lookups.  `Map` has an `apply` method defined on it which
// performs lookup.  As such, you can do things like the following:

assert(m1(1) == 2)

// However, you should only use the `apply` method whenever you know
// for certain that an element is contained in the map.  Otherwise,
// `apply` will throw an exception.  If you're not sure, you can
// concisely combine both the `contains` and the `apply` using `get`:

val gotten1: Option[Int] = m1.get(1)
val gotten2: Option[Int] = m1.get(5)

assert(gotten1.isDefined && gotten1.get == 2)
assert(gotten2.isEmpty)

// `get` will return Some(item) if the given key exists, else None.
// With an Option in hand, you can manipulate it as in `option.scala`.

// Now try it yourself.  Use `apply` to get the value for the key `3` below
// from the map `m1`

assert(m1.get(3).get == 4)

// Use `get` to get `7` from `m1`:

assert(m1.get(7) == None)

// Use `get` to get `3` from `m1`:

assert(m1.get(3) == Some(4))

// Now for some additional operations on `Map`s.  The familiar operations
// `filter`, `map`, and `foldLeft` are all there.  A key difference is
// that for these operations, a map is viewed as a sequence of
// key/value pairs, with no particular ordering guaranteed.  For example:

// ._1, ._2 get the first and second elements of a tuple, respectively
assert(Map(1 -> 2, 3 -> 4).map(pair => pair._2 -> pair._1) == Map(2 -> 1, 4 -> 3))
assert(Map(1 -> 2, 3 -> 4, 5 -> 1).filter(pair => pair._1 > pair._2) == Map(5 -> 1))

// There are two specialized versions of `filter` and `map` that exist
// on `Map`, representing common operations.  The first of these
// is `filterKeys`.  `filterKeys` can be used to remove keys from a map
// which do not match some given predicate.  For example:

assert(Map(1 -> 2, 3 -> 4, 5 -> 6).filterKeys(_ > 3) == Map(5 -> 6))

// If you need to look at the value, then the more general `filter` is applicable.

// The specialized version of `map` is `mapValues`, which works like `map`
// but is applied to only the values contained in the `Map`.  For example:

assert(Map(1 -> 2, 3 -> 4).mapValues(_ + 1) == Map(1 -> 3, 3 -> 5))

// Now try it yourself.  Fill in the `???` below with code in order
// to make the assertions pass.

assert(Map(1 -> 2, 3 -> 4).mapValues(_ * -1) == Map(1 -> -2, 3 -> -4))
assert(Map(1 -> 2, 3 -> 4).filterKeys(_ > 2) == Map(3 -> 4))

