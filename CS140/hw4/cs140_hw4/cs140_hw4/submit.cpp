#include "innerproduct.h"

#define COARSENESS 100

double rec_cilkified(double *a, double *b, int n)
{
	double sum1 = 0;
	double sum2 = 0;
	//base case
	if (n < COARSENESS) {
		for (int i = 0; i < n; ++i) {
			sum1 += a[i] * b[i];
		}
		return sum1;
	}
	else {
		sum1 = rec_cilkified(a, b, n/2);
		sum2 = rec_cilkified(&(a[n/2]), &(b[n/2]), n - n/2);
		return sum1 + sum2;
	}
}

double loop_cilkified(double *a, double *b, int n)
{
	double retval = 0;
	double* sum = new double[n/COARSENESS];
	for (int i = 0; i < n/COARSENESS; ++i) {
		sum[i] = 0;
		for (int j = 0; j < COARSENESS; ++j) {
			sum[i] += a[i * COARSENESS + j] * b[i * COARSENESS + j];
		}
	}
	for (int i = 0; i < n/COARSENESS; ++i) retval += sum[i];
	delete[] sum;
	return retval;
}

double hyperobject_cilkified(double *a, double *b, int n)
{
	//cilk::reducer_opadd<double> sum; //this initializes to 0 (I think?)
	double sum = 0;
	double retval;
	for (int i = 0; i < n; ++i) {
		sum += a[i] * b[i];
	}
	retval = sum;
	return retval;
}

