//Author 1: Steven Cabral
//Author 2: Bronwyn Perry-Huston


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

int Pennant::getSize() {
	int size = 1;
	if (left != nullptr) size += left->getSize();
	if (right != nullptr) size += right->getSize();
	return size;
}

void Pennant::toArray(ULLI* destArray, int& currIndex) {
	if (left != nullptr) {
		left->toArray(destArray, currIndex);
	}
	destArray[currIndex++] = data;
	if (right != nullptr) {
		right->toArray(destArray, currIndex);
	}
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

Bag::Bag() : scale(4), size(0) {
	penArray = new Pennant*[scale + 1];
	for (int i = 0; i < scale + 1; ++i) {
		penArray[i] = nullptr;
	}
}

void Bag::init(int scale) {
	this->scale = scale;
	this->size = 0;
	penArray = new Pennant*[scale + 1];
	for (int i = 0; i < scale + 1; ++i) {
		penArray[i] = nullptr;
	}
}

Bag::Bag(int scale) : scale(scale), size(0) {
	penArray = new Pennant*[scale + 1];
	for (int i = 0; i < scale + 1; ++i) {
		penArray[i] = nullptr;
	}
}

void Bag::doubleSize() {
	
	scale *= 2;
	Pennant** temp = new Pennant*[scale];
	for (int i = 0; i < scale / 2; ++i) {
		temp[i] = penArray[i];
	}
	for (int i = scale / 2; i < scale; ++i) {
		temp[i] = nullptr;
	}
	delete[] penArray;
	penArray = temp;
}

//NOTE: copy constructor copies the pennant pointers, not the pennants
Bag::Bag(const Bag& other) : scale(other.scale), size(other.size) {
	penArray = new Pennant*[scale + 1];
	for (int i = 0; i < scale + 1; ++i) {
		penArray[i] = other.penArray[i];
	}
}

Bag& Bag::operator=(const Bag& other) {
	for (int i = 0; i < scale + 1; ++i) {
		penArray[i] = other.penArray[i];
	}
	return *this;
}


Bag::~Bag(){
	delete[] penArray;
}

//Inserts a data element into a bag
void Bag::bag_insert(ULLI data) {

	//Confirm bag has enough space to insert
	while (size >= exp2(scale)) {
		doubleSize();
	}

	//Convert data to Pennant
	Pennant* toInsert = new Pennant(data);
	int k = 0;
	//Adding one pennant is analogous to adding by 1 on structure
	while (penArray[k] != nullptr) {
		toInsert->pen_union(penArray[k]);
		penArray[k++] = nullptr;
	}
	penArray[k] = toInsert;
	size += 1;
}

//Unions two bag structures (NOTE: bags have to have same scale)
Bag* Bag::bag_union(Bag* other) {
	Pennant* carry = nullptr;
	int bitcode;

	//unioning bags is analogous to adding the pennants

	if (other == nullptr) return this; //handle identity case

	//allows new object in the case the union is too large
	while (size + other->size >= exp(scale)) {
		doubleSize();	
	}

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
			//penArray[k] doesn't change
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
			penArray[k] = carry;
			carry = nullptr;
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
	}
	//DEBUG
	if (carry != nullptr) {
		cout << "Error: overflow in Bag::bag_union()" << endl;
		throw 1;
	}
	size += other->size;
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
	other->size = size / 2;
	size = size / 2;
	//add back in the remainder
	if (leftover != nullptr) {
		bag_insert(leftover->data);
	}

	return other;
}

//return an array of the bag's contents
ULLI* Bag::toArray() {
	ULLI* retVal = new ULLI[size]; //TODO this gives warning - new can't take an unsigned long long?
	int curIndex = 0;
	for (int i = 0; i < scale + 1; ++i) {
		if (penArray[i] != nullptr) {
			penArray[i]->toArray(retVal, curIndex);
		}
	}
	return retVal;
}

//printing function for debugging
void Bag::print() {
	cout << "This bag has size: " << size << endl;
	ULLI* printArray = toArray();
	for (int i = 0; i < size; ++i) {
		cout << printArray[i] << " ";
	}

	cout << endl;
}
