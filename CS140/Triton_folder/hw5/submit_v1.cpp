#include "life.h"
#include <time.h>
#include <fstream>

//#define GRAIN_SIZE 1 TODO change granularity here

//DEBUG
//#define nworkers 1
//#define cilk_for for
int nworkers = __cilkrts_get_nworkers();

//TODO way of optimizing:
/*
	- each processor is given a chunk of memory, including space for ghost cells
	- have 1 array for current iteration, 1 for next iteration, and 1 for the current sum (used to update next iteration)
		- can we store the sum in the next iteration array?
	- how to allocate the memory? array operator? search "data parallelization"
*/



//Generate the life matrix any way you want. We would highly recommend that you print the generated
//matrix into a file, so that you can share it with other teams for checking correctness.
void genlife(int *a, unsigned int n)
{
	srand(time(NULL));
	cilk_for(unsigned int i = 0; i < n; ++i) {
		for (unsigned int j = 0; j < n; ++j) {
			a[i*n + j] = rand() > RAND_MAX / 2 ? 0 : 1;
		}
	}
}

//Read the life matrix from a file
void readlife(int *a, unsigned int n, char *filename)
{
	//this function isn't a bottleneck, so it shouldn't need to be optimized or parallelized
		//how to parallelize reading a file?
	std::fstream inFile(filename);
	char currChar;
	inFile.get(currChar);
	int index = 0;
	a[index++] = currChar - '0';
	while (inFile.get(currChar)) {
		if (currChar < '0') continue;
		a[index++] = currChar - '0';
	}

	inFile.close();
}

//Simple print function
void printlife(int *a, unsigned int n) {
	for (unsigned int i = 0; i < n; ++i) {
		std::cout << a[n*i];
		for (unsigned int j = 1; j < n; ++j) {
			std::cout << " " << a[n*i + j];
		}
		std::cout << std::endl;
	}
}

//Life function
void life(int *a, unsigned int n, unsigned int iter, int *livecount)
{
    // You need to store the total number of livecounts for every 1/0th of the total iterations into the livecount array.
	// For example, if there are 50 iterations in your code, you need to store the livecount for iteration number 5 10 15
	// 20 ... 50. The countlive function is defined in life.cpp, which you can use. Note that you can
	// do the debugging only if the number of iterations is a multiple of 10.
	// Furthermore, you will need to wrap your counting code inside the wrapper #if DEBUG == 1 .. #endif to remove
	// it during performance evaluation.
	// For example, your code in this function could look like -
	//
	//
	//	for(each iteration)
	//      {
	//
	//		Calculate_next_life();
	//
	//		#if DEBUG == 1
	//		  if_current_iteration == debug_iteration
	//		  total_lives = countlive();
	//		  Store_into_livecount(total_lives);
	//		#ENDIF
	//
	//	}

	//check input is divisible by processors
	if (n % nworkers != 0) {
		std::cout << "Verify n is divisible by number of processors" << std::endl;
		return;
	}

	//convert malloc'ed memory into fancy pants c++ allocated memory
	int* aNext = new int[n*n];
	cilk_for(unsigned int i = 0; i < n; ++i) {
		for (unsigned int j = 0; j < n; ++j) {
			aNext[i*n + j] = a[i*n + j];
		}
	}
	free(a);
	a = new int[n*n];
	cilk_for(unsigned int i = 0; i < n; ++i) {
		for (unsigned int j = 0; j < n; ++j) {
			a[i*n + j] = aNext[i*n + j];
		}
	}


	//since each step of the loop is dependent on the previous, cannot parallelize this loop
	for (unsigned int i = 1; i < iter + 1; ++i) {

		//TODO split the memory into discrete chunks (by rows? 1 per node? include ghost cells too)

		//NOTE: here we assume work is evenly divisible among processors TODO set in input check and document
		cilk_for(int j = 0; j < (nworkers); ++j) { //for each processor

			cilk_for (unsigned int k = 0; k < n; ++k)  {//TODO is parallelizing each row a good idea?
				for (unsigned int l = 0; l < n / (nworkers); ++l) {
					//find values of all neighbors
					int sum = 0; 
					int myX = j * (n / nworkers) + l;
					int myY = k;
					sum += a[((myY + n - 1) % n) * n + (myX + n - 1) % n]; //upper left neighbor
					sum += a[((myY + n - 1) % n) * n + myX];
					sum += a[((myY + n - 1) % n) * n + (myX + 1) % n];
					sum += a[myY * n + (myX + n - 1) % n];
					sum += a[myY * n + myX]; //also add self to sum to simplify if statement
					sum += a[myY * n + (myX + 1) % n];
					sum += a[((myY + 1) % n) * n + (myX + n - 1) % n];
					sum += a[((myY + 1) % n) * n + myX];
					sum += a[((myY + 1) % n) * n + (myX + 1) % n]; //lower right neighbor

					//update next cell
					if (sum == 3 || (sum == 4 && a[myY * n + myX])) aNext[myY * n + myX] = 1;
					else aNext[myY * n + myX] = 0;
				}
			}
		}
		
		cilk_for(unsigned int j = 0; j < n; ++j) {
			for (unsigned int k = 0; k < n; ++k) {
				a[j*n + k] = aNext[j*n + k];
			}
		}

		#if DEBUG == 1
			if (i % (iter/10) == 0) {
				livecount[i / (iter/10) - 1] = countlive(a, n);
			}
		#endif
	}

	printlife(a, n);

	delete[] a;
	delete[] aNext;
	return;
}
