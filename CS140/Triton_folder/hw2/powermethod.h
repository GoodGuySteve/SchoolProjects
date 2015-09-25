#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <math.h>
#include <time.h>

// Function declarations
void generatematrix(double * mat, int size);
void generatevec(double * x, int size);
double powerMethod(double * mat, double * vec, int size, int iter);
double norm2(double *x, int size);
void matVec(double *mat, double *vec, double *local_vec, int rows, int size);
