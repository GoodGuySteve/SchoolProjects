//Author 1: Steven Cabral
//Author 2: Bronwyn Perry-Huston

#pragma once

#define _CILK

#ifdef _CILK

#include <iostream>
#include <cilk/cilk.h>
#include <cilk/reducer.h>
#include "Bag.h"

//NOTE: to use this class, static member "scale" must be initialized before construction

class bag_reducer {
	struct Monoid : cilk::monoid_base < Bag > {
		void reduce(Bag* left, Bag* right) const {
			cout << "Performing reduction operation..." << endl;	
			left->bag_union(right);
		}
		void identity(Bag* empty) const {
			cout << "Instantiating an identitiy object..." << endl;
			empty = new Bag(); //union with null does nothing
			cout << "Identity object created." << endl;
		}
	};
	cilk::reducer<Monoid> reducerImp;

public:
	int scale; //scale is the log of the largest size the container can hold
	ULLI size();
	void init(int scale);
	bag_reducer(int scale);
	explicit bag_reducer(const Bag &init);
	~bag_reducer();
	void insert(ULLI data);
	Bag* split();
	ULLI* toArray();
	void print();
};

#endif
