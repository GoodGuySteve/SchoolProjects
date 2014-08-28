#ifndef BST_H
#define BST_H
#include "data_structures.h"

template <class T>
class BST {
 private:
  BinaryNode<T> *root;
 public:
  /* Construct an empty Binary Search Tree */
  BST(): root(NULL) {}

  /* Destruct a binary search tree.

     Similar to list_destruct, this function is responsible for deallocating
     all memory that the BST allocated, but it is not responsible for
     deallocating any of the BST contents.
  */
  ~BST();

  /* Add an item to the BST. Return true if the item was added.
     Duplicate items should not be added.
  */
  bool insert(T item);

  /* Outputs the items of the BST using a breadth-first traversal. */
  void queue_output() const;


  /* Return the item contained in the BST if it equals (==) `item`.

     Throw 1, if the item is not found.
   */
  T contains(T item) const {
    BinaryNode<T> *tmp = root;
    while(tmp != NULL) {  // Traverse the tree down some path
      if (item == tmp->get_data())
        return tmp->get_data();
      else if (item < tmp->get_data())  // Go left
        tmp = tmp->get_lhs();
      else  // Go right
        tmp = tmp->get_rhs();
    }
    throw 1;  // `item` not found
  }

  /* Outputs the items of the BST using a depth-first traversal. */
  void stack_output() const {
    Stack<BinaryNode<T>*> s;  // Save pointers to the nodes
    if (root != NULL)
      s.push(root);  // Seed the traversal with the root of the tree
    while(!s.is_empty()) {  // while there are Nodes left to process
      BinaryNode<T> *tmp = s.pop();
      std::cout << tmp->get_data() << " ";  // output the node
      if (tmp->get_rhs())
        s.push(tmp->get_rhs());  // Add its rhs to be processed
      if (tmp->get_lhs())
        s.push(tmp->get_lhs());  // Add its lhs to be processed
    }
    std::cout << std::endl;
  }
};

/* Templated code that is intended to be used as a general purpose library (can
   take any type) often contains the entire implementation in the header
   file. The reason is the compiler needs to know what types to compile the
   templated code for thus the implementation is always required or all the
   available types must be specified up-front which is not possible in the
   general case.

   While I could have had you add your implementation to this file, I do not
   want you to be able to change the BST interface, hence why this file is
   importing from bst.cpp.
*/
#include "bst.cpp"

#endif /* BST_H */
