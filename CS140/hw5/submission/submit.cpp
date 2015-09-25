//Author 1: Steven Cabral 6801179
//Author 2: Liam Cardenas

#include "life.h"
#include <time.h>
#include <fstream>

//#define GRAIN_SIZE 1 TODO change granularity here
#define GHOST_BORDER 1 //number of extra ghost cell rows added on each side of chunks

//DEBUG
//#define nworkers 2
//#define cilk_for for
int nworkers = __cilkrts_get_nworkers();

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

	//check input is divisible by processors
	if (n % nworkers != 0) {
		std::cout << "Verify n is divisible by number of processors" << std::endl;
		return;
	}

	int** aCurr = new int*[nworkers];
	int** aNext = new int*[nworkers];

	cilk_for(int j = 0; j < (nworkers); ++j) {
		//allocate the memory into discrete chunks (including ghost cells too)
		aCurr[j] = new int[2 * n * GHOST_BORDER + n * (n / nworkers)];
		aNext[j] = new int[n * (n / nworkers)];
	}
	
	//prime the values
	cilk_for(int j = 0; j < (nworkers); ++j) {
		for (unsigned int l = 0; l < n / (nworkers); ++l) { //row number
			for (unsigned int k = 0; k < n; ++k) { //column number
				aNext[j][n*l + k] = a[j*n*(n / (nworkers)) + n*l + k];
			}
		}
	}
	
	//NOTE: here we assume work is evenly divisible among processors TODO document this

	//since each step of the loop is dependent on the previous, cannot parallelize this loop
	for (unsigned int i = 1; i < iter + 1; ++i) {

		//initial values for aCurr at start of iteration
		cilk_for(int j = 0; j < (nworkers); ++j) {
			for (int l = 0; l < GHOST_BORDER; ++l) { //fill ghost cells from outer rows to inner rows (relative to block j)
				for (unsigned int k = 0; k < n; ++k) {
					aCurr[j][l*n + k] = aNext[(j + (nworkers)-1) % (nworkers)][n*((n / nworkers) - GHOST_BORDER) + n*l + k]; //lower ghost cells
					aCurr[j][2 * n * GHOST_BORDER + n * (n / nworkers) - l*n - n + k] = aNext[(j + 1) % (nworkers)][n*l + k]; //upper ghost cells
				}
			}
			for (unsigned int l = 0; l < n / (nworkers); ++l) { //fill remaining cells from previous iteration
				for (unsigned int k = 0; k < n; ++k) {
					aCurr[j][(l + GHOST_BORDER)*n + k] = aNext[j][n*l + k];
				}
			}
		}

		cilk_for(int j = 0; j < (nworkers); ++j) { //for each processor
			for (unsigned int l = 0; l < n / (nworkers); ++l) { //for each row (within the processor span)
				for (unsigned int k = 0; k < n; ++k)  {//for each column	
					//find values of all neighbors
					int sum = 0; 
					int myX = k;
					int myY = l + GHOST_BORDER;
					sum += aCurr[j][n*(myY - 1) + (myX + n - 1) % n]; //upper left neighbor
					sum += aCurr[j][n*(myY - 1) + myX];
					sum += aCurr[j][n*(myY - 1) + (myX + 1) % n];
					sum += aCurr[j][n*(myY) + (myX + n - 1) % n];
					sum += aCurr[j][n*(myY) + myX]; //add self to sum to simplify conditional
					sum += aCurr[j][n*(myY) + (myX + 1) % n];
					sum += aCurr[j][n*(myY + 1) + (myX + n - 1) % n];
					sum += aCurr[j][n*(myY + 1) + myX];
					sum += aCurr[j][n*(myY + 1) + (myX + 1) % n]; //lower right neighbor

					//update next cell
					if (sum == 3 || (sum == 4 && aCurr[j][n*(myY)+myX])) aNext[j][l * n + k] = 1;
					else aNext[j][l * n + k] = 0;
				}
			}
		}
		

		#if DEBUG == 1
		if (i % (iter/10) == 0) {
			//dump vales back into original array
			for (int j = 0; j < (nworkers); ++j) {
				for (unsigned int l = 0; l < n / (nworkers); ++l) {
					for (unsigned int k = 0; k < n; ++k) {
						a[j*n*(n / (nworkers)) + n*l + k] = aNext[j][l*n + k];
					}
				}
			}
			livecount[i / (iter/10) - 1] = countlive(a, n);
		}
		#endif
	}

	//fill a at the end
	cilk_for(int j = 0; j < (nworkers); ++j) {
		for (unsigned int l = 0; l < n / (nworkers); ++l) {
			for (unsigned int k = 0; k < n; ++k) {
				a[j*n*(n / (nworkers)) + n*l + k] = aNext[j][l*n + k];
			}
		}
	}

	//printlife(a, n);

	delete[] aNext;
	return;


}
