// This concludes the introduction to Scala for CS162.  You've covered
// a lot of ground in a short time, but you've really only scratched the
// surface of what Scala has to offer.  For the most part, we assume
// of you only what was covered in this tutorial.  That said, there
// are many places that if you go look at the APIs online, you'll
// find a nice method that does something that you want already.
// A large part of writing short, idiomatic Scala code is in taking
// advantage of what the library writers provide, and it's simply
// not possible to go over all that content in a minimalistic tutorial.

// Don't be afraid to Google around for help.  Of specific importance
// are the objects themselves.  For example, if you have a `Map` and you're
// stuck, then Googling for `scala Map` will give you the Scala standard
// library definition for `Map`, which includes all the methods defined
// on `Map` along with their signatures.  Because Scala is purely
// object-oriented, it must be the case that all Scala library code is
// defined in a class somewhere in the standard library.

// Specifically with this class, for the assignments, you should be compiling
// your code, not loading with `scala` directly.  This bears repeating:
//
// ---For your assignments, you should compile your code and run the compiled versions.---
//
// To illustrate, the code in this file is intended to be compiled.  You can
// compile this code with:
//
// scalac 018_moving_forward.scala
//
// Once compiled, you can run the code like so:
//
// scala MovingForward 1 2 3
//
// where `1`, `2`, and `3` are command-line arguments to the `main` method
// of the `MovingForward` object below.

object MovingForward {
  // `main` always takes an `Array[String]`.  We simply print out
  // each of the arguments, one per line.
  def main(args: Array[String]) {
    args.foreach(arg => println(arg))
  }
}

