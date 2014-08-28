#ifndef DATA_STRUCTURES_H
#define DATA_STRUCTURES_H
#include <iostream>


template <class T>
class Node {
 private:
  T data;
  Node<T> *next;
 public:
  Node(T data, Node<T> *next): data(data), next(next) {}

  T get_data() const {
    return data;
  }

  Node<T> *get_next() const {
    return next;
  }

  Node<T> *set_next(Node<T> *node) {
    this->next = node;
    return node;
  }
};


template <class T>
class BinaryNode {
 private:
  T data;
  BinaryNode<T> *lhs;
  BinaryNode<T> *rhs;
 public:
 BinaryNode(T data, BinaryNode<T> *lhs, BinaryNode<T> *rhs):
  data(data), lhs(lhs), rhs(rhs) {}

  T get_data() const {
    return data;
  }

  BinaryNode<T> *get_lhs() const {
    return lhs;
  }

  BinaryNode<T> *get_rhs() const {
    return rhs;
  }

  void set_data(T data) {
    this->data = data;
  }

  BinaryNode<T> *set_lhs(BinaryNode<T> *node) {
    this->lhs = node;
    return node;
  }

  BinaryNode<T> *set_rhs(BinaryNode<T> *node) {
    this->rhs = node;
    return node;

  }
};


template <class T>
class List {
 private:
  Node<T> *head;
  int size;
  Node<T> *tail;
  int normalized_position(int position, bool for_insert=false) const {
    int size = this->size;
    if (for_insert)
      ++size;
    if (position < 0)
      position += size;
    if (position < 0 || position >= size)
      throw 1;
    return position;
  }

 public:
  List(): head(NULL), size(0), tail(NULL) {}

  ~List() {
    Node<T> *next = head;
    while (size--) {
      next = head->get_next();
      delete head;
      head = next;
    }
  }

  T get_at(int position) const {
    position = normalized_position(position);
    if (position == size - 1)
      return tail->get_data();
    Node<T> *tmp = head;
    while (position-- > 0)
      tmp = tmp->get_next();
    return tmp->get_data();
  }

  int get_size() const {
    return size;
  }

  bool is_empty() const {
    return size == 0;
  }

  /* The new keyword throws exceptions when it fails. This means
     this function will also throw an exception when insert_at fails */
  void insert_at(int position, T data) {
    position = normalized_position(position, true);
    if (position == 0) {
      head = new Node<T>(data, head);
      if (size == 0)
        tail = head;
    }
    else if (position == size)
      tail = tail->set_next(new Node<T>(data, NULL));
    else {
      Node<T> *tmp = head;
      while (position-- > 1)
        tmp = tmp->get_next();
      tmp->set_next(new Node<T>(data, tmp->get_next()));
    }
    ++size;
  }

  void output() const {
    Node<T> *tmp = head;
    for (int i = 0; i < size; ++i) {
      std::cout << i << ": " << tmp->get_data() << std::endl;
      tmp = tmp->get_next();
    }
  }

  T remove_at(int position) {
    position = normalized_position(position);
    T retval;
    Node<T> *to_delete;
    if (position == 0) {
      to_delete = head;
      head = to_delete->get_next();
      if (!head)
        tail = NULL;
    }
    else {
      Node<T> *tmp = head;
      while (position-- > 1)
        tmp = tmp->get_next();
      to_delete = tmp->get_next();
      if (!tmp->set_next(to_delete->get_next()))
        tail = tmp;
    }
    --size;
    retval = to_delete->get_data();
    delete to_delete;
    return retval;
  }
};


template <class T>
class Stack {
 private:
  List<T> list;
 public:
  Stack(): list() {}

  bool is_empty() const {
    return list.is_empty();
  }

  T pop() {
    return list.remove_at(0);
  }

  void push(T item) {
    list.insert_at(0, item);
  }
};


template <class T>
class Queue {
 private:
  List<T> list;
 public:
 Queue(): list() {}

  bool is_empty() const {
    return list.is_empty();
  }

  void enqueue(T item) {
    list.insert_at(-1, item);
  }

  T dequeue() {
    return list.remove_at(0);
  }
};


template <class T>
class PtrWrapper {
};

template <class T>
class PtrWrapper<T *> {
 public:
  T *ptr;
  PtrWrapper() {}
  PtrWrapper(T *ptr): ptr(ptr) {}
  bool operator==(const PtrWrapper<T*> &other) const {
    return *ptr == *other.ptr;
  }
  bool operator!=(const PtrWrapper<T*> &other) const {
    return *ptr != *other.ptr;
  }
  bool operator<(const PtrWrapper<T*> &other) const {
    return *ptr < *other.ptr;
  }
  bool operator>(const PtrWrapper<T*> &other) const {
    return *ptr > *other.ptr;
  }
};


template <class T>
std::ostream& operator<<(std::ostream& os, const PtrWrapper<T*> &obj) {
  return os << *obj.ptr;
}


#endif /* DATA_STRUCTURES_H */
