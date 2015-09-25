// `val` is what you will always use to introduce variables.
// However, occasionally we will give you code that uses `var`.
// `var` is much like `val`, except that it allows for reassignment.
// For example, the code below works:

var someInteger: Int = 7
someInteger = 8

// ...whereas the code below would fail:

// val someInteger: Int = 7
// someInteger = 8

// If you have code with a `var` in it, you are free to mutate that
// particular variable.  However, you may not introduce new `var`s.

// Let's try this out.  Insert a line of code that will make the assertion
// below hold:

var assertMe: Int = 10
assertMe = 12
assert(assertMe == 12)
