//Author 1: Steven Cabral


//Bag and Pennant classes based on the classes found in the paper by Leiserson
//and Schardl linked to on the BFS project page
//TODO proper citation?

#pragma once

#include <iostream>

#define ULLI unsigned long long int

using namespace std;

class Pennant {
public:
	ULLI data;
	Pennant* left;
	Pennant* right;
	Pennant();
	Pennant(ULLI);
	~Pennant();
	Pennant* pen_union(Pennant* other);
	Pennant* pen_split();
	void print();
};

class Bag {
public:
	int scale; //scale is the log of the max vertex size
	Pennant** penArray;
	Bag(int scale);
	Bag(const Bag& other);
	~Bag();
	void bag_insert(ULLI data);
	Bag* bag_union(Bag* other);
	Bag* bag_split();
	void print();
};

