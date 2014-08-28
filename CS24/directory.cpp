#include "directory.h"


Directory::~Directory() {
	
	/*BinaryNode<PtrWrapper<Directory *> >* tree;
	
// 	tree = subdirs.find_max(NULL);	COMMENTED OUT TEMPORARILY BECAUSE WE CANT GET FIND MAX TO WORK
	
	Queue<BinaryNode<PtrWrapper<Directory *> >* > jerry;
	if (tree == NULL)
		get_the_fuck_outta_here;
	jerry.enqueue(tree);
	while(!jerry.is_empty()){
		BinaryNode<PtrWrapper<Directory *> >* tmp = jerry.dequeue();
        
		if (tmp->get_lhs() != NULL)
			jerry.enqueue(tmp->get_lhs());
		if(tmp->get_rhs() != NULL)
			jerry.enqueue(tmp->get_rhs());
		
// 		delete tmp->ptr;    COMMENTED OUT TEMPORARILY BECAUSE NO MEMEBER PTR
		
		delete tmp;
	}
// 	delete name;
// 	delete subdirs;
	

//TODO: Delete all the dir pointers created in add_subdirectory

  // TODO: Implement this destructor
  */
	try{
		while (true){
			PtrWrapper<Directory *> toDelete;
			toDelete = subdirs.remove_root();
			delete toDelete.ptr;
		}
	}
	catch (int e){
		if (e == 3) return; //when no root left
		else{
			std::cout << "Unhandled exception in BST::~BST" << std::endl;
			return;
		}
	}
}


bool Directory::add_subdirectory(std::string name) {
	using namespace std;
	Directory* dir = new Directory(name,this);

	
	PtrWrapper<Directory*> *pointy = new PtrWrapper<Directory*>(dir);
	
// 	"YO DAWG, WE'RE FUCKING BOB THE BUILDER UP IN HERE CONSTRUCTING SHIT LEFT AND RIGHT YO " << endl;
		
	bool lmao = (subdirs.insert(*pointy));
	delete pointy;
	
	return lmao;

  // TODO: Implement this function
  return false;
}


Directory *Directory::get_subdirectory(std::string name) const {
	
	Directory* dir = new Directory(name);
	PtrWrapper<Directory*> *pointy = new PtrWrapper<Directory*>(dir);
	PtrWrapper<Directory*> foundit;
	
	try{
		foundit = subdirs.contains(*pointy);
	}
	catch(int e){
		if (e == 1){
			
			delete pointy;
			delete dir;
			return NULL;
		}
	}
	delete pointy;
	delete dir;
	
	return foundit.ptr;
	
  // TODO: Implement this function
  return NULL;
}


bool Directory::remove_subdirectory(std::string name) {
  // TODO: Implement this function
	PtrWrapper<Directory *> our_data;
	Directory tmpDir(name);
	PtrWrapper<Directory *> tmpPtrWrapper(&tmpDir);

	try{
		our_data = subdirs.remove(tmpPtrWrapper);
	}
	catch (int e){
		if(e) return false;
	}
	if (our_data == tmpPtrWrapper) {
		delete our_data.ptr;
		return true;

	}
	else throw 2; //removed data is not the right data
  return false;
}
