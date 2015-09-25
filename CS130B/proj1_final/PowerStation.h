//Author: Steven Cabral
#ifndef _POWER_STATION_H
	#define _POWER_STATION_H

//This class is used mainly as a container for different information about the stations.
class PowerStation {
public:
	//long long int powerLevel;
	int x;
	int y;
	int index;
	bool marked;
	bool markedTree;
	bool markedDC;
	PowerStation();
	~PowerStation();

	//long long int getPowerLevel();
	//void setPowerLevel(long long int p);
	int getPositionX() const;
	void setPositionX(int x);
	int getPositionY() const;
	void setPositionY(int y);
	int getIndex() const;
	void setIndex(int i);
	bool isMarked()const;
	bool isMarkedTree()const;
	bool isMarkedDC()const;
	void mark();
	void unmark();
	void markTree();
	void unmarkTree();
	void markDC();
	void unmarkDC();
};
#endif
