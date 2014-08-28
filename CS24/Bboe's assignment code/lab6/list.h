#ifndef LIST_H
#define LIST_H

#include <string>

class Node {
 private:
  std::string data;
  Node *next;
 public:
 Node(std::string data, Node *next): data(data), next(next) {}

  std::string get_data() const {
    return data;
  }

  Node *get_next() const {
    return next;
  }

  void set_data(std::string data) {
    this->data = data;
  }

  Node *set_next(Node *node) {
    this->next = node;
    return node;
  }
};


class List {
 private:
  Node *head;
  int size;
  Node *tail;
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
    Node *next = head;
    while (size--) {
      next = head->get_next();
      delete head;
      head = next;
    }
  }

  void sort();  // TODO: Write this function in sort.cpp

  std::string get_at(int position) const {
    position = normalized_position(position);
    if (position == size - 1)
      return tail->get_data();
    Node *tmp = head;
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
  void insert_at(int position, std::string data) {
    position = normalized_position(position, true);
    if (position == 0) {
      head = new Node(data, head);
      if (size == 0)
        tail = head;
    }
    else if (position == size)
      tail = tail->set_next(new Node(data, NULL));
    else {
      Node *tmp = head;
      while (position-- > 1)
        tmp = tmp->get_next();
      tmp->set_next(new Node(data, tmp->get_next()));
    }
    ++size;
  }

  void output() const {
    Node *tmp = head;
    for (int i = 0; i < size; ++i) {
      std::cout << i << ": " << tmp->get_data() << std::endl;
      tmp = tmp->get_next();
    }
  }

  std::string remove_at(int position) {
    position = normalized_position(position);
    std::string retval;
    Node *to_delete;
    if (position == 0) {
      to_delete = head;
      head = to_delete->get_next();
      if (head == NULL)
        tail = NULL;
    }
    else {
      Node *tmp = head;
      while (position-- > 1)
        tmp = tmp->get_next();
      to_delete = tmp->get_next();
      if (tmp->set_next(to_delete->get_next()) == NULL)
        tail = tmp;
    }
    --size;
    retval = to_delete->get_data();
    delete to_delete;
    return retval;
  }
};

#endif /* LIST_H */
