//Author 1: Steven Cabral
//Author 2: Bronwyn Perry-Huston


//Bag and Pennant classes based on the classes found in the paper by Leiserson
//and Schardl linked to on the BFS project page
//TODO proper citation?

#pragma once

#include <iostream>
#include <cmath>

#define ULLI int //unsigned long long int
#define nullptr NULL

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
	int getSize();
	void toArray(ULLI* array, int& currIndex);
	void print();
};

class Bag {
public:
	int scale; //scale is the log of the max vertex size
	ULLI size; //number of elements in the bag
	Pennant** penArray;
	Bag(); //NOTE: do NOT call default constructor unless init is called afterwards
	void init(int scale);
	Bag(int scale);
	void doubleSize();
	Bag(const Bag& other);
	Bag& operator=(const Bag& other);
	~Bag();
	void bag_insert(ULLI data);
	Bag* bag_union(Bag* other);
	Bag* bag_split();
	ULLI* toArray();
	void print();
};

