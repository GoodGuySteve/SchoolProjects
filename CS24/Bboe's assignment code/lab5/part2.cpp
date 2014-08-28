#include <iostream>
#include "myclass.h"

void single() {
  std::cout << "-> single\n";
  MyClass m;
  std::cout << "<- single\n";
}

void two() {
  std::cout << "-> two\n";
  MyClass m1("first");
  MyClass m2("second");
  std::cout << "<- two\n";
}

void constant_array() {
  std::cout << "-> constant_array\n";
  MyClass m[3];
  m[0].set_message("1");
  m[1].set_message("2");
  m[2].set_message("3");
  std::cout << "<- constant_array\n";
}

void dynamic_single() {
  std::cout << "-> dynamic_single\n";
  MyClass *m = new MyClass("dynamic");
  delete m;
  std::cout << "<- dynamic_single\n";
}

void dynamic_array(int n) {
  // Create an array on the heap of MyClass instances
  std::cout << "-> dynamic_array\n";
  MyClass *m = new MyClass[n];
  m[0].set_message("first allocated");
  if (n > 1)
    m[n - 1].set_message("last allocated");
  delete [] m;
  std::cout << "<- dynamic_array\n";
}

void dynamic_pointer_array(int n, char **msgs) {
  // Create an array on the heap of pointers for MyClass objects
  std::cout << "-> dynamic_pointer_array\n";
  MyClass **m = new MyClass*[n];
  for (int i = 0; i < n; ++i)
    m[i] = new MyClass(msgs[i]);  // Actually point to a MyClass instance
  for (int i = 0; i < n; ++i)
    delete m[i];  // Must manually delete each
  delete [] m;  // Delete the pointers (requires the '[]' part)
  std::cout << "<- dynamic_array\n";
}



int main(int argc, char *argv[]) {
  single();
  std::cout << std::endl;

  two();
  std::cout << std::endl;

  constant_array();
  std::cout << std::endl;

  dynamic_single();
  std::cout << std::endl;

  dynamic_array(argc);
  std::cout << std::endl;

  dynamic_pointer_array(argc, argv);
  return 0;
}
