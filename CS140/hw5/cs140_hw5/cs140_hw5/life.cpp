// Function declarations file
#include "life.h"

// Debugging flag. Set to 0 during performance evaluation
int *livecount;

using namespace std;

int main(int argc, char **argv)
{
	unsigned int n;
	unsigned int iter;
	int *a;

	// Size calculation

	// Initialize life matrix through either reading from standard input or initializing as required.

	if(argc < 3)
	{
		printf("Usage : ./life [r] <matrix size> <number of iterations>\n");
		exit(-1);
	}

	// Read from file
	if(argv[1][0] == 'r')
	{
		n = (unsigned int)atoi(argv[3]);
		iter = (unsigned int)atoi(argv[4]);
		a = (int *)malloc(sizeof(int)*(n*n));

		if(a == NULL) {
			printf("Malloc failed .. Exiting\n");
			exit(-1);
		}

		readlife(a,n, argv[2]);
	}
	// Generate random data
	else
	{
		n = (unsigned int)atoi(argv[1]);
		iter = (unsigned int)atoi(argv[2]);
		a = (int *)malloc(sizeof(int)*(n*n));

		if(a == NULL) {
			printf("Malloc failed .. Exiting\n");
			exit(-1);
		}

		genlife(a,n);

		int live = countlive(a,n);
	}

	//Debug array
	livecount = (int *)malloc(sizeof(int)*n);

	//Initialize livecount array
	#if DEBUG == 1
		for(int i = 0; i < 10; i++)
			livecount[i] = 0;
	#endif

	life(a,n,iter,livecount);

	// Print the livecount array
	//#if DEBUG == 1

		for(int i = 0; i < 10; i++)
			printf("%d ",livecount[i]);
		printf("\n");


	//#endif

	return 0;

}

//The countlive function to be used for calculating the number of live cells.
int countlive(int *a, unsigned int n)
{
	int sum = 0;
	for (unsigned int i = 0; i < n; i++) {
		for (unsigned int j = 0; j < n; ++j) {
			sum += a[i*n + j];
		}
	}

	return sum;
}
