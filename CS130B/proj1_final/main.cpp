//Author: Steven Cabral
#include <iostream>
#include "PowerStation.h"
#include "Input.h"
#include "Algorithm.h"

using namespace std;

int main(int argc, char** argv) {

	Input input;
	PowerStation* stationArray = input.getStationArray();
	int length = input.getLength();
	int sourceIndex = input.getSourceIndex();
	long long int* power = new long long int[length];

	switch (input.getMethod()) {
	case 1:
		ACSF(power, stationArray, length, sourceIndex);
		break;
	case 2:
		ACSet(power, stationArray, length, sourceIndex);
		break;
	case 3:
		ACF(power, stationArray, length, sourceIndex);
		break;
	case 4:
		DC(power, stationArray, length, sourceIndex);
		break;
	default:
		cout << "Error - method number should be an integer between 0 and 3 (inclusive)" << endl;
		return -1;
	}

	//output results from whichever algorithm was used in the function
	//NOTE: all indices must be increased by 1 (internally indexed 0 thru n-1)

	return 0;
}