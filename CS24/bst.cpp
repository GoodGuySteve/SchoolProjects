
#include <iostream>
#define get_the_fuck_outta_here return




template<class T>
BinaryNode <T> *printNode(BinaryNode <T> *duck){
	if (duck == NULL)
		return NULL;
	printNode(duck->get_lhs());
	std::cout << duck->get_data() << ' ';
	printNode(duck->get_rhs());
	return duck;
}


template <class T>
BinaryNode<T> *BST<T>::find_max(BinaryNode<T> *node) {

	// 	Keep going down rhs until rhs == NULL. Then return the node
	BinaryNode<T> *tmp = node;

	while (tmp->get_rhs() != NULL){
		tmp = tmp->get_rhs();
	}

	return tmp;



	// 	THIS IS ALL COMMENTED OUT TEMPORARILY BECAUSE OF ISSUES WITH ACCESSING THE NODES IN TREE
	// 	if (root != NULL){
	// 		BinaryNode<T> * entire_fucking_tree;
	// 		entire_fucking_tree = root;
	// 		root = NULL;
	// 		return entire_fucking_tree;
	// 	}
	// 		

	// TODO: Implement this function
	throw 1;
	// TODO: Implement this function (when you have a use for it)
	return NULL;
}


template <class T>
T BST<T>::remove(T item) {
	
		// 	remember to take care of the root case

	BinaryNode<T> *our_parent = NULL;
	BinaryNode<T> *our_guy = root;
	BinaryNode<T> *tmp;
	T our_data;

	while (our_guy != NULL) {  // Traverse the tree down some path
		if (item == our_guy->get_data())
			break;
		else if (item < our_guy->get_data()){  // Go left
			our_parent = our_guy;
			our_guy = our_guy->get_lhs();
		}
		else{  // Go right
			our_parent = our_guy;
			our_guy = our_guy->get_rhs();
		}
	}

	if (our_guy == NULL){
		throw 1; // item not found
		return our_data; //VS
	}
	//now we know our item is inside our BST


	// NOW WE CAN GET TO OUR 3 CASES OF DELETING. WOOOO
	our_data = our_guy->get_data();

	if (our_guy == root){
		our_data = remove_root();
		return our_data;
	}

	if (our_guy->get_rhs() == NULL && our_guy->get_lhs() == NULL){ //no children lol
		if (our_parent->get_rhs() == our_guy){ //we want to NULL the rhs
			delete our_parent->get_rhs();
			our_parent->set_rhs(NULL);
			return our_data;
		}

		else{ //else, NULL lhs
			delete our_parent->get_lhs();
			our_parent->set_lhs(NULL);
			return our_data;
		}
	}

	else if (our_guy->get_rhs() == NULL && our_guy->get_lhs() != NULL){ // one child -> our lhs is a kid

		tmp = our_guy->get_lhs(); //saved the child in tmp
		
		if (our_parent->get_rhs() == our_guy){
			delete our_parent->get_rhs();
			our_parent->set_rhs(tmp);
			return our_data;

		}

		else{ //else, NULL lhs
			delete our_parent->get_lhs();
			our_parent->set_lhs(tmp);
			return our_data;
		}

	}

	else if (our_guy->get_lhs() == NULL && our_guy->get_rhs() != NULL){

		tmp = our_guy->get_rhs(); //saved the child in tmp 

		if (our_parent->get_rhs() == our_guy){
			delete our_parent->get_rhs();
			our_parent->set_rhs(tmp);
			return our_data;

		}

		else{ //else, NULL lhs
			delete our_parent->get_lhs();
			our_parent->set_lhs(tmp);
			return our_data;
		}


	}


	else{ //two child case
		// 	saving max data into an int or someshit, then removing max's shit, then overwriting the data

		BinaryNode<T> *max;
		max = find_max(our_guy->get_lhs());
		T max_data = max->get_data();

		remove(max->get_data());




		our_guy->set_data(max_data);


		return our_data;
	}






	// TODO: Implement this function
	throw 1;
}


template <class T>
T BST<T>::remove_root() {

	if (root == NULL) throw 3; //no root

	T retData = root->get_data();
	T maxData;
	BinaryNode<T>* tmpNode;

	if (root->get_lhs() == NULL){
		BinaryNode<T> *tmpRoot = root;
		root = root->get_rhs();
		delete tmpRoot;
		return retData;
	}

	tmpNode = find_max(root->get_lhs());
	maxData = tmpNode->get_data();
	if(remove(maxData) != maxData) throw 2; //removed the wrong data
	root->set_data(maxData);
	return retData;

	throw 1;
}


template <class T>
void BST<T>::sorted_output() const {
	printNode(root);
	std::cout << std::endl;


	// TODO: Implement this function
}


template <class T>
BST<T>::~BST() {
	Queue<BinaryNode<T>* > jerry;
	if (root == NULL)
		get_the_fuck_outta_here;
	jerry.enqueue(root);
	while (!jerry.is_empty()){
		BinaryNode<T>* tmp = jerry.dequeue();

		if (tmp->get_lhs() != NULL)
			jerry.enqueue(tmp->get_lhs());
		if (tmp->get_rhs() != NULL)
			jerry.enqueue(tmp->get_rhs());
		delete tmp;
	}




	// TODO: Write this function
	// TODO: Copy your lab7 implementation here
}

template <class T>
bool BST<T>::insert(T item) {

	BinaryNode<T> *to_insert = new BinaryNode<T>(item, NULL, NULL);
	if (root == NULL) {
		root = to_insert;
		return true;
	}
	T current_data;
	BinaryNode<T>* current_node;
	BinaryNode<T>* prev_node;

	current_node = root;
	prev_node = current_node;

	while (current_node != NULL){
		prev_node = current_node;
		current_data = current_node->get_data();
		if (item > current_data)
			current_node = current_node->get_rhs();
		else if (item < current_data)
			current_node = current_node->get_lhs();
		else
			return false;
	}

	if (item > current_data){
		prev_node->set_rhs(to_insert);
		return true;
	}
	else if (item < current_data){
		prev_node->set_lhs(to_insert);
		return true;
	}


	// TODO: Copy your lab7 implementation here
	return false;
}

