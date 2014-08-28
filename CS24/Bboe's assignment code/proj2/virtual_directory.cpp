#include <iostream>
#include "bst.h"
#include "directory.h"

using namespace std;


/* Change `cwd` to point to either its parent (..) or one of its
   subdirectories.

   If `name` is `..` change into the parent directory. Attempting to change to
   the parent directory of the root should produce the "Invalid directory:
   <NAME>" error message.

   Otherwise `cwd` should only be updated if `cwd` has a subdirectory with the
   name `name`. Otherwise the message "Invalid directory: <NAME>" should be
   output where `<NAME>` is replaced by the `name` variable.

   Note: `cwd` is a reference to a pointer so that when you reassign `cwd` that
   change occurs for the cwd of the caller (main in this case).
*/
void change_directory(Directory *&cwd, string name) {
  // TODO: Implement this function
}


/* Attempt to create a directory with name `name` under the cwd.

   The directory name `..` is reserved to mean the parent directory. If `name`
   matches `..` output: ".. is a reserved directory name\n"

   On failure (the directory already exists) output: "Directory already exists:
   <NAME>" where `<NAME>` is replaced by the `name` variable.
*/
void make_directory(Directory *cwd, string name) {
  // TODO: Implement this function
}


/* Output the complete path to the present working directory.

   From the root the output should only be "/\n".

   From a directory foo with parent bar whose parent is the root the output
   should be "/bar/foo\n".

   Hint: You might want to use one of the data structures to help produce the
   output in the correct order.
*/
void output_pwd(Directory *cwd) {
  // TODO: Implement this function
}


/* Attempt to remove a subdirectory of `cwd` matching `name`.

   If the directory does not exist output: "Invalid directory: <NAME>\n"
*/
void remove_directory(Directory *cwd, string name) {
  // TODO: Implement this function
}


/* The main program simply prompts the user for commands until the input stream
   is closed (ctrl+d or EOF).

   You need not change anything in main. However, you are more than welcome to
   add additional commands for fun and possibly extra credit.
*/
int main() {
  Directory root("");
  Directory *cwd = &root;
  string command;

  while (cin) {
    cout << cwd->get_name() << "> ";
    getline(cin, command);
    if (command == "help") {
      cout << "Commands: cd NAME, mkdir NAME, ls, pwd, rmdir NAME\n";
    }
    else if (command.substr(0, 3) == "cd ") {
      change_directory(cwd, command.substr(3, command.size()));
    }
    else if (command == "ls") {
      cwd->list_directory();
    }
    else if (command.substr(0, 6) == "mkdir ") {
      make_directory(cwd, command.substr(6, command.size()));
    }
    else if (command == "pwd") {
      output_pwd(cwd);
    }
    else if (command.substr(0, 6) == "rmdir ") {
      remove_directory(cwd, command.substr(6, command.size()));
    }
    else if (command == "");
    else {
      cout << "Invalid command. Type help for the list of commands.\n";
    }
  }
  return 0;
}
