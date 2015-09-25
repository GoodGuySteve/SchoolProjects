// This is a single-line comment

/* This is a block comment
   It can span multiple lines */

// This lesson simply goes through the basics of how to run things and
// read this tutorial.

// For one, I frequently use backticks (`this is in backticks`) to
// denote code in comments.  For example `x + y` refers to the variable `x`
// plus the variable `y`. Except where explicitly stated, the
// backticks shouldn't actually be put in your code.

// As noted in the introduction there are a couple of ways to execute
// Scala code.

// Similar to Java, Scala can be compiled to Java bytecode,
// and excuted directly. This can be done by invoking `scalac file.scala`,
// and then invoking `scala file` to execute the compiled program.

// You can alternatively just invoke `scala` with no arguments to load
// the REPL. This environment has slightly different behavior
// than compiling a Scala source file.

// In Scala source files all code must be inside a top level
// declaration like an object, class, or trait. This restriction is
// relaxed inside the REPL so you are able to enter program fragments
// directly.

// There is also a confusing usuage of the `scala` tool.
// It is possible to directly run a Scala source file without
// invoking the compiler by using the `scala` command this  may look the
// same as invoking `scalac` and then executing the program but
// it obeys the rules of the REPL, not those of the full langauge.

// This means a few important restrictions of the REPL are enforced.
// The most important of which is that you can not declare packages
// when executing in script-mode.
//
// This is important to remember as you write code, because
// each execution strategy has subtle differences.

// With that said its now time for some actual practice.
// At the terminal, go to the directory where the tutorial is and type
// `scala` to load the REPL.  After a few seconds, you'll
// be met with a prompt like so:

// scala>

// Here you can start typing in code, and see how it will get evaluated.
// This is called a read-eval-print-loop, or REPL for short, since it
// behaves by reading your code, evaluating it, printing it out, and then
// looping that entire process.

// Below is an example of me using the REPL:

// scala> 1 + 2
// res0: Int = 3

// Go ahead and try it out.  The REPL is a particularly nice feature,
// as it lets you try out snippets of code before you encorporate them
// into something larger.  This is a lot more lightweight than writing
// some test code into a file and going through the compiler, and can
// get you through a task faster.

// Now for a REPL command.  Type the following in the REPL:

// :load 0_basics.scala

// Assuming your in the right directory, the REPL should respond
// like so:

// scala> :load 0_basics.scala
// Loading 0_basics.scala...

// scala>

// It seems like absolutely nothing happened.  However, in reality,
// the REPL read in `0_basics.scala`, and executed it.  It just so
// happens that `0_basics.scala` doesn't contain any executable code; it's
// all just a lot of comments.  Starting with the next lesson, there will
// be actual code that can be executed.  For each lesson, you can load
// it into the REPL with :load, and be on your way.

// It is assumed that all these files will be loaded from the REPL.  The
// majority of them won't compile as-is, because the REPL lets you get
// away with having definitions out in the ether, as opposed to tied down
// to some class.  Rather than make everything compile up front and just
// treat a whole lot as a magical incantation necessary to make things
// compile, I favor the REPL approach.

// Occasionally, examples will say to replace `???` with some code.
// `???` can be thought of a sort of placeholder that always
// returns something of the appropriate type.  However, any attempts
// to execute `???` throw exceptions.  It actually takes a fair amount
// of explaining to get at what `???` does, which is beyond the
// scope of this tutorial.  For the curious, there isn't actually
// anything special about `???`; it's just a regular old library method.

// For the tutorial, you are expected to fill in code at `???` and occasionally
// define methods, all in the name of getting some assertions to pass and
// to make the REPL not spew out errors.
