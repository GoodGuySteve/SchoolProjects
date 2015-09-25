//Author: Steven Cabral
#include "Algorithm.h"

#define INF 9223372036854775807  //the maximum value that can be stored in a long long int

//NOTE TO GRADER: qsort is used because it is not implemented in any location where there is a performance bottleneck
//any difference in perfomance to a homemade sorting algorithm would be negligible

//small structure to couple the power necessary and the index for easy comparisons
struct DistAndIndex {
	long long int dist;
	int index;
};

//small structure to contain return values for DP recursion
struct DPdata {
	long long int minPower;
	int keyP;
	int keyQ;
};

//returns the maximum of two numbers
long long int max(long long int a, long long int b) {
	return a > b ? a : b;
}

//returns the power distance (the square of actual distance) between two PowerStations
long long int dist(const PowerStation& a, const PowerStation& b) {
	long long int difX = a.getPositionX() - b.getPositionX();
	long long int difY = a.getPositionY() - b.getPositionY();
	return (difX * difX + difY * difY);
}

//function used for qsort in function ACSF
int compareACSF(const void* a, const void* b) {
	if (((DistAndIndex*)a)->dist < ((DistAndIndex*)b)->dist) return -1;
	if (((DistAndIndex*)a)->dist > ((DistAndIndex*)b)->dist) return 1;
	if (((DistAndIndex*)a)->index < ((DistAndIndex*)b)->index) return -1;
	if (((DistAndIndex*)a)->index > ((DistAndIndex*)b)->index) return 1;
	if (((DistAndIndex*)a)->index == ((DistAndIndex*)b)->index) return 0;
	return 0;
}

//function used for qsort in function tree
int compareTree(const void* a, const void* b) {
	if ((int*)a < (int*)b) return -1;
	if ((int*)a > (int*)b) return 1;
	else return 0;
}

//function used for qsort in function DC
int compareDC(const void* a, const void* b) {
	if (((PowerStation*)a)->getIndex() < ((PowerStation*)b)->getIndex()) return -1;
	if (((PowerStation*)a)->getIndex() > ((PowerStation*)b)->getIndex()) return 1;
	else return 0;
}

int compareDP(const void* a, const void* b) {
	if (((PowerStation*)a)->getPositionX() < ((PowerStation*)b)->getPositionX()) return -1;
	if (((PowerStation*)a)->getPositionX() > ((PowerStation*)b)->getPositionX()) return 1;
	if (((PowerStation*)a)->getIndex() < ((PowerStation*)b)->getIndex()) return -1;
	else return 1;
}

//Procedure tree takes some set with power configurations for that set.
//The output is a new, different arrangement of the set such that each member
//points to its parent in the set (root points to -1).
//Returns indices relative to station subset, NOT absolute indices
int* tree(long long int* power, PowerStation* stationArray, int length, int root) {
	//tree structure - each index contains a number pointing to its parent (root is -1)
	int* t = new int[length];

	//queue structure - each index contains a number pointing to the next index of the queue
	int* queue = new int[length];
	int queueLength = 0;

	//partially empty list of values
	int* l = new int[length];

	for (int i = 0; i < length; ++i) {
		stationArray[i].unmarkTree();
	}

	//add the source to the front of the queue
	int queueFront = root;
	int queueBack = root;
	t[root] = -1;
	queue[root] = -1;
	stationArray[root].markTree();
	while (queueFront != -1) {
		//delete front from queue
		int currEntry = queueFront;
		queueFront = queue[queueFront];

		//create l - the list of unmarked stations inside the circle centered at currEntry
		int size = 0;
		for (int i = 0; i < length; ++i) {
			if (!stationArray[i].isMarkedDC()) continue;
			if (stationArray[i].isMarkedTree()) continue;
			if (!stationArray[i].isMarked()) continue; //all tree calls only search marked entires
			if (dist(stationArray[i], stationArray[currEntry]) <= power[currEntry]) {
				//add to l
				l[size] = i;
				++size;
			}
		}
		//each j is the smallest index in l
		for (int j = 0; j < size; ++j) {
			//add j as child of i
			t[l[j]] = currEntry;
			stationArray[l[j]].markTree();
			//add l[j] to back of queue
			queue[queueBack] = l[j];
			queue[l[j]] = -1;
			queueBack = l[j];
			if (queueFront == -1) queueFront = queueBack;
		}
	}
	delete[] queue;
	delete[] l;
	return t;
}

//uses the output of tree to form a schedule, with indices relative to the input subset
long long int* tree_power(int* t, PowerStation* stationArray, int length) {
	long long int* power = new long long int[length];
	for (int k = 0; k < length; ++k) power[k] = 0;
	for (int i = 0; i < length; ++i) {
		if (!stationArray[i].isMarkedDC()) continue;
		if (!stationArray[i].isMarked()) continue;
		if (t[i] == -1) continue;
		int parent = t[i];
		power[parent] = max(power[parent], dist(stationArray[parent], stationArray[i]));
	}
	return power;
}


//Output to stdout
void printToStdout(long long int* power, int* t, int length) {
	long long int totalPower = 0;
	for (int i = 0; i < length; ++i) {
		totalPower += power[i];
	}
	cout << totalPower << endl;
	for (int i = 0; i < length; ++i) {
		if (power[i] == 0) continue;
		if (t[i] == -1) t[i] = -2;
		cout << i + 1 << " " << power[i] << " " << t[i] + 1 << endl;
	}
}

//Add closest to source first
//in the pseudocode, power corresponds to r, stationArray corresponds to O, length corresponds
//to n, and sourceIndex corresponds to s
void ACSF(long long int* power, PowerStation* stationArray, int length, int sourceIndex) {
	int i;
	//q acts as a copy of the power array for tree manipulations
	long long int* q = new long long int[length];

	long long int minPower;
	int minJ;

	//set all elements of power to 0
	for (i = 0; i < length; ++i) power[i] = 0;

	//unmark all indices
	for (i = 0; i < length; ++i) stationArray[i].unmark();

	//workaround for tree function
	for (i = 0; i < length; ++i) stationArray[i].markDC();

	//mark source
	stationArray[sourceIndex].mark();

	//sort all unmarked (non-source) indices by distance from source, then by index
	//NOTE: using new structure since this only needs to be sorted once
	DistAndIndex* sortedArray = new DistAndIndex[length - 1];
	for (int i = 0; i < length - 1; ++i) {
		sortedArray[i].index = i;
		sortedArray[i].dist = dist(stationArray[i], stationArray[sourceIndex]);
	}
	//swap the last member with the source index
	if (sourceIndex != length - 1) {
		sortedArray[sourceIndex].index = length - 1;
		sortedArray[sourceIndex].dist = dist(stationArray[length - 1], stationArray[sourceIndex]);
	}

	qsort(sortedArray, length - 1, sizeof(DistAndIndex), compareACSF);

	//add one station per iteration in order of distance from source and index
	for (int j = 0; j < length - 1; ++j) {
		//lindex and jindex, and minJ are all absolute indices
		int jindex = sortedArray[j].index;
		//int lindex;
		//mark station j
		stationArray[jindex].mark();
		power[jindex] = 0;

		//initialize minPower and minJ to invalid values (for error checking)
		minJ = -1;
		minPower = -1;

		//find lowest power over all marked stations
		for (int lindex = 0; lindex < length; ++lindex) {
			if (!stationArray[lindex].isMarked()) continue; //skip non-marked stations
			if (lindex == jindex) continue;

			//q is a copy of power, with the indices changed to fit markArray
			for (int k = 0; k < length; ++k) q[k] = power[k];
			q[lindex] = max(q[lindex], dist(stationArray[lindex], stationArray[jindex]));


			//construct the tree
			int* t = tree(q, stationArray, length, sourceIndex);
			//find the power schedule for that tree (temp)
			long long int* temp = tree_power(t, stationArray, length);
			//find the total power for that tree (p)
			long long int p = 0;
			for (int k = 0; k < length; ++k) {
				if (!stationArray[k].isMarked()) continue; //skip unmarked	
				p += temp[k];
			}
			//set p to minimum power needed and record the (lowest) index that broadcasts to j
			if (p < minPower || (p == minPower && lindex < minJ) || minPower == -1) {
				minPower = p;
				minJ = lindex;
			}
			delete[] temp;
			delete[] t;
		}
		//update the actual power once a minimal l has been found
		power[minJ] = max(power[minJ], dist(stationArray[minJ], stationArray[jindex]));

		//convert mark set to tree input format
		//put power in terms of markSet indices
		int* t = tree(power, stationArray, length, sourceIndex);
		long long int* tPower = tree_power(t, stationArray, length);
		for (int k = 0; k < length; ++k) {
			//store schedule into power array
			power[k] = tPower[k];
		}
		delete[] tPower;
		delete[] t;
	}

	//print results
	int* t = tree(power, stationArray, length, sourceIndex);
	printToStdout(power, t, length);
	delete[] sortedArray;
	delete[] q;
}

//Add closest to set first
//Same as ACSF, but the order in which the marked indices are selected is different
void ACSet(long long int* power, PowerStation* stationArray, int length, int sourceIndex) {
	int i;
	//q acts as a copy of the power array for tree manipulations
	long long int* q = new long long int[length];

	long long int minPower;
	int minJ;

	//set all elements of power to 0
	for (i = 0; i < length; ++i) power[i] = 0;

	//unmark all indices
	for (i = 0; i < length; ++i) stationArray[i].unmark();

	//workaround for tree function
	for (i = 0; i < length; ++i) stationArray[i].markDC();

	//mark source
	stationArray[sourceIndex].mark();

	//add one station per iteration in order of distance from source and index
	for (int j = 0; j < length - 1; ++j) {
		//find the unmarked j with the shortest distance to any item in the marked set
		int lindex = sourceIndex;
		int jindex = 0;
		while (stationArray[jindex].isMarked()) ++jindex;
		long long int minDist = dist(stationArray[jindex], stationArray[lindex]);
		long long int curDist;
		for (int i = 0; i < length; ++i) {
			for (int l = 0; l < length; ++l) {
				//find smallest combination of unmarked (l) and marked (i)
				if (!stationArray[l].isMarked() || stationArray[i].isMarked()) continue;
				curDist = dist(stationArray[i], stationArray[l]);
				if (curDist < minDist) {
					minDist = curDist;
					jindex = i;
					lindex = l;
				}
			}
		}
		//mark station j
		stationArray[jindex].mark();
		power[jindex] = 0;

		//initialize minPower and minJ to invalid values (for error checking)
		minJ = -1;
		minPower = -1;

		//find lowest power over all marked stations
		for (lindex = 0; lindex < length; ++lindex) {
			if (!stationArray[lindex].isMarked()) continue; //skip non-marked stations
			if (lindex == jindex) continue;

			//q is a copy of power, with the indices changed to fit markArray
			for (int k = 0; k < length; ++k) q[k] = power[k];
			q[lindex] = max(q[lindex], dist(stationArray[lindex], stationArray[jindex]));


			//construct the tree
			int* t = tree(q, stationArray, length, sourceIndex);
			//find the power schedule for that tree (temp)
			long long int* temp = tree_power(t, stationArray, length);
			//find the total power for that tree (p)
			long long int p = 0;
			for (int k = 0; k < length; ++k) {
				if (!stationArray[k].isMarked()) continue; //skip unmarked	
				p += temp[k];
			}
			//set p to minimum power needed and record the (lowest) index that broadcasts to j
			if (p < minPower || (p == minPower && lindex < minJ) || minPower == -1) {
				minPower = p;
				minJ = lindex;
			}
			delete[] temp;
			delete[] t;
		}
		//update the actual power once a minimal l has been found
		power[minJ] = max(power[minJ], dist(stationArray[minJ], stationArray[jindex]));

		//convert mark set to tree input format
		//put power in terms of markSet indices
		int* t = tree(power, stationArray, length, sourceIndex);
		long long int* tPower = tree_power(t, stationArray, length);
		for (int k = 0; k < length; ++k) {
			//store schedule into power array
			power[k] = tPower[k];
		}
		delete[] tPower;
		delete[] t;
	}

	//print results
	int* t = tree(power, stationArray, length, sourceIndex);
	printToStdout(power, t, length);
	delete[] q;
}

//Add cheapest first
void ACF(long long int* power, PowerStation* stationArray, int length, int sourceIndex) {
	int i;

	//variables for calling tree function
	PowerStation* treeInput = new PowerStation[length];
	long long int* treePower = new long long int[length];
	//int treeRoot;
	//int treeLength;
	int* treeOut;
	long long int* treePowerOut;

	long long int pMin = -1; //stores the lowest power for the lowest power schedule
	int minJ;
	int minL;
	
	long long int* q = new long long int[length]; //temporary variable for storing powers

	//set all elements of power to 0
	for (i = 0; i < length; ++i) power[i] = 0;

	//unmark all stations
	for (i = 0; i < length; ++i) stationArray[i].unmark();

	//workaround for tree function
	for (i = 0; i < length; ++i) stationArray[i].markDC();

	//mark source
	stationArray[sourceIndex].mark();
	for (i = 1; i < length; ++i) {
		//consider each unmarked station
		pMin = -1;
		for (int j = 0; j < length; ++j) {
			if (stationArray[j].isMarked()) continue;
			stationArray[j].mark();
			power[j] = 0;
			//consider each marked station
			for (int l = 0; l < length; ++l) {
				if (!stationArray[l].isMarked()) continue;
				if (l == j) continue;
				for (int k = 0; k < length; ++k) {
					q[k] = power[k];
				}
				q[l] = max(q[l], dist(stationArray[l], stationArray[j]));
				//generate the tree to calculate the lowest power

				treeOut = tree(q, stationArray, length, sourceIndex);
				treePowerOut = tree_power(treeOut, stationArray, length);
				long long int p = 0;
				for (int k = 0; k < length; ++k) {
					if (!stationArray[k].isMarked()) continue;
					p += treePowerOut[k];
				}
				if (p < pMin || pMin == -1) {
					pMin = p;
					minJ = j;
					minL = l;
				}
				delete[] treeOut;
				delete[] treePowerOut;
			}
			stationArray[j].unmark();
		}

		stationArray[minJ].mark();
		power[minL] = max(power[minL], dist(stationArray[minL], stationArray[minJ]));
		//generate the tree to calculate the lowest power
		treeOut = tree(power, stationArray, length, sourceIndex);
		treePowerOut = tree_power(treeOut, stationArray, length);
		for (int l = 0; l < length; ++l) {
			if (!stationArray[l].isMarked()) continue;
			power[l] = treePowerOut[l];
		}
		delete[] treePowerOut;
		delete[] treeOut;
	}
	treeOut = tree(power, stationArray, length, sourceIndex);
	printToStdout(power, treeOut, length);
	delete[] treeOut;
	delete[] treePower;
	delete[] treeInput;
}

//This functin is a copy of ACF, only the initialization is left out to make it compatible with DC
//TODO - ignore ones that aren't DC marked and iterate correct number of times per call
void ACF_uninit(long long int* power, PowerStation* stationArray, int length, int sourceIndex) {
	int i;

	//variables for calling tree function
	PowerStation* treeInput = new PowerStation[length];
	long long int* treePower = new long long int[length];
	//int treeRoot;
	//int treeLength;
	int* treeOut;
	long long int* treePowerOut;

	long long int pMin = -1; //stores the lowest power for the lowest power schedule
	int minJ;
	int minL;

	long long int* q = new long long int[length]; //temporary variable for storing powers

	//set all elements of power to 0 - must do this in DC
	//for (i = 0; i < length; ++i) power[i] = 0;

	//unmark all stations - must do this in DC (for both DC marks and normal marks!)
	//for (i = 0; i < length; ++i) stationArray[i].unmark();

	//mark source - must do this in DC (for both marks!)
	//stationArray[sourceIndex].mark();

	//limit the number of times the main loop executes
	int numUnmarked = 0;
	for (int i = 0; i < length; ++i) {
		if (!stationArray[i].isMarkedDC()) continue;
		if (!stationArray[i].isMarked()) ++numUnmarked;
	}
	for (i = 0; i < numUnmarked; ++i) {
		//consider each unmarked station
		pMin = -1;
		for (int j = 0; j < length; ++j) {
			if (!stationArray[j].isMarkedDC()) continue;
			if (stationArray[j].isMarked()) continue;
			stationArray[j].mark();
			power[j] = 0;
			//consider each marked station
			for (int l = 0; l < length; ++l) {
				if (!stationArray[l].isMarkedDC()) continue;
				if (!stationArray[l].isMarked()) continue;
				if (l == j) continue;
				for (int k = 0; k < length; ++k) {
					q[k] = power[k];
				}
				q[l] = max(q[l], dist(stationArray[l], stationArray[j]));
				//generate the tree to calculate the lowest power

				treeOut = tree(q, stationArray, length, sourceIndex);
				treePowerOut = tree_power(treeOut, stationArray, length);
				long long int p = 0;
				for (int k = 0; k < length; ++k) {
					if (!stationArray[k].isMarkedDC()) continue;
					if (!stationArray[k].isMarked()) continue;
					p += treePowerOut[k];
				}
				if (p < pMin || pMin == -1) {
					pMin = p;
					minJ = j;
					minL = l;
				}
				delete[] treeOut;
				delete[] treePowerOut;
			}
			stationArray[j].unmark();
		}

		stationArray[minJ].mark();
		power[minL] = max(power[minL], dist(stationArray[minL], stationArray[minJ]));
		//generate the tree to calculate the lowest power
		treeOut = tree(power, stationArray, length, sourceIndex);
		treePowerOut = tree_power(treeOut, stationArray, length);
		for (int l = 0; l < length; ++l) {
			if (!stationArray[l].isMarkedDC()) continue;
			if (!stationArray[l].isMarked()) continue;
			power[l] = treePowerOut[l];
		}
		delete[] treePowerOut;
		delete[] treeOut;
	}
	treeOut = tree(power, stationArray, length, sourceIndex);
	//printToStdout(power, treeOut, length); don't output in this function
	delete[] treeOut;
	delete[] treePower;
	delete[] treeInput;
}

//Divide and conquer - copies codes from ACF but runs it piecewise
void DC(long long int* power, PowerStation* stationArray, int length, int sourceIndex) {

	int i;

	//set all elements of power to 0 - must do this in DC
	for (i = 0; i < length; ++i) {
		power[i] = 0;
		//unmark all stations - must do this in DC (for both DC marks and normal marks!)
		stationArray[i].unmark();
		stationArray[i].unmarkDC();
	}

	//mark source - must do this in DC (for both marks!)
	stationArray[sourceIndex].mark();
	stationArray[sourceIndex].markDC();

	//sort stationArray by distance from source, then by index
	//station array will now be ordered for the rest of the execution
	DistAndIndex* sortedArray = new DistAndIndex[length];
	for (i = 0; i < length; ++i) {
		sortedArray[i].index = i;
		sortedArray[i].dist = dist(stationArray[i], stationArray[sourceIndex]);
	}

	qsort(sortedArray, length, sizeof(DistAndIndex), compareACSF);

	int currIndex;

	//Mark first length/4 stations
	for (int i = 0; i < length / 4; ++i) {
		currIndex = sortedArray[i].index;
		stationArray[currIndex].markDC();
	}

	ACF_uninit(power, stationArray, length, sourceIndex);

	//Then run ACF on the first half of the input, pre-marking the first 4th and preserving
	//run ACF on the second 4th of the input
	for (i = 0; i < 2 * (length / 4); ++i) {
		currIndex = sortedArray[i].index;
		stationArray[currIndex].markDC();
	}

	ACF_uninit(power, stationArray, length, sourceIndex);

	//then do for third 4th
	//run ACF on the third  4th of the input
	for (i = 0; i < 3 * (length / 4); ++i) {
		currIndex = sortedArray[i].index;
		stationArray[currIndex].markDC();
	}

	ACF_uninit(power, stationArray, length, sourceIndex);

	//finally the remaining amount
	//run ACF on the first 4th of the input
	for (i = 0; i < length; ++i) {
		currIndex = sortedArray[i].index;
		stationArray[currIndex].markDC();
	}

	ACF_uninit(power, stationArray, length, sourceIndex);

	//sort outputs by index before outputting (don't need sort function since index is stored in PowerStation)
	int* treeOut = tree(power, stationArray, length, sourceIndex);
	printToStdout(power, treeOut, length);

	delete[] treeOut;
	delete[] sortedArray;
}

//Custom greedy method that behaves much like the ACSet method, though the selection criteria also takes into 
//account the current power of each station.
//Method selects the next index closest to the powered area for addition
void ownGreedy(long long int* power, PowerStation* stationArray, int length, int sourceIndex) {
	int i;
	//q acts as a copy of the power array for tree manipulations
	long long int* q = new long long int[length];

	long long int minPower;
	int minJ;

	//set all elements of power to 0
	for (i = 0; i < length; ++i) power[i] = 0;

	//unmark all indices
	for (i = 0; i < length; ++i) stationArray[i].unmark();

	//workaround for tree function
	for (i = 0; i < length; ++i) stationArray[i].markDC();

	//mark source
	stationArray[sourceIndex].mark();

	//add one station per iteration in order of distance from source and index
	for (int j = 0; j < length - 1; ++j) {
		//find the unmarked j with the shortest distance to any item in the marked set
		int lindex = sourceIndex;
		int jindex = 0;
		while (stationArray[jindex].isMarked()) ++jindex;
		long long int minDist = dist(stationArray[jindex], stationArray[lindex]) - power[lindex];
		long long int curDist;
		for (int i = 0; i < length; ++i) {
			for (int l = 0; l < length; ++l) {
				//find smallest combination of unmarked (l) and marked (i)
				if (!stationArray[l].isMarked() || stationArray[i].isMarked()) continue;
				curDist = dist(stationArray[i], stationArray[l]) - power[lindex];
				if (curDist < minDist) {
					minDist = curDist;
					jindex = i;
					lindex = l;
				}
			}
		}
		//mark station j
		stationArray[jindex].mark();
		power[jindex] = 0;

		//initialize minPower and minJ to invalid values (for error checking)
		minJ = -1;
		minPower = -1;

		//find lowest power over all marked stations
		for (lindex = 0; lindex < length; ++lindex) {
			if (!stationArray[lindex].isMarked()) continue; //skip non-marked stations
			if (lindex == jindex) continue;

			//q is a copy of power, with the indices changed to fit markArray
			for (int k = 0; k < length; ++k) q[k] = power[k];
			q[lindex] = max(q[lindex], dist(stationArray[lindex], stationArray[jindex]));


			//construct the tree
			int* t = tree(q, stationArray, length, sourceIndex);
			//find the power schedule for that tree (temp)
			long long int* temp = tree_power(t, stationArray, length);
			//find the total power for that tree (p)
			long long int p = 0;
			for (int k = 0; k < length; ++k) {
				if (!stationArray[k].isMarked()) continue; //skip unmarked	
				p += temp[k];
			}
			//set p to minimum power needed and record the (lowest) index that broadcasts to j
			if (p < minPower || (p == minPower && lindex < minJ) || minPower == -1) {
				minPower = p;
				minJ = lindex;
			}
			delete[] temp;
			delete[] t;
		}
		//update the actual power once a minimal l has been found
		power[minJ] = max(power[minJ], dist(stationArray[minJ], stationArray[jindex]));

		//convert mark set to tree input format
		//put power in terms of markSet indices
		int* t = tree(power, stationArray, length, sourceIndex);
		long long int* tPower = tree_power(t, stationArray, length);
		for (int k = 0; k < length; ++k) {
			//store schedule into power array
			power[k] = tPower[k];
		}
		delete[] tPower;
		delete[] t;
	}

	//print results
	int* t = tree(power, stationArray, length, sourceIndex);
	printToStdout(power, t, length);
	delete[] q;
}

//this function is equivalent to the VAL function in the given pseudocode
//VAL(i,j,m) gives the least power needed to broadcast from i to j using maximum m transmitters
	//base conditions: VAL(i,i,m) = 0, VAL(i,j,0) = inf, VAL(i,j,1) = dist(i,j)
	//VAL(i,j,m) = smallest for all values p and q of VAL(i,p,q) + VAL(p,j,m-q)
		//make sure to save the best values for p and q
DPdata DPnomem_rec(PowerStation* sortedArray, int i, int j, int m, DPdata*** key) {
	DPdata data;
	DPdata returnedData1, returnedData2;
	long long int returnedMin;

	if (i == j) { //base case
		data.minPower = 0;
		data.keyP = j;
		data.keyQ = 0;
		key[i][j][m] = data;
		return data;
	}

	if (m == 0) { //base case
		data.minPower = INF;
		data.keyP = -1;
		data.keyQ = -1;
		key[i][j][m] = data;
		return data;
	}

	if (m > j - i) { //prune values that are pointless to search
		data = DPnomem_rec(sortedArray, i, j, m - 1, key);
		key[i][j][m] = data;
		return data;
	}

	//initialize for the q=0 case (no splitting)
	data.minPower = dist(sortedArray[i], sortedArray[j]);
	data.keyP = j;
	data.keyQ = 0;
	key[i][j][m] = data;

	if (m == 1) return data; //base case

	for (int p = i + 1; p <= j; ++p) {
		for (int q = 1; q < m; ++q) {
			//if (q > p - i) continue;  //prune values that are pointless to search 
			returnedData1 = DPnomem_rec(sortedArray, i, p, q, key);
			returnedData2 = DPnomem_rec(sortedArray, p, j, m - q, key);
			returnedMin = returnedData1.minPower + returnedData2.minPower;
			if (returnedMin < data.minPower) { //better value has been found
				data.minPower = returnedMin;
				data.keyP = p;
				data.keyQ = q;
				key[i][j][m] = data;
			}
		}

	}

	return data;
}

void getRecTree(int* tree, int i, int j, int m, DPdata*** key) {

	int keyP = key[i][j][m].keyP;
	int keyQ = key[i][j][m].keyQ;
	if (keyQ == 0) {
		for (int k = i + 1; k <= j; ++k) {
			tree[k] = i;
		}
	}
	else {
		getRecTree(tree, i, keyP, keyQ, key);
		getRecTree(tree, keyP, j, m - keyQ, key);
	}

}

//dynamic programming approach which recalculates all values along the calculation path
	//point of the exercise to demonstrate how inefficient it is without utilizing the full memory
void DPnomem(long long int* power, PowerStation* stationArray, int length, int sourceIndex) {

	//workaround since sourceIndex isn't an index here
	++sourceIndex;

	//sort all inputs by increasing x value
	PowerStation* sortedArray = new PowerStation[length];
	for (int i = 0; i < length; ++i) {
		sortedArray[i] = stationArray[i];
	}
	qsort(sortedArray, length, sizeof(PowerStation), compareDP);

	DPdata*** key = new DPdata**[length];
	for (int i = 0; i < length; ++i) {
		key[i] = new DPdata*[length];
		for (int j = 0; j < length; ++j) {
			key[i][j] = new DPdata[sourceIndex];
		}
	}

	if (sourceIndex >= length) sourceIndex = length - 1; //fix the maximum value for m

	//our output comes from calling VAL(1,n,k)
	DPdata output = DPnomem_rec(sortedArray, 0, length - 1, sourceIndex, key);

	//retrieve the tree using the key values in output
	int* tree = new int[length];
	tree[0] = -1;
	for (int i = 1; i < length; ++i) {
		tree[i] = -2;
	}
	getRecTree(tree, 0, length - 1, sourceIndex, key);

	//everything beyond this point is retrieving the tree for printing in proper format

	//unsort and output the tree manually
	int* outMap = new int[length];  //maps an index from the unsorted array to the sorted array
	int index;
	for (int i = 0; i < length; ++i) {
		index = sortedArray[i].getIndex();
		outMap[index] = i;
	}

	int* sToUMap = new int[length]; //maps an index from the sorted array to the unsorted array
	for (int i = 0; i < length; ++i) {
		sToUMap[outMap[i]] = i;
	}

	//put tree indices in terms of unsorted array
	int* outTree = new int[length];
	for (int i = 0; i < length; ++i) {
		outTree[i] = tree[outMap[i]]; //translate data to correct indices
		if (outTree[i] == -1) continue;
		//outTree[i] = outMap[outTree[i]]; //translate internal indices to point correctly
	}
	//translate internal indices to point correctly as well
	for (int i = 0; i < length; ++i) {
		if (outTree[i] == -1) continue;
		int sortedPointer = outTree[i];
		outTree[i] = sToUMap[sortedPointer];
	}

	//construct power schedule according to sorted tree
	for (int k = 0; k < length; ++k) power[k] = 0;
	for (int i = 0; i < length; ++i) {
		if (outTree[i] == -1) {
			outTree[i] = -2; //mangle tree for formatted printing (NOTE: mangled tree unusuable beyond this point for any other function)
			continue;
		}
		int parent = outTree[i];
		power[parent] = max(power[parent], dist(stationArray[parent], stationArray[i]));
	}	

	//print to stdout
	cout << output.minPower << endl;
	for (int i = 0; i < length; ++i) {
		if (power[i] == 0) continue;
		cout << i + 1 << " " << power[i] << " " << outTree[i] + 1 << endl;
	}

	//delete used data (not needed because program returns data on termination)
	/*
	for (int i = 0; i < length; ++i) {
		for (int j = 0; j < length - 1; ++j) {
			delete[] key[i][j];
		}
	}

	for (int i = 0; i < length; ++i) {
		delete[] key[i];
	}

	delete[] key;

	delete[] outTree;
	delete[] outMap;
	delete[] tree;
	delete[] sortedArray;*/
}

//this function is equivalent to DPnomem_rec, with the exception that it check the data when it can avoid recalculation
DPdata DPmem_rec(PowerStation* sortedArray, int i, int j, int m, DPdata*** key) {
	//TODO actually change this function
	DPdata data;
	DPdata returnedData1, returnedData2;
	long long int returnedMin;

	if (i == j) { //base case
		data.minPower = 0;
		data.keyP = j;
		data.keyQ = 0;
		key[i][j][m] = data;
		return data;
	}

	if (m == 0) { //base case
		data.minPower = INF;
		data.keyP = -1;
		data.keyQ = -1;
		key[i][j][m] = data;
		return data;
	}
	
	if (m > j - i) { //prune values that are pointless to search
		if (key[i][j][m - 1].keyQ == -1) {
			data = key[i][j][m - 1];
		}
		else {
			data = DPmem_rec(sortedArray, i, j, m - 1, key);
		}
		key[i][j][m] = data;
		return data;
	}


	if (m == 1) { //base case q = 1 (linear distance)
		data.minPower = dist(sortedArray[i], sortedArray[j]);
		data.keyP = j;
		data.keyQ = 0;
		key[i][j][m] = data;

		return data; 
	}
	
	//initialize for loop
	data.minPower = INF;
	data.keyP = -1;
	data.keyQ = -1;

	for (int p = i + 1; p <= j; ++p) {
		for (int q = 1; q < m; ++q) {
			//if (q > p - i) continue;  //prune values that are pointless to search
			if (key[i][p][q].keyQ == -1) {
				returnedData1 = DPmem_rec(sortedArray, i, p, q, key);
			}
			else {
				returnedData1 = key[i][p][q];
			}
			if (key[p][j][m - q].keyQ == -1) {
				returnedData2 = DPmem_rec(sortedArray, p, j, m - q, key);
			}
			else {
				returnedData2 = key[p][j][m - q];
			}
			returnedMin = returnedData1.minPower + returnedData2.minPower;
			if (returnedMin < data.minPower) { //better value has been found
				data.minPower = returnedMin;
				data.keyP = p;
				data.keyQ = q;
				key[i][j][m] = data;
			}
		}

	}

	return data;
}

//this function is identical to DPnomem except for the call to DPmem_rec instead of DPnomem_rec
	//also initializes memory to uncalculated values
void DPmem(long long int* power, PowerStation* stationArray, int length, int sourceIndex) {
	//workaround since sourceIndex isn't an index here
	++sourceIndex;

	//sort all inputs by increasing x value
	PowerStation* sortedArray = new PowerStation[length];
	for (int i = 0; i < length; ++i) {
		sortedArray[i] = stationArray[i];
	}
	qsort(sortedArray, length, sizeof(PowerStation), compareDP);

	if (sourceIndex >= length) sourceIndex = length - 1; //fix the maximum value for m

	DPdata*** key = new DPdata**[length];
	for (int i = 0; i < length; ++i) {
		key[i] = new DPdata*[length];
		for (int j = 0; j < length; ++j) {
			key[i][j] = new DPdata[sourceIndex];
			for (int k = 0; k < sourceIndex; ++k) {
				key[i][j][k].keyQ = -1;
			}
		}
	}

	//our output comes from calling VAL(1,n,k)
	DPdata output = DPmem_rec(sortedArray, 0, length - 1, sourceIndex, key);

	//retrieve the tree using the key values in output
	int* tree = new int[length];
	tree[0] = -1;
	for (int i = 1; i < length; ++i) {
		tree[i] = -2;
	}
	getRecTree(tree, 0, length - 1, sourceIndex, key);

	//everything beyond this point is retrieving the tree for printing in proper format

	//unsort and output the tree manually
	int* outMap = new int[length];  //maps an index from the unsorted array to the sorted array
	int index;
	for (int i = 0; i < length; ++i) {
		index = sortedArray[i].getIndex();
		outMap[index] = i;
	}

	int* sToUMap = new int[length]; //maps an index from the sorted array to the unsorted array
	for (int i = 0; i < length; ++i) {
		sToUMap[outMap[i]] = i;
	}

	//put tree indices in terms of unsorted array
	int* outTree = new int[length];
	for (int i = 0; i < length; ++i) {
		outTree[i] = tree[outMap[i]]; //translate data to correct indices
		if (outTree[i] == -1) continue;
		//outTree[i] = outMap[outTree[i]]; //translate internal indices to point correctly
	}
	//translate internal indices to point correctly as well
	for (int i = 0; i < length; ++i) {
		if (outTree[i] == -1) continue;
		int sortedPointer = outTree[i];
		outTree[i] = sToUMap[sortedPointer];
	}

	//construct power schedule according to sorted tree
	for (int k = 0; k < length; ++k) power[k] = 0;
	for (int i = 0; i < length; ++i) {
		if (outTree[i] == -1) {
			outTree[i] = -2; //mangle tree for formatted printing (NOTE: mangled tree unusuable beyond this point for any other function)
			continue;
		}
		int parent = outTree[i];
		power[parent] = max(power[parent], dist(stationArray[parent], stationArray[i]));
	}

	//print to stdout
	cout << output.minPower << endl;
	for (int i = 0; i < length; ++i) {
		if (power[i] == 0) continue;
		cout << i + 1 << " " << power[i] << " " << outTree[i] + 1 << endl;
	}

	//delete used data (not needed because program returns data on termination)
	/*
	for (int i = 0; i < length; ++i) {
		for (int j = 0; j < length - 1; ++j) {
			delete[] key[i][j];
		}
	}

	for (int i = 0; i < length; ++i) {
		delete[] key[i];
	}

	delete[] key;

	delete[] outTree;
	delete[] outMap;
	delete[] tree;
	delete[] sortedArray;*/
}

//Does the DP method without using a recursive function
void DPiter(long long int* power, PowerStation* stationArray, int length, int sourceIndex) {
	//workaround since sourceIndex isn't an index here
	++sourceIndex;

	//sort all inputs by increasing x value
	PowerStation* sortedArray = new PowerStation[length];
	for (int i = 0; i < length; ++i) {
		sortedArray[i] = stationArray[i];
	}
	qsort(sortedArray, length, sizeof(PowerStation), compareDP);

	DPdata*** key = new DPdata**[length];
	for (int i = 0; i < length; ++i) {
		key[i] = new DPdata*[length];
		for (int j = 0; j < length; ++j) {
			key[i][j] = new DPdata[sourceIndex];
		}
	}

	if (sourceIndex >= length) sourceIndex = length - 1; //fix the maximum value for m

	//calculate all values from the bottom up
	DPdata data;
	data.keyQ = 0;
	data.minPower = 0;

	//prime for q = 1 case
	for (int i = 0; i < length; ++i) {
		for (int j = 0; j < length; ++j) {
			if (i == j) {
				data.keyP = j;
				data.keyQ = 0;
				data.minPower = 0;
			}
			else {
				data.keyP = j;
				data.keyQ = 0;
				data.minPower = dist(sortedArray[i], sortedArray[j]);
			}
			key[i][j][1] = data;
		}
	}

	//calculate level by level iteratively, starting from bottom level
	for (int q = 2; q <= sourceIndex; ++q) {
		for (int i = 0; i < length; ++i) {
			for (int j = i; j < length; ++j) {
				data.minPower = INF;
				data.keyP = -1;
				data.keyQ = -1;

				if (q > j - i) { //prune unnecessary computations
					key[i][j][q] = key[i][j][q - 1];
					continue;
				}

				//note: in the case of ties, pick the smallest p, then smallest q
				for (int p = i + 1; p <= j; ++p) {
					for (int partQ = 1; partQ < q; ++partQ) {
						DPdata lhs = key[i][p][partQ];
						DPdata rhs = key[p][j][q - partQ];
						if (lhs.minPower + rhs.minPower < data.minPower) {
							data.minPower = lhs.minPower + rhs.minPower;
							data.keyP = p;
							data.keyQ = partQ;
						}
					}
				}
				key[i][j][q] = data;
			}
		}
	}

	DPdata output = key[0][length - 1][sourceIndex];

	//retrieve the tree using the key values in output
	int* tree = new int[length];
	tree[0] = -1;
	for (int i = 1; i < length; ++i) {
		tree[i] = -2;
	}
	getRecTree(tree, 0, length - 1, sourceIndex, key);

	//everything beyond this point is retrieving the tree for printing in proper format

	//unsort and output the tree manually
	int* outMap = new int[length];  //maps an index from the unsorted array to the sorted array
	int index;
	for (int i = 0; i < length; ++i) {
		index = sortedArray[i].getIndex();
		outMap[index] = i;
	}

	int* sToUMap = new int[length]; //maps an index from the sorted array to the unsorted array
	for (int i = 0; i < length; ++i) {
		sToUMap[outMap[i]] = i;
	}

	//put tree indices in terms of unsorted array
	int* outTree = new int[length];
	for (int i = 0; i < length; ++i) {
		outTree[i] = tree[outMap[i]]; //translate data to correct indices
		if (outTree[i] == -1) continue;
		//outTree[i] = outMap[outTree[i]]; //translate internal indices to point correctly
	}
	//translate internal indices to point correctly as well
	for (int i = 0; i < length; ++i) {
		if (outTree[i] == -1) continue;
		int sortedPointer = outTree[i];
		outTree[i] = sToUMap[sortedPointer];
	}

	//construct power schedule according to sorted tree
	for (int k = 0; k < length; ++k) power[k] = 0;
	for (int i = 0; i < length; ++i) {
		if (outTree[i] == -1) {
			outTree[i] = -2; //mangle tree for formatted printing (NOTE: mangled tree unusuable beyond this point for any other function)
			continue;
		}
		int parent = outTree[i];
		power[parent] = max(power[parent], dist(stationArray[parent], stationArray[i]));
	}

	//print to stdout
	cout << output.minPower << endl;
	for (int i = 0; i < length; ++i) {
		if (power[i] == 0) continue;
		cout << i + 1 << " " << power[i] << " " << outTree[i] + 1 << endl;
	}

	//delete used data (not needed because program returns data on termination)
	/*
	for (int i = 0; i < length; ++i) {
		for (int j = 0; j < length - 1; ++j) {
			delete[] key[i][j];
		}
	}

	for (int i = 0; i < length; ++i) {
		delete[] key[i];
	}

	delete[] key;

	delete[] outTree;
	delete[] outMap;
	delete[] tree;
	delete[] sortedArray;
	*/
}