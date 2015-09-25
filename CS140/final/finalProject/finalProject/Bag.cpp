//Author 1: Steven Cabral


//Bag and Pennant classes based on the classes found in the paper by Leiserson
//and Schardl linked to on the BFS project page

#include "Bag.h"


Pennant::Pennant() : left(nullptr), right(nullptr) {
}

Pennant::Pennant(ULLI d) : data(d), left(nullptr), right(nullptr) {
}

Pennant::~Pennant() {
}

Pennant* Pennant::pen_union(Pennant* other) {
	other->right = this->left;
	this->left = other;
	return this;
}

Pennant* Pennant::pen_split() {
	Pennant* other = this->left;
	this->left = other->right;
	other->right = nullptr;
	return other;
}

//prints the pennant in a breadth-first manner
//useful function for debugging
void Pennant::print() {
	if (left != nullptr) {
		left->print();
	}
	cout << data << " ";
	if (right != nullptr) {
		right->print();
	}
}


Bag::Bag(int scale) : scale(scale) {
	penArray = new Pennant*[scale + 1];
	for (int i = 0; i < scale + 1; ++i) {
		penArray[i] = nullptr;
	}
}

//NOTE: copy constructor copies the pennant pointers
Bag::Bag(const Bag& other) {
	penArray = new Pennant*[scale + 1];
	for (int i = 0; i < scale + 1; ++i) {
		penArray[i] = other.penArray[i];
	}
}

Bag::~Bag(){
	delete[] penArray;
}

//Inserts a data element into a bag
void Bag::bag_insert(ULLI data) {
	//Convert data to Pennant
	Pennant* toInsert = new Pennant(data);
	int k = 0;
	//Adding one pennant is analogous to adding by 1 on structure
	while (penArray[k] != nullptr) {
		toInsert->pen_union(penArray[k]);
		penArray[k++] = nullptr;
	}
	penArray[k] = toInsert;
}

//Unions two bag structures
Bag* Bag::bag_union(Bag* other) {
	Pennant* carry = nullptr;
	int bitcode;
	//unioning bags is analogous to adding the pennants
	for (int k = 0; k < scale + 1; ++k) {
		bitcode = 0;
		bitcode += (carry != nullptr) * 4;
		bitcode += (other->penArray[k] != nullptr) * 2;
		bitcode += (penArray[k] != nullptr);

		switch(bitcode) {
		case 0: //both pennants null, no carry
			carry = nullptr;
			penArray[k] = nullptr;
			break;
		case 1: //other pennant null, no carry
			carry = nullptr;
			break;
		case 2: //local pennant null, no carry
			carry = nullptr;
			penArray[k] = other->penArray[k];
			break;
		case 3: //both pennant non-null, no carry
			carry = penArray[k]->pen_union(other->penArray[k]);
			penArray[k] = nullptr;
			break;
		case 4: //both pennants null with carry
			carry = nullptr;
			//penArray[k] doesn't change
			break;
		case 5: //other pennant null with carry
			carry = penArray[k]->pen_union(carry);
			penArray[k] = nullptr;
			break;
		case 6: //local pennant null with carry
			carry = other->penArray[k]->pen_union(carry);
			penArray[k] = nullptr;
			break;
		case 7: //no pennant null
			carry = other->penArray[k]->pen_union(carry);
			//penArray[k] doesn't change
			break;
		default:
			cout << "Error in case statement in Bag::bag_union()" << endl;
			throw 1; //should not be reached
		}
		//DEBUG
		if (carry != nullptr) {
			cout << "Error: overflow in Bag::bag_union()" << endl;
			throw 1;
		}
	}
	return this;
}

//Splits a bag into two bags of about half the size
//This bag call will contain the remainder of the split
Bag* Bag::bag_split() {
	Bag* other = new Bag(scale);
	Pennant* leftover = penArray[0];
	penArray[0] = nullptr;
	//splitting the bag is analogous to shifting by 1
	for (int k = 1; k < scale + 1; ++k) {
		if (penArray[k] != nullptr) {
			other->penArray[k - 1] = penArray[k]->pen_split();
			penArray[k - 1] = penArray[k];
			penArray[k] = nullptr;
		}
	}
	//add back in the remainder
	if (leftover != nullptr) {
		bag_insert(leftover->data);
	}

	return other;
}

//printing function for debugging
void Bag::print() {
	for (int i = 0; i < scale + 1; ++i) {
		if (penArray[i] != nullptr)
			penArray[i]->print();
	}
	cout << endl;
}