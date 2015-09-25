//Author 1: Steven Cabral
//Author 2: Bronwyn Perry-Huston

#include <iostream>
#include "Bag.h"

//This line can be commented out to allow testing on a non-parallel machine without cilk
#define _CILK

#ifdef _CILK
	#include <cilk/cilk.h>
	#include "bag_reducer.h"
#else
	#define cilk_for for
#endif

using namespace std;

int main(int argc, char* argv[]) {

	bag_reducer bag(15);

	bag.insert(0);
	bag.insert(1);
	bag.insert(2);
	bag.insert(2);
	bag.insert(4);
	bag.insert(5);
	bag.insert(6);
	bag.insert(7);

	bag.print();

	Bag* otherBag = bag.split();
	cout << "after split:" << endl;
	bag.print();
	otherBag->print();

	Bag* thirdBag = otherBag->bag_split();

	cout << "after split:" << endl;
	bag.print();
	otherBag->print();
	thirdBag->print();

	//bag.view()->bag_union(thirdBag);

	//cout << "after union:" << endl;
	//bag.print();
	//otherBag->print();

	delete thirdBag;

	cilk_for(int i = 9; i < 5000; i += 3) {
		bag.insert(i);
	}

	cout << "reducer after parallel inserts:" << endl;
	bag.print();
	cout << endl;

	ULLI size = 10;
	Bag** bagArray = new Bag*[size / 4];
	
	cilk_for(int i = 0; i < size / 4; ++i) {
		bagArray[i] = bag.split();
	}
	
	cout << "All bags after splitting:" << endl;
	for (int i = 0; i < size / 4; ++i) {
		cout << "Bag " << i << ":" << endl;
		bagArray[i]->print();
	}
	cout << "remaining reducer: " << endl;
	bag.print();

	cout << endl << "splitting reducer once more: "<< endl;
	cout << "bag contents: " << endl;
	bag.split()->print();
	
	cout << "reducer contents: " << endl;
	bag.print();
	return 0;
}
