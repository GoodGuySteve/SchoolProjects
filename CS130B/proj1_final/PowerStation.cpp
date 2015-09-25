//Author: Steven Cabral

#include "PowerStation.h"


PowerStation::PowerStation() {
}


PowerStation::~PowerStation() {
}

//long long int PowerStation::getPowerLevel() { return powerLevel; }
//void PowerStation::setPowerLevel(long long int p) { powerLevel = p; }
int PowerStation::getPositionX() const { return x; }
void PowerStation::setPositionX(int x) { this->x = x; }
int PowerStation::getPositionY() const { return y; }
void PowerStation::setPositionY(int y) { this->y = y; }
int PowerStation::getIndex() const { return index; }
void PowerStation::setIndex(int i) { index = i; }
bool PowerStation::isMarked() const { return marked; }
bool PowerStation::isMarkedTree() const { return markedTree; }
bool PowerStation::isMarkedDC() const { return markedDC; }
void PowerStation::mark() { marked = true; }
void PowerStation::unmark() { marked = false; }
void PowerStation::markTree() { markedTree = true; }
void PowerStation::unmarkTree() { markedTree = false; }
void PowerStation::markDC() { markedDC = true; }
void PowerStation::unmarkDC() { markedDC = false; }