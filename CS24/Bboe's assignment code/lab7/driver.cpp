#include <iostream>
#include <sstream>
#include "data_structures.h"
#include "bst.h"
using namespace std;


int main(int argc, char *argv[]) {
  if (argc < 2) {
    cout << "Usage: driver num [num...]\n";
    return 1;
  }

  // Add the numbers to the binary search tree
  BST<int> b;
  for (int i = 1; i < argc; ++i) {
    stringstream ss(argv[i]);
    int n;
    if (!(ss >> n && ss.eof())) {
      cout << "'" << argv[i] << "' is not a valid number. Goodbye!\n";
      return 1;
    }
    if (!b.insert(n)) {
      cout << "Failed to insert: " << n << " Not inserting any more.\n";
      break;
    }
  }

  b.stack_output();
  b.queue_output();

  for (int i = 0; i < 10; ++i) {
    cout << i << " in BST: ";
    try {
      b.contains(i);
      cout << "YES!\n";
    }
    catch (int e) {
      cout << "NO!\n";
    }
  }
  return 0;
}
