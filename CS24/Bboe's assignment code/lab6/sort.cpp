#include <iostream>
#include "list.h"

/* The List class is very similar to the list we wrote before in C, but
   converted to C++. Other changes include the more general `insert_at`
   function rather than `push_back`.

   Rather than storing pointers, our list now stores std::string types. To
   indicate errors, exceptions are raised, but you need not concern yourself
   with that now.

   Because the Node class's attributes are private, you cannot directly access
   `next` and `data` (even within the List class's functions), thus you must
   use the `get_next` and `get_data` functions on an instance of the node.

*/

void List::sort() {
  /*
    TODO: Write the code to in-place sort the items (strings) of the List.

    Your sort must run in O(n*n) time or better and must be O(1) extra space.
    Hint: Using `get_at` will not suffice as it is O(n).

    Strings should be compared by using the less-than ('<') operator such that
    if string a < string b, then string a is before string b in the list.

    You should write one of bubble, insertion, or selection sort.

    You can either swap the nodes within their position in the list, or swap
    the data contained within the nodes when you swap.
  */
}


/* Sort the command line arguments using our List class.

   WARNING: If you change this code you will likely not pass the tests.
 */
int main(int argc, char *argv[]) {
  List list;
  for (int i = 1; i < argc; ++i) {
    list.insert_at(-1, argv[i]);
  }
  list.sort();
  list.output();
}
