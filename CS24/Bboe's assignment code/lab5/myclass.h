#ifndef MYCLASS_H
#define MYCLASS_H

#include <string>


// This file contains the skeleton code for part 2 of lab 5. The comments below
// explain how to complete this part of the lab.
//

//
// Example usage of MyClass to demonstrate functionality:
// ------------------------------------------------------------------
// int main() {
//   MyClass m1;
//   MyClass m2("I am m2!);
//   MyClass m3;
//
//   m3.set_message("I am m3");
//   return 0;
// }
//
// Output:
// ------------------------------------------------------------------
// Called default constructor
// Called string constructor with: i am m2
// Called default constructor
// Destructing: i am m3
// Destructing: i am m2
// Destructing: DEFAULT MESSAGE

class MyClass {
 public:
  
  // Default constructor
  MyClass() {
    // TODO:
    // When the default constructor is called it should print "Called default
    // constructor" to standard output and set the `msg` member to "DEFAULT
    // MESSAGE"
  }
  
  // String constructor
  MyClass(std::string msg) {
    // TODO:
    // When the string constructor is called it should print "Called string
    // constructor with: " followed by the string passed to the constructor
    // and set the `msg` member to the string argument passed to the
    // constructor
  }
  
  // Default destructor
  ~MyClass() {
    // TODO:
    // When the destructor is called, it should print
    // "Destructing: " followed by the `msg` member
  }
  
  // Mutator method for `msg`
  void set_message(std::string msg) {
    // TODO:
    // The set_message method should update the value of the `msg`
  }
  
 private:
  std::string msg;
};

#endif // MYCLASS_H
