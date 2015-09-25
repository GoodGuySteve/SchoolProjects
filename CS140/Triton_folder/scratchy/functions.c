/* CS 140
 * Assignment 1 : Matrix Vector Multiplication and the Power Method 
 * Group members : Steven Cabral , Liam Cardenas
 * */

/* This file provides the placeholder function definitions, where you will be
 * implementing the assignment algorithms. You will be required to turn in 
 * only this file during the submission, where it will be compiled together
 * with our main function and tested. It is therefore required that you keep the 
 * function declaration formats unchanged.
 */

#include "powermethod.h"

// Subroutine for generating the input matrix
void generatematrix(double * mat, int size)
{
	int i;
	int myrank,nprocs;

	MPI_Comm_rank(MPI_COMM_WORLD, &myrank);
	MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
	
	int arraySize = size*size / nprocs;
	int rowNum, colNum;

	for (i = 0; i < arraySize; ++i) {
		//Rows and columns are indexed from 1 to size
		rowNum = size / nprocs * myrank + 1 + i / size;
		colNum = i % size + 1;
		if (rowNum >= colNum) mat[i] = rowNum;
		else mat[i] = 0;
	}	
}

// Subroutine to generate a random vector
void generatevec(double * x,int size)
{
	int i;
	for (i = 0; i < size; ++i) {
		x[i] = 1;
	}
}

// Subroutine for the power method, to return the spectral radius
double powerMethod(double * mat, double * x, int size, int iter)
{
	int i, j;
	int nprocs, myrank;
	MPI_Comm_rank(MPI_COMM_WORLD, &myrank);
	MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
	double * local_x = (double *)malloc(size/nprocs*sizeof(double));		
	/*for (i = 0; i < iter; ++i) {
		//printf("initializing iteration %d\n", i);
		double normVal = norm2(x, size);
		//printf("norm2 gives value %f on iteration%d\n", normVal, i);
		for (j = 0; j < size; ++j) {
			x[j] = x[j] / normVal;
		}
		if(myrank == 0) {
			fprintf(stdout, "iteration %d: performing scatter\n", i);
		}
		MPI_Bcast(x, size, MPI_DOUBLE, 0, MPI_COMM_WORLD);

		matVec(mat, x, local_x, size/nprocs, size);
		if(myrank == 0) {
			fprintf(stdout, "process %d gives the following vector on iteration %d:\n",myrank, i);
		}
		for (j = 0; j < size/nprocs; ++j) {
			printf("%f\n", local_x[j]);
			//x[j] = local_x[j];
		}
		if(myrank == 0) {
			fprintf(stdout, "iteration %d: performing gather\n",i);
		}
		
		MPI_Gather(local_x, size/nprocs, MPI_DOUBLE, x, size, MPI_DOUBLE, 0, MPI_COMM_WORLD);
		if(myrank == 0) {
			fprintf(stdout, "iteration %d: vector gathered from all nodes:\n", i);
			for (j = 0; j < size; ++j) {
				printf("%f\n", x[j]);
			}
		}
	}
	free(local_x);
	return norm2(x, size);
	*/
	local_x[0] = myrank;
	printf("Proccessor %d reporting in\n", myrank);
	MPI_Gather(local_x, 1, MPI_DOUBLE, x, 1, MPI_DOUBLE, 0, MPI_COMM_WORLD);
	for (i = 0; i < nprocs; ++i)
		if(myrank == 0) 
			printf("Member %d of proccess %d is %f\n",i,myrank,x[i]);
	double* data, *dataTotal;
	int count = 5;
	data = (double *)malloc(count*sizeof(double));
	if(myrank == 0) dataTotal = (double *)malloc(count*nprocs*sizeof(double));

	for(i=0; i < count; ++i) {
		data[i] = myrank*(i+1); 
	}
	MPI_Gather(data, 5, MPI_DOUBLE, dataTotal, 5, MPI_DOUBLE, 0, MPI_COMM_WORLD);
	if(myrank == 0) {
		printf("Total vector assembled: (should be length %d)\n", count*nprocs);
		for(i=0; i < count*nprocs; ++i) {
			printf("%f\n", dataTotal[i]);
		}
	}

	return -0.01;
}

// Subroutine to return the normal of a vector
double norm2(double * x, int size)
{
	int i = 0;
	double sum = 0;
	for (i = 0; i < size; ++i) sum += (x[i]*x[i]);
	return sqrt(sum); 
}

// Subroutine to multiply a matrix by a vector
void matVec(double *mat, double *vec, double *local_vec, int rows, int size)
{
        int i, rowIndex;
	for (i = 0; i < rows; ++i) {
		local_vec[i] = 0;
	}
	for (rowIndex = 0; rowIndex < rows; rowIndex++) {
        	for(i = 0; i < size; ++i) {
                	local_vec[rowIndex] += mat[rows*rowIndex + i] * vec[i];
		}
	}
}

//Debug function to display the contents of a buffer
void displayBuf(double *buf, int size) {
	int i;
	for (i = 0; i < size; ++i) {
		printf("%d\n", buf[i]);
	}
}
