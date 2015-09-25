//Author: Steven Cabral
#include "Input.h"

using namespace std;

Input::Input() {
	int lineNum = 0;
	string stdline;
	int firstArg;
	int secondArg;

	if (!cin.eof()) {
		lineNum++;

		getline(cin, stdline);
		istringstream curLine(stdline);
		if (!(curLine >> method)) {
			cout << "Incorrect entry on line " << lineNum << " of input.txt - expecting integer." << endl;
			throw 1;
		}
		if (!(curLine >> length)) {
			cout << "Incorrect entry on line " << lineNum << " of input.txt - expecting integer." << endl;
			throw 1;
		}
		if (!(curLine >> sourceIndex)) {
			cout << "Incorrect entry on line " << lineNum << " of input.txt - expecting integer." << endl;
			throw 1;
		}
		sourceIndex = sourceIndex - 1;
	}
	stationArray = new PowerStation[length];
	for (int i = 0; i < length; ++i) {
		if (cin.eof()) throw 1;

		lineNum++;
		getline(cin, stdline);
		istringstream curLine(stdline);

		if (!(curLine >> firstArg)) {
			cout << "Incorrect entry on line " << lineNum << " of input.txt - expecting integer." << endl;
			throw 1;
		}
		if (!(curLine >> secondArg)) {
			cout << "Incorrect entry on line " << lineNum << " of input.txt - expecting integer." << endl;
			throw 1;
		}
		stationArray[i].setIndex(i);
		stationArray[i].setPositionX(firstArg);
		stationArray[i].setPositionY(secondArg);
	}

}


Input::~Input() {
	delete[] stationArray;
}

int Input::getMethod() { return method; }
int Input::getLength() { return length; }
int Input::getSourceIndex() { return sourceIndex; }
PowerStation* Input::getStationArray() { return stationArray; }