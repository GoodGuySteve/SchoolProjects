#ifndef BST_H
#define BST_H
#include "data_structures.h"

template <class T>
class BST {
 private:
  BinaryNode<T> *root;
 public:

  /* Return a pointer to the BinaryNode containing the largest value.

     The `static` prefix to the function indicates that it is a `class method`
     rather than an `instance method`. This means the function can be called
     independent of an instance of the class. For example:

       tmp = BST::find_max(some_binary_node);
   */
  static BinaryNode<T> *find_max(BinaryNode<T> *node);

  /* Remove and return the item from the BST equal to `item`.

     Throw 1, if the item is not found.
   */
  T remove(T item);

  /* Remove the root node from the BST and return its data, if one
     exists. Raise an exception otherwise.

     Throw 1, if there is no root.

     This method is necessary in order to remove all Nodes from the BST.

     Hint: you can implement this pretty easily by utilziig `BST::remove`. This
     will allow you to avoid duplicating your work.
   */
  T remove_root();

  /* Output the list in sorted order.

     This function will need to call a recursive helper function you write in
     bst.cpp.
   */
  void sorted_output() const;

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
