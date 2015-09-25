// Now that we have seen a few different collections it is time to talk about
// an important syntactic feature of Scala: for comphrensions.

// We have already talked in depth about operations on collections such
// as map, filter, flatMap, and so on.

// For comphrensions provide a covienient syntactic abstraction for these
// operations.

// In its simplest form `for` resembles the for/in style loop that is present in C++11,
// and Java.

for (elem <- List(1,2,3)) {
    println(elem)
}

// This is the imperative form of `for` where the body of the loop is executed
// for its side effects, and we ignore its return value.

// As I just said `for` has a return value. This makes it much different than other
// looping constructs as it is an *expression* and not a statement. This
// means that a `for` always returns a value. When we use it in the traditional
// loop form like above it always returns `()` (pronounced unit).

// For is much more powerful and can be used to return a new collection directly.
val newList = for (elem <- List(1,2,3)) yield elem + 1

// When we postfix the for-comphrension with the keyword yield, it allows us to return
// a value back from each iteration.  In the above snippet we don't need
// to allocate a new List and append an element on each iteration, we can
// simply describe how to build a new List from the old one.

// We just described how to compute each new element from each old element.
// This sounds oddly familar right? If you remember the operation `map`,
// which takes a transforming function and applies it to each element, you
// may notice that these look startingly similar.

// Could we write the above operation as map? Let's try.

val newList2 = List(1,2,3).map(elem => elem + 1)

// If we look both compute the List(2,3,4), coincdentally
// this is actually what the compiler does internally. This
// means that any type supporting the map method (and a few others
// we will discuss can opt into the for-expression.

// These implications are broad and  means we can use this syntax
// on any type supporting the correct set of operations, even if
// it doesn't look like traditional looping.
// For example on `for` on `Option[T]` values.

Some(2) == (for (o <- Some(1)) yield o + 1)
None == (for (o <- (None: Option[Int])) yield o + 1)

// Here we the semantics of Option's map which cascades failure.

// `for` also supports and extended form with more than one
// binding clause (things of the form: `name <- collection`).

// For example if we want to compute *all* possible results
// of multiplying list elements we can simply do:
val result = for (x <- List(1,2,3); y <- List(4,5,6)) yield x * y

List(4,5,6,8,10,12,12,15,18) == result

// This relies on the behavior of flatMap the second
// operation types must implement to use `for`.

// If we desugar this example we will see:
List(1,2,3).flatMap(x => List(4,5,6).map(y => x * y))

// There are some nice theoretical reasons that this works out,
// but as a beginning Scala programmer you can just see that
// if flatMap, and map are implemented with correct type
// signatures you are able to use them on that type.

// The syntax is nice addition but adds nothing beyond the
// semantics of map, and flatMap.
