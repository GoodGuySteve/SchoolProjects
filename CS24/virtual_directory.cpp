#include <iostream>
#include <string>
#include "bst.h"
#include "directory.h"
#define _CRTDBG_MAP_ALLOC
#include <stdlib.h>
#include <crtdbg.h>

using namespace std;


/* Change `cwd` to point to either its parent (..) or one of its
   subdirectories.

   If `name` is `..` change into the parent directory. Attempting to change to
   the parent directory of the root should produce the "Invalid directory:
   <NAME>" error message.

   Otherwise `cwd` should only be updated if `cwd` has a subdirectory with the
   name `name`. Otherwise the message "Invalid directory: <NAME>" sh`ould be
   output where `<NAME>` is replaced by the `name` variable.

   Note: `cwd` is a reference to a pointer so that when you reassign `cwd` that
   change occurs for the cwd of the caller (main in this case).
*/
void change_directory(Directory *&cwd, string name) {
	using namespace std;
	
	if (name == ".."){
		if (cwd->get_parent()== NULL){
			cout << "Invalid directory: " << name << '\n';
			return;
		}
			
		cwd = cwd->get_parent();
		return;
	}
	
	Directory* foundit;
	
	if ((foundit = cwd->get_subdirectory(name)) == NULL){
		cout << "Invalid directory: " << name << endl;
		return;
	}
	cwd = foundit;
	
	
// DOESN'T WORK BECAUSE SUBDIRS IS PRIVATE	
// 	Directory* dir = new Directory(name);
// 
// 	PtrWrapper<Directory*> foundit;
// 	PtrWrapper<Directory*> *pointy = new PtrWrapper<Directory*>(dir);
// 	try{
// 		foundit = cwd->subdirs.contains(*pointy);
// 	}
// 	catch(int e){
// 		if (e == 1){
// 			cout << "Invalid directory: " << name;
// 			delete pointy;
// 			delete dir;
// 			return;
// 		}
// 	}
// 	delete pointy;
// 	delete dir;
// 	
// 	cwd = foundit.ptr;
// 	
	
	
	
	
	return;
			
	
	
	
  // TODO: Implement this function
}


/* Attempt to create a directory with name `name` under the cwd.

   The directory name `..` is reserved to mean the parent directory. If `name`
   matches `..` output: "../ is a reserved directory name\n"

   On failure (the directory already exists) output: "Directory already exists:
   <NAME>" where `<NAME>` is replaced by the `name` variable.
*/
void make_directory(Directory *cwd, string name) {
	if (name == ".."){
		std::cout << ".. is a reserved directory name\n";
		return;
	}
	
	if (!cwd->add_subdirectory(name)){
		std::cout << "Directory already exists: "<< name << std::endl;
		return;
	}
	
	return;
		
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
	using namespace std;
	Directory * dir = cwd;
	Stack<Directory *> silver;
	
	silver.push(cwd);
	while ((dir->get_parent()) != NULL){
		silver.push(dir->get_parent());
		dir = dir->get_parent();
	}
	
	while (!(silver.is_empty())){
		dir = silver.pop();
		cout << '/' << dir->get_name();
	}
	cout << endl;
		
	
	
  // TODO: Implement this function
}


/* Attempt to remove a subdirectory of `cwd` matching `name`.

   If the directory does not exist output: "Invalid directory: <NAME>\n"
*/
void remove_directory(Directory *cwd, string name) {
	using namespace std;
	
//	PtrWrapper<Directory *> to_delete; remove subdir returns bool. don't need this atm
	
	
	
// 	try {
// 		to_delete = cwd->get_subdirectory(name);
// 	}
// 	catch(int e){
// 		cout << "Invalid directory: " << name << endl;
// 		return;
// 	}
// 		

	
	
// 	make sure to replace get_subdirectory
	if(!cwd->remove_subdirectory(name)){
		cout << "Invalid directory: " << name << endl;
		return;
	}
	
	
	
	
	//delete to_delete.ptr;
		
		
	
	
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
