/**
 * check_answer.c:
 * 
 * This routine tests some random elements of the matrix
 *
 **/

#include "matrix_multiply.h"

#define NCHECKS 100

int check_answer(matrix_t *A, matrix_t *B, matrix_t *C)
{
  int i, j, k, c, nfail;
  double correct, error;
  double sqrteps = .00000001;

  nfail = 0;
  for(c = 0; c < NCHECKS; c++){
    i = (int) (drand48()*C->rows);
    j = (int) (drand48()*C->cols);
    correct = 0.0;
    for(k = 0; k < A->cols; k++){
      correct += element(A,i,k) * element(B,k,j);
    }
    error = element(C,i,j) - correct;
    if(error < 0.0) error = -error;
    if(error/correct > sqrteps){
      nfail++;
      printf("Error: C(%d,%d) = %g should have been %g\n", i, j, element(C,i,j), correct);
    }
  }
  printf("Output matrix passes %d of %d tests\n", NCHECKS-nfail, NCHECKS);
  return nfail;
}
