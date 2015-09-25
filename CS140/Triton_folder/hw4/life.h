//#include<cilk/cilk.h>
#include<iostream>
#include<string.h>
#include<stdio.h>
#include<stdlib.h>
#include<numeric>

void readlife(int *a,unsigned int n, char *filename);
void genlife(int *a,unsigned int n);
void life(int *a,unsigned int n, unsigned int iter, int *livecount);
int countlive(int *a, unsigned int n);
