/**
 * testbed.c:
 * 
 * This file runs your code, timing its execution and printing out the result.
 *
 **/

#include "matrix_multiply.h"

int print_help()
{
  printf("Usage: ./matrix_multiply -n<matrix_dimension> -a<algorithm> [-p]\n");
  return 0;
}

int main(int argc, char **argv)
{
  char optchar;
  char algopt = '0';
  int should_print = 0;
  int i, j;
  int Anr = 4;
  int Anc = 5;
  int Bnc = 3;
  matrix_t *A;
  matrix_t *B;
  matrix_t *C;
  double time1, time2, elapsed, flops;

  if(argc == 1){
    print_help();
    return 0;
  }
  opterr = 0;
  while ((optchar = getopt(argc, argv, "hpn:a:")) != -1) {
    switch (optchar) {
      case 'h':
        print_help();
        return 0;
      case 'p':
        should_print = 1;
        break;
      case 'n':
        Anr = Anc = Bnc = atoi(optarg);
        break;
      case 'a':
        algopt = (char)*optarg;
        break;
      default:
        print_help();
        return 0;
    }
  }
  printf("Making matrices\n");
  A = make_matrix(Anr, Anc);
  B = make_matrix(Anc, Bnc);
  C = make_matrix(Anr, Bnc);
  printf("A is %d-by-%d, B is %d-by-%d, C is %d-by-%d\n", A->rows, A->cols, B->rows, B->cols, C->rows, C->cols);
  for (i = 0; i < A->rows; i++) {
    for (j = 0; j < A->cols; j++) {
      element(A,i,j) = A->rows * i + j + 1;
    }
  }
  for (i = 0; i < B->rows; i++) {
    for (j = 0; j < B->cols; j++) {
      element(B,i,j) = B->rows * i + j + 1;
    }
  }

  if (should_print) {
    printf("Matrix A: \n");
    print_matrix(A);
  }
  if (should_print) {
    printf("Matrix B: \n");
    print_matrix(B);
  }

  switch (algopt){
    case '1':
      printf("Using matrix_multiply_run_1...\n");
      time1 = elapsed_seconds();
      matrix_multiply_run_1(A, B, C);
      time2 = elapsed_seconds();
      break;
    case '2':
      printf("Using matrix_multiply_run_2...\n");
      time1 = elapsed_seconds();
      matrix_multiply_run_2(A, B, C);
      time2 = elapsed_seconds();
      break;
    case '3':
      printf("Using matrix_multiply_run_3...\n");
      time1 = elapsed_seconds();
      matrix_multiply_run_3(A, B, C);
      time2 = elapsed_seconds();
      break;
    case '4':
      printf("Using matrix_multiply_run_4...\n");
      time1 = elapsed_seconds();
      matrix_multiply_run_4(A, B, C);
      time2 = elapsed_seconds();
      break;
    case '5':
      printf("Using matrix_multiply_run_5...\n");
      time1 = elapsed_seconds();
      matrix_multiply_run_5(A, B, C);
      time2 = elapsed_seconds();
      break;
    case '6':
      printf("Using matrix_multiply_run_6...\n");
      time1 = elapsed_seconds();
      matrix_multiply_run_6(A, B, C);
      time2 = elapsed_seconds();
      break;
    default:
      printf("Sorry, unrecognized algorithm option: %c\n", algopt);
      exit(1);
  }
  
  elapsed = time2 - time1;
  flops = 2.0 * A->rows * A->cols * B->cols / elapsed;

  /** WARNING! DO NOT CHANGE PRINT STATEMENTS BELOW THIS LINE! **/
  if (should_print) {
    printf("---- RESULTS ----\n");
    printf("Product: \n");
    print_matrix(C);
    printf("---- END RESULTS ----\n");
  }
  if(check_answer(A, B, C) > 0){
    exit(1);
  }
  printf("Time = %f sec, rate = %g flops/sec\n", elapsed, flops);
  return 0;
}
