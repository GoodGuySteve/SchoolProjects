#include<stdio.h>
#include<mpi.h>
#include<stdlib.h>

#define INPUT_BODY   "p=( %lf , %lf , %lf ) v=( %lf , %lf , %lf ) m= %lf "
#define OUTPUT_BODY  "p=(% 1.4e,% 1.4e,% 1.4e) v=(% 1.4e,% 1.4e,% 1.4e) m=% 1.4e\n"

void readnbody(double** s, double** v, double* m, int n);

void gennbody(double** s, double** v, double* m, int n);

void nbody(double** s, double** v, double* m, int n, int iter, int timestep);
