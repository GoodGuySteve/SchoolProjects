//Author 1: Steven Cabral

#include <iostream>
//#include <cilk/cilk.h>

#define ULLI unsigned long long int

//This function implements a Kronecker generator in cilk, as specified in http://www.graph500.org/specifications

//The function takes two inputs. Scale is the log of the number of vertices in the graph. 
//Edgefactor is half the average degree of each vertex. The function outputs a sparse adjacency list as a sparse matrix.


/*
ULLI** generator(int scale, int edgefactor) {

	ULLI N = 2 ^ scale; //N = number of vertices
	ULLI M = N * edgefactor; //M = number of edges

	double Aprob = 0.59;
	double Bprob = 0.19;
	double Cprob = 0.19;

}*/



