#pragma once

#include <string>
#include <cmath>
#include <queue>
#include "Definitions.h"
#include "Document.h"

using namespace std;

class Tree {
public:
	long int totalEntries;
	double probPos;
	double probNeg;
	string splitWords[BFAC - 1];
	Tree* lowSplit; //the side of the tree with entries having low frequency of splitWord
	Tree* highSplit[BFAC - 1]; //the side of the tree with entries having high frequency of splitWord

	Tree();
	~Tree();
};

//construct a tree from a set of training documents
Tree* buildTree(list<Document*> docList);

//use a tree to find the tree's evaluation of the document
int docEval(Document* doc, Tree* tree);