//Author 1: Steven Cabral
//Author 2: Bronwyn Perry-Huston

#include "bag_reducer.h"

#ifdef _CILK

bag_reducer::bag_reducer(int scale) : reducerImp(), scale(scale) {
	if (scale <= 0) {
		cout << "Error - reducer scale not initialized before call to constructor" << endl;
		throw - 1;
	}
	reducerImp.view().init(scale);
}

bag_reducer::bag_reducer(const Bag &init) : reducerImp(init) {

}

ULLI bag_reducer::size() {
	return reducerImp.view().size;
}

void bag_reducer::init(int scale) {
	this->scale = scale;
	reducerImp.view().init(scale);
}

bag_reducer::~bag_reducer() {

}

void bag_reducer::insert(ULLI data) {
	reducerImp.view().bag_insert(data);
}

Bag* bag_reducer::split() {
	return reducerImp.view().bag_split();
}

ULLI* bag_reducer::toArray() {
	return reducerImp.view().toArray();
}

void bag_reducer::print() {
	reducerImp.view().print();
}

#endif
