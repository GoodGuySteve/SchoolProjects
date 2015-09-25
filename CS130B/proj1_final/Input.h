//Author: Steven Cabral
#pragma once

#include <fstream>
#include <iostream>
#include <string>
#include <sstream>
#include "PowerStation.h"

using namespace std;

//Input class takes a file as input and parses the inputs into a usable format
class Input
{
private:
	int method; //1:ACSF 2:ACSet 3:ACF 4:DC
	int length;
	int sourceIndex; //indices go from 0 to length-1
	PowerStation* stationArray;

public:
	Input();
	int getMethod();
	int getLength();
	int getSourceIndex();
	PowerStation* getStationArray();
	~Input();
};