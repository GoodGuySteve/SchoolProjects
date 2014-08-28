#ifndef DIRECTORY_H
#define DIRECTORY_H
#include "data_structures.h"
#include "bst.h"

/* This class represents a directory.

   A directory contains the name of the directory such as "home" or "bboe".

   A directory also has a pointer to its parent directory. The root-directory's
   parent pointer will be NULL.

   The subdirs private attribute is a BST containing wrapped pointers to
   subdirectories of the directory. The pointers must be wrapped so that they
   can be compared properly, otherwise the standard operators would compare
   their addresses.
*/
class Directory {
 private:
  std::string name;
  Directory *parent;
  BST<PtrWrapper<Directory*> > subdirs;
 public:
  /* Construct a directory instance. `name` refers to the name of the directory
     and `parent`, when provided, sets the pointer to the parent of the
     directory.
  */
  Directory(std::string name, Directory *parent=NULL):
    name(name), parent(parent), subdirs() {}

  /* Deallocate the directory.

     This destructor needs to free all memory allocated by this directory.
  */
  ~Directory();

  /* Attempt to create a new directory under the current directory.

     Return whether or not the addition was successful.
   */
  bool add_subdirectory(std::string name);

  /* Return the name of the directory. */
  std::string get_name() const { return name; }

  /* Return a pointer to the parent directory. */
  Directory *get_parent() const { return parent; }

  /* Return a pointer to the subdirectory matching `name`.
     Return NULL if no such subdirectory exists.
  */
  Directory *get_subdirectory(std::string name) const;

  /* Output the directories in sorted order. */
  void list_directory() const { subdirs.sorted_output(); }

  /* Attempt to remove a subdirectory matching `name`.
     Return whether or not a directory was removed.
  */
  bool remove_subdirectory(std::string name);
};


/* NOTE: You need not understand any of the following code in this file. */

/* The following functions perform operator overloading that support
   comparision between between Directory instances. They implement comparision
   such that only the name of the Directory is compared. This allows for the
   following:

   Directory d1("hello");
   Directory d2("world"):
   Directory d3("hello", &d2);

   d1 == d2  (false: hello != world)
   d1 != d2  (true: hello != world)
   d1 < d2   (true: hello < world)
   d1 == d3  (true: hello == world, even though d3's parent is different)

   Having these comparisons overloaded allows us to store Directory types in
   the BST and use the simple comparison tests that you have already
   written. Note that `<=` and `>=` are not overloaded thus you can not use
   those comparisons in your BST class.
*/
inline bool operator==(const Directory &lhs, const Directory &rhs) {
  return lhs.get_name() == rhs.get_name();
}
inline bool operator!=(const Directory &lhs, const Directory &rhs) {
  return lhs.get_name() != rhs.get_name();
}
inline bool operator<(const Directory &lhs, const Directory &rhs) {
  return lhs.get_name() < rhs.get_name();
}
inline bool operator>(const Directory &lhs, const Directory &rhs) {
  return lhs.get_name() > rhs.get_name();
}

/* The following overloads the << operator for ostream and directory objects.

   Doing this allows us to control what is output when we execute:

   Directory d("hello");
   cout << d << endl;
*/
inline std::ostream& operator<<(std::ostream& os, const Directory &obj) {
  return os << obj.get_name();
}

#endif /* DIRECTORY_H */
