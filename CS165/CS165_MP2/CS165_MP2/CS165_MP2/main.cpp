#include <fstream>
#include <iostream>
#include <sstream>
#include <string>
#include <ctime>

#include "Document.h"
#include "Definitions.h"
#include "Tree.h"

using namespace std;

//TODO make suffix stripper a little less crude to increase accuracy
string& stripSuffix(string& input) {
	int length = input.length();
	if (length < 5) return input; //only strip from larger words

	//strip "ing"
	if (input[length - 1] == 'g' && input[length - 2] == 'n' && input[length - 3] == 'i') {
		input.erase(length - 3, length);
		return input;
	}

	//strip "ed"
	if (input[length - 1] == 'd' && input[length - 2] == 'e') {
		input.erase(length - 2, length);
		return input;
	}

	//strip "ly"
	if (input[length - 1] == 'y' && input[length - 2] == 'l') {
		input.erase(length - 2, length);
		return input;
	}

	//strip "'s"
	if (input[length - 1] == 's' && input[length - 2] == '\'') {
		input.erase(length - 2, length);
		return input;
	}

	//case when no stripping is done
	return input;
}


int main(int argc, char* argv[]) {

	list<Document*> trainDocs;
	list<Document*> testDocs;
	unordered_map<string, int> freqTotal; //contains the total frequencies of all words among all reviews
	unordered_map<string, int> freqNeg; //contains the frequencies of words among all negative reviews
	unordered_map<string, int> freqPos; //contains the frequncies of words among all positive reviews

	string line;
	ofstream trainFileTemp("trainingDataAsFile.txt");
	ifstream testFile(argv[1]);

	time_t start = time(&start);
	time_t end;

	//read stdin into a new file to work around an inconsistency between test computer and csil
	while (getline(cin, line)) {
		trainFileTemp << line << endl;
	}

	trainFileTemp.close();
	ifstream trainFile("trainingDataAsFile.txt");

	//read in the training set and build frequency lists
	if (trainFile.is_open()) {
		while (getline(trainFile, line)) {
			// parse line to get label and data
			string word;
			string cleanWord;
			int cleanLength;
			Document* doc = new Document();
			istringstream curLine(line);
			curLine >> doc->label;
			while (curLine >> word) {
				//clean the words to allow more accurate caluclation of frequencies
				cleanWord = word; //preallocate the length
				cleanLength = 0;

				if (word[0] == '/' && word.length() > 2 && word[2] == '<') continue; //skips the partial HTML tags

				for (unsigned int i = 0; i < word.length(); ++i) {
					char c = tolower(word[i]);

					//record only alphanumeric characters and apostrophes
					if (c > 96 && c < 123 || c > 64 && c < 91 || c > 47 && c < 58 || c == 39) {
						cleanWord[cleanLength++] = c;
					}
					
					//case: HTML break tags - force algorithm to truncate the "br" from the end of the word
					else if (c == '<') {
						if (word.length() > 2 && word[i + 1] == 'b' && word[i + 2] == 'r') {
							word[i + 1] = '.';
							word[i + 2] = '.';
						}
					}
				}

				//wipe the remainder of the overwritten word
				cleanWord.erase(cleanLength, cleanWord.length());

				//strip the suffix
				cleanWord = stripSuffix(cleanWord);

				//add word to document and frequency tables
				if (cleanWord.length() > 0) {
					pair<string, int> hashInsert(cleanWord, 1);
					doc->wordlist.push_back(cleanWord);
					if (!doc->freqRaw.insert(hashInsert).second) {
						++doc->freqRaw.at(cleanWord);
					}
					if (!freqTotal.insert(hashInsert).second) {
						++freqTotal.at(cleanWord);
					}
					if (doc->label == 1) {
						if (!freqPos.insert(hashInsert).second) {
							++freqPos.at(cleanWord);
						}
					}
					else {
						if (!freqNeg.insert(hashInsert).second) {
							++freqNeg.at(cleanWord);
						}
					}
				}
			}
			trainDocs.push_back(doc);
		}
		trainFile.close();
	}

	if (testFile.is_open()) {
		while (getline(testFile, line)) {
			// parse line to get label and data
			string word;
			string cleanWord;
			int cleanLength;
			Document* doc = new Document();
			istringstream curLine(line);
			curLine >> doc->label;
			while (curLine >> word) {
				//clean the words to allow more accurate caluclation of frequencies
				cleanWord = word; //preallocate the length
				cleanLength = 0;

				if (word[0] == '/' && word.length() > 2 && word[2] == '<') continue; //skips the partial HTML tags

				for (unsigned int i = 0; i < word.length(); ++i) {
					char c = tolower(word[i]);

					//record only alphanumeric characters and apostrophes
					if (c > 96 && c < 123 || c > 64 && c < 91 || c > 47 && c < 58 || c == 39) {
						cleanWord[cleanLength++] = c;
					}

					//case: HTML break tags - force algorithm to truncate the "br" from the end of the word
					else if (c == '<') {
						if (word.length() > 2 && word[i + 1] == 'b' && word[i + 2] == 'r') {
							word[i + 1] = '.';
							word[i + 2] = '.';
						}
					}
				}

				//wipe the remainder of the overwritten word
				cleanWord.erase(cleanLength, cleanWord.length());

				//strip the suffix
				cleanWord = stripSuffix(cleanWord);

				//add word to document tables
				if (cleanWord.length() > 0) {
					pair<string, int> hashInsert(cleanWord, 1);
					doc->wordlist.push_back(cleanWord);
					if (!doc->freqRaw.insert(hashInsert).second) {
						++doc->freqRaw.at(cleanWord);
					}
				}
			}
			testDocs.push_back(doc);
		}
		testFile.close();
	}

	//construct a tree using normalized frequencies
	Tree* tree = buildTree(trainDocs);

	//use tree to analyze documents
	double trainAccuracy = 0;
	double testAccuracy = 0;
	for (list<Document*>::iterator docIt = testDocs.begin(); docIt != testDocs.end(); ++docIt) {
		int result = docEval(*docIt, tree);
		std::cout << result << endl;
		if (result == (**docIt).label) testAccuracy += (double) 1.0 / testDocs.size();
	}

	for (list<Document*>::iterator docIt = trainDocs.begin(); docIt != trainDocs.end(); ++docIt) {
		int result = docEval(*docIt, tree);
		if (result == (**docIt).label) trainAccuracy += (double) 1.0 / trainDocs.size();
	}

	//print results
	end = time(&end);
	std::cout << end - start << " seconds" << endl;
	std::cout << trainAccuracy << " training accuracy" << endl;
	std::cout << testAccuracy << " test accuracy" << endl;

	return 0;
}

