/* Header file for homework 4 */

#include <cilk/cilk.h>
#include <cilk/reducer_opadd.h>
#include <cmath>
#include <iostream>
#include <string>
#include <cstdio>
#include <cstdlib>
#include <numeric>
#include <ctime>
#include <algorithm>
#include <functional>

#include "example_util_gettime.h"

double rec_cilkified(double *a, double *b, int n);
double loop_cilkified(double *a, double *b, int n);
double hyperobject_cilkified(double *a, double *b, int n);

void fill_arrays(double *a, double *b, int n);
int close_enough(double x, double y);
