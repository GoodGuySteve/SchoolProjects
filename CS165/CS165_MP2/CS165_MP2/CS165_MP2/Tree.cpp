#include "Tree.h"

Tree::Tree()
{
}


Tree::~Tree()
{
}

Tree* buildTree(list<Document*> docList) {

	Tree* retVal = new Tree();

	long int docLenTotal = 0;
	int numPos = 0;
	int numNeg = 0;
	double docLenAvg;

	int posLowDocs;
	int posHighDocs;
	int negLowDocs;
	int negHighDocs;

	double maxInfoVal = 0;
	double curInfoVal;
	double splitFreq;

	double freqCeil = (double) FREQ_MAX * docList.size() / 2000;
	double freqFloor = (double) FREQ_CUTOFF * docList.size() / 2000;

	//stores the top 10 information values
	priority_queue < pair<double, string> > maxInfoVals;
	//iterate over all branches to initialize information values
	for (int i = 0; i < BFAC - 1; ++i) {
		pair<double, string> toInsert(0, "");
		maxInfoVals.push(toInsert);
	}

	unordered_map<string, double> freqTotal; //contains the total frequencies of all words among all reviews
	unordered_map<string, double> freqNeg; //contains the frequencies of words among all negative reviews
	unordered_map<string, double> freqPos; //contains the frequncies of words among all positive reviews

	//calculate average document length
	for (list<Document*>::iterator docIt = docList.begin(); docIt != docList.end(); ++docIt) {
		docLenTotal += (**docIt).wordlist.size(); //document length is the number of words (after cleaning)
		if ((**docIt).label) ++numPos;
		else ++numNeg;
	}

	docLenAvg = (double) docLenTotal / docList.size();

	//base case to stop recursion
	if (docList.size() <= GRANULARITY) {
		retVal->lowSplit = nullptr;
		for (int i = 0; i < BFAC - 1; ++i) {
			retVal->highSplit[i] = nullptr;
		}
		retVal->totalEntries = docList.size();
		retVal->probPos = (double)numPos / docList.size();
		retVal->probNeg = 1 - retVal->probPos;
		
		for (int i = 0; i < BFAC - 1; ++i) {
			retVal->splitWords[i] = "";
		}

		return retVal;
	}



	//normalize all the frequencies for the each document
	for (list<Document*>::iterator docIt = docList.begin(); docIt != docList.end(); ++docIt) {
		Document* doc = *docIt;
		doc->freqNorm = new unordered_map<string, double>();
		for (unordered_map<string, int>::iterator mapIt = (**docIt).freqRaw.begin(); \
			mapIt != (**docIt).freqRaw.end(); ++mapIt) {
			pair<string, double> normInsert;
			normInsert.first = mapIt->first;
			normInsert.second = (mapIt->second * (TF_NORM_K + 1)) / (mapIt->second \
				+ TF_NORM_K * (1 - TF_NORM_B + TF_NORM_B * ((*docIt)->wordlist.size() / docLenAvg)));
			doc->freqNorm->insert(normInsert);
		}
	}

	//iterate over all documents to fill freqTotal, freqNeg, and freqPos for the frequencies in the subset
	for (list<Document*>::iterator docIt = docList.begin(); docIt != docList.end(); ++docIt) {

		if ((**docIt).label) {
			for (unordered_map<string, double>::iterator mapIt = (**docIt).freqNorm->begin(); \
				mapIt != (**docIt).freqNorm->end(); ++mapIt) {
				if (!freqTotal.insert(*mapIt).second) { //if string already present
					freqTotal.at(mapIt->first) += mapIt->second;
				}
				if (!freqPos.insert(*mapIt).second) { //if string already present
					freqPos.at(mapIt->first) += mapIt->second;
				}
			}
		}
		else {
			for (unordered_map<string, double>::iterator mapIt = (**docIt).freqNorm->begin(); \
				mapIt != (**docIt).freqNorm->end(); ++mapIt) {
				if (!freqTotal.insert(*mapIt).second) { //if string already present
					freqTotal.at(mapIt->first) += mapIt->second;
				}
				if (!freqNeg.insert(*mapIt).second) { //if string already present
					freqNeg.at(mapIt->first) += mapIt->second;
				}
			}
		}
	}

	//find a good split word using by calculating information gain for all words
	for (unordered_map<string, double>::iterator it = freqTotal.begin(); it != freqTotal.end(); ++it) {
		//Trim stop words and infrequent words (words with little information)
		if (it->second <= freqFloor || it->second >= freqCeil || it->first == "") continue;

		try {
			curInfoVal = freqPos.at(it->first) / freqNeg.at(it->first);
			if (curInfoVal < 1) curInfoVal = 1 / curInfoVal;
			if (curInfoVal > -maxInfoVals.top().first) {
				maxInfoVals.pop();
				pair <double, string> betterInfo(-curInfoVal, it->first);
				maxInfoVals.push(betterInfo);
				//maxInfoVal = curInfoVal;
			}
		}
		catch (...) {
			continue;
		}

	}

	for (int i = BFAC - 2; i >= 0; --i) {
		retVal->splitWords[i] = maxInfoVals.top().second;
		maxInfoVals.pop();
	}

	//split tree based on the split word
	if (retVal->splitWords[0] == "") { //no valid frequencies found - cut tree here
		retVal->lowSplit = nullptr;
		for (int i = 0; i < BFAC - 1; ++i) {
			retVal->highSplit[i] = nullptr;
		}
		retVal->totalEntries = docList.size();
		retVal->probPos = (double)numPos / docList.size();
		retVal->probNeg = 1 - retVal->probPos;
		//delete the allocated freqNorm memory
		for (list<Document*>::iterator docIt = docList.begin(); docIt != docList.end(); ++docIt) {
			delete (**docIt).freqNorm;
		}
		return retVal;
	}
	else {
		list<Document*>* lowList = new list < Document* >();
		list<Document*>* highList = new list <Document*>[BFAC - 1];
		
		//sort into different lists
		for (list<Document*>::iterator docIt = docList.begin(); docIt != docList.end(); ++docIt) {
			for (int i = 0; i < BFAC; ++i) {
				if (i == BFAC - 1) {
					lowList->push_back(*docIt);
					break;
				}
				if (retVal->splitWords[i] == "") continue;
				try {
					if ((**docIt).freqNorm->at(retVal->splitWords[i]) >= 0) {
						highList[i].push_back(*docIt);
						break;
					}
					else {
						//lowList->push_back(*docIt);
						continue;
					}
				}
				catch (...) {
					//lowList->push_back(*docIt);
					continue;
				}
			}
		}

		//delete the allocated freqNorm memory
		for (list<Document*>::iterator docIt = docList.begin(); docIt != docList.end(); ++docIt) {
			delete (**docIt).freqNorm;
		}

		//recursive calls to construct subtrees
		retVal->lowSplit = buildTree(*lowList);
		for (int i = 0; i < BFAC - 1; ++i) {
			if (retVal->splitWords[i] == "") {
				retVal->highSplit[i] = nullptr;
			}
			else {
				retVal->highSplit[i] = buildTree(highList[i]);
			}
		}
		retVal->totalEntries = docList.size();
		retVal->probPos = (double)numPos / retVal->totalEntries;
		retVal->probNeg = 1 - retVal->probPos;

		delete[] highList;
		delete lowList;
	}
	return retVal;

}

int docEval(Document* doc, Tree* tree) {
	int index;
	while (tree->splitWords[0] != "") {
		index = BFAC - 1;
		//test if any split word is in the document
		for (int i = 0; i < BFAC - 1; ++i) {
			try {
				if (doc->freqRaw.at(tree->splitWords[i])) {
					index = i;
					break;
				}
				continue;
			}
			catch (...) {
				continue;
			}
		}
		if (index == BFAC - 1) { //no split word was found in the document
			tree = tree->lowSplit;
		}
		else {
			tree = tree->highSplit[index];
		}

	}

	//now we are at the leaf node
	if (tree->probPos > 0.5) return 1;
	else return 0;
}