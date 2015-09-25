/* CS 140
 * Assignment 1 : Matrix Vector Multiplication and the Power Method  
 * */

/* This is a sample main function for the assignment provided for testing purposes.
 * You may make any you want to this file for testing purposes. The function definitions
 * are to be written in functions.c. 
 */

#include"powermethod.h"

int main(int argc, char **argv)
{
	//Input matrix and vector declaration 
	double *mat, *vec;
	double spectral_radius;

	double start,stop;

	MPI_Init(&argc,&argv);

	// Determine the size of the matrix and number of iterations from the 
	// command line arguments.
	
	int size = atoi(argv[1]);
        int iter = atoi(argv[2]);
	
	// Determine the rank of the current process.
	int myrank;
	int nprocs;

	MPI_Comm_rank(MPI_COMM_WORLD, &myrank);
	MPI_Comm_size(MPI_COMM_WORLD, &nprocs);

	
	{
		vec = (double *)malloc(sizeof(double)*size);
	}

	// Use process 0 for all the print statements.
	if( myrank == 0)
	{
		printf("\nThis program generates a matrix of dimensions %d x %d and runs the powermethod\non it for %d iterations. If you are using the input matrix from the given example,\nthe result should be 6\n\n",size,size,iter);	
	}
	
	int i;
	
	// Add the computation in a loop to get the results for 3 different matrices.	
	for(i = 0; i < 3; i++)
	{
		// Generate matrix
		mat = (double *)malloc(sizeof(double)*size*size/nprocs);
		mygeneratematrix(mat,size,i);
		//generatematrix(mat,size);
		
		//Generate a random vector
		generatevec(vec,size); 
		
		// Power Method to generate the spectral radius of the matrix.	
		
		start = MPI_Wtime();
		spectral_radius = powerMethod(mat,vec,size,iter);
		stop = MPI_Wtime();
			
		// Print the radius and execution time using thread 0.
		// You can change the print format anyway you want.
		
		if(myrank == 0)
		{
			printf("%lf %lf\n",spectral_radius,stop - start);	
		}

		free(mat);

	}


	if(myrank == 0)
		free(vec);

	MPI_Finalize();
	
}