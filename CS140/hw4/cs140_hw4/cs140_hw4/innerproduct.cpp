/*
Homework 4 : Cilkified Inner Products.
Team member 1:
Team member 2:
*/

#include "innerproduct.h"

using namespace std;

// String to display results
char result[][15] = {"did not match", "matched"};

int main(int argc, char **argv) {
  int n;
  long t1, t2;

  // Size calculation
  if (argc == 2) {
    n = atoi(argv[1]);
  } else {
    n = 1000000;
  }
	
  // Array allocation
  double *a = new double[n];
  double *b = new double[n];

  double ref_result, rec_result, loop_result, reducer_result; 
	
  // Initialize arrays
  srand(time(NULL));
  fill_arrays(a, b, n);

  // Compute inner product using library function
  t1 = example_get_time();
  ref_result = std::inner_product(a, a + n, b, 0.0);
  t2 = example_get_time();
  cout << "Standard library function time: " << t2 - t1 << endl;
	
  // Compute inner product for each method
  t1 = example_get_time();
  rec_result = rec_cilkified(a, b, n);
  t2 = example_get_time();
  cout << "Recursive time:                 " << t2 - t1 << endl;

  t1 = example_get_time();
  loop_result = loop_cilkified(a, b, n);
  t2 = example_get_time();
  cout << "Loop time:                      " << t2 - t1 << endl;

  t1 = example_get_time();
  reducer_result = hyperobject_cilkified(a, b, n);
  t2 = example_get_time();
  cout << "Reducer time:                   " << t2 - t1 << endl;

  cout << "Result from standard library function: " << ref_result << endl;
  cout << "Recursive result:                      " << result[close_enough(rec_result, ref_result)] << endl;
  cout << "Loop result:                           " << result[close_enough(loop_result, ref_result)] << endl;
  cout << "Reducer result:                        " << result[close_enough(reducer_result, ref_result)] << endl;

  delete[] a;
  delete[] b;

  return 0;
}

void fill_arrays(double *a, double *b, int n) {
  for (int i = 0 ; i < n; i++) {
    a[i] = (double) rand() / sqrt(n / 3.0);
    b[i] = (double) rand() / sqrt(n / 3.0);
  }
}

int close_enough(double x, double y) {
  if (x == y)
    return 1;
  if (fabs(x - y) / (fabs(x) + fabs(y)) < 1.0e-6)
    return 1;
  return 0;
}
