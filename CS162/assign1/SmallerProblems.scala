// These problems are extracted from "Programming Scala", by
// Dean Wampler.

object Problem1 {
    def main( args:Array[String] ) = {
      // Reverse each element of the args array and print out the
      // result. Note that the String class has a 'reverse' method.
      //
      // For example:
      //
      // scala Problem1 foo bar baz
      // oof rab zab
	  
	  for (x <- args) yield print(x.reverse + ' ')
	  print('\n')
	  
    }
}

object Problem2 {
  // A binary tree node.  The field `ord` is declared with
  // `var`, so it is mutable.  For example, you can do:
  //
  // val n = Node(...)
  // n.ord = (1 -> 2)
  //
  // Because we introduced the `var`, you may modify _this_ `var`.
  // You may not introduce any other `var`s.
  case class Node(var ord:(Int,Int), 
                  left:Option[Node],
                  right:Option[Node])

  def main( args:Array[String] ) = {
    // example tree
    val tree = Node( (-1,-1), 
      None,
      Some(Node( (-1,-1),
                Some(Node( (-1,-1), None, None )),
                Some(Node( (-1,-1), Some(Node( (-1,-1), None, None )), None ))
      ))
    )
    
    // set the tree nodes' labels and print the tree. note that case
    // classes are automatically given a toString method, so we don't
    // need to define our own.  Your solution must be general, in that
    // it can work with arbitrary trees.
    order( tree )
    println( tree )

    // For example:
    //
    // scala Problem2
    // Node((0,4),None,Some(Node((1,3),Some(Node((2,0),None,None)),Some(Node((3,2),Some(Node((4,1),None,None)),None)))))
  }

  def order( node:Node ) {
    // use a nested method inside this method as a helper function to
    // traverse the given tree and set each Node's 'ord' field to the
    // tuple '(preorder, postorder)', where 'preorder' is the Node's
    // preorder label and 'postorder' is the Node's postorder
    // label. For consistent numbers, visit left children before right
    // children. Labels should start at 0 (i.e., the root node's
    // preorder label should be 0).

	def recPreLabel(n: Node, preord: Int): Int = {
		
		n.ord = (preord -> -1)
		val lhs = 
			n.left match {
				case None => preord
				case x: Some[Node] => recPreLabel(x.get, preord + 1)
			}
		val rhs = 
			n.right match {
				case None => lhs
				case y: Some[Node] => recPreLabel(y.get, lhs + 1)
			}
		
		rhs
	}
	
	def recPostLabel(n: Node, postord: Int): Int = {
	
		val lhs = 
			n.left match {
				case None => postord
				case x: Some[Node] => recPostLabel(x.get, postord)
			}
		val rhs = 
			n.right match {
				case None => lhs
				case y: Some[Node] => recPostLabel(y.get, lhs)
			}
		n.ord = n.ord.copy(_2 = rhs + 1)
	
		rhs + 1
	}
	
	recPreLabel(node, 0)
	recPostLabel(node, -1)
	
    // As a hint, you'll need to use recursion here.  The nested
    // method should have an auxilliary parameter, representing the
    // currently available ID.  The nested method should return the
    // next available ID.  This is equivalent to an approach of
    // having a mutable variable elsewhere and incrementing it
    // each time we need a new ID, which is likely a more obvious
    // solution if you're coming from an imperative background.  This
    // is equivalent, because the mutable variable sets up an implicit
    // data dependency between recursive calls, whereas with functional
    // purity we must make this data dependency explicit.
  }
}

object Problem3 {
  def main( args:Array[String] ) = {
    val list = args.toList

	println(list.reverse.foldLeft("")((a: String, b: String) => if (a > b) a else b).toString)
	
	val ndList = list.foldLeft(List[String]())( (l: List[String], s: String) =>
		if(l.contains(s)) l
		else s :: l
	)
	println(ndList)
	
	val rtList = list.foldLeft(List[((String, Int))]())(
		(l: List[((String, Int))], s: (String)) =>
		if(l.map(_._1).contains(s)) { //TODO fix this condition
			val entry = l.filter(x => x._1 == s).head
			entry.copy(_2 = entry._2 + 1) :: l.filterNot(x => x._1 == s)
		}
		else ((s, 1)) :: l
	).reverse
	
	println(rtList)
	
    // Use the foldLeft method of list to print the following:
    //
    // 1. the largest element in args (using string comparison)
    // 2. args with no duplicate elements
    // 3. a run-length-encoded version of args

    // NOTES
    //
    // If the initial value given to foldLeft is an empty List you
    // need to explicitly give the type of the List, e.g., List[Int]()
    // or List[String](), otherwise the compiler won't be able to
    // figure out the types.
    //
    // To determine if a string `s1` is greater than another string `s2`,
    // you can use `>` like so: `s1 > s2`.  The `compareTo` method on
    // `String` can also be used.
    // 
    // You may use reverse as part of your solution.
    //
    // For run-length-encoding specifics, see
    // http://en.wikipedia.org/wiki/Run_length_encoding

    // For example:
    //
    // scala Problem3 foo bar bar baz moo moo moo cow
    // moo
    // List(foo, bar, baz, moo, cow)
    // List((foo,1), (bar,2), (baz,1), (moo,3), (cow,1))
  }
}
