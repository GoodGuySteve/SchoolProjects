//Author 1: Steven Cabral
//Author 2: Bronwyn Perry-Huston

#include <iostream>
#include "Bag.h"

//This line can be commented out to allow testing on a non-parallel machine without cilk
//#define _CILK

#ifdef _CILK
	#include <cilk/cilk.h>
//	#include "BagReducer.h"
#else
	#define cilk_for for
#endif

using namespace std;

int main(int argc, char* argv[]) {

	Bag* bag = new Bag(8);
	bag->bag_insert(0);
	bag->bag_insert(1);
	bag->bag_insert(2);
	bag->bag_insert(4);
	bag->bag_insert(5);
	bag->bag_insert(6);
	bag->bag_insert(7);

	bag->print();

	for (int i = 8; i < 300; ++i) {
		bag->bag_insert(i);
	}

	Bag* otherBag = bag->bag_split();
	cout << "after split:" << endl;
	bag->print();
	otherBag->print();

	Bag* thirdBag = otherBag->bag_split();

	cout << "after split:" << endl;
	bag->print();
	otherBag->print();
	thirdBag->print();

	bag->bag_union(thirdBag);

	cout << "after union:" << endl;
	bag->print();
	otherBag->print();

	delete thirdBag;

	return 0;
}
