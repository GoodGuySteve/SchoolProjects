#include <iostream>

template <class T>
BST<T>::~BST() {
  // TODO: Write this function
}

template <class T>
bool BST<T>::insert(T item) {
  BinaryNode<T> *to_insert = new BinaryNode<T>(item, NULL, NULL);
  if (root == NULL) {
    root = to_insert;
    return true;
  }
  // TODO: Complete this function
  return false;
}

template <class T>
void BST<T>::queue_output() const {
  // TODO: Write this function
}
