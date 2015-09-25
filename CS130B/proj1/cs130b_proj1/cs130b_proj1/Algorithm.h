//Author: Steven Cabral
//File contains all four algorithms used in finding the lowest power schedule
#include <cstdlib>
#include <iostream>
#include "PowerStation.h"

using namespace std;

void ACSF(long long int* power, PowerStation* stationArray, int length, int sourceIndex);
void ACSet(long long int* power, PowerStation* stationArray, int length, int sourceIndex);
void ACF(long long int* power, PowerStation* stationArray, int length, int sourceIndex);
void DC(long long int* power, PowerStation* stationArray, int length, int sourceIndex);

void ownGreedy(long long int* power, PowerStation* stationArray, int length, int sourceIndex);
void DPnomem(long long int* power, PowerStation* stationArray, int length, int sourceIndex);
void DPmem(long long int* power, PowerStation* stationArray, int length, int sourceIndex);
void DPiter(long long int* power, PowerStation* stationArray, int length, int sourceIndex);