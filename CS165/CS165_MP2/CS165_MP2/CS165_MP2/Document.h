#pragma once

#include <unordered_map>
#include <list>
#include <string>
#include "Definitions.h"

using namespace std;

class Document {
public:
	int label;
	list<string> wordlist;
	unordered_map<string, int> freqRaw;
	unordered_map<string, double>* freqNorm;
	Document();
	~Document();
};