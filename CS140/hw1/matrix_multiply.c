#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <math.h>
#include <assert.h>

#include "matrix_multiply.h"

/*
 * Allocates a rows-by-cols matrix and returns it
 */
matrix_t *make_matrix(int rows, int cols)
{
  matrix_t *new_matrix = malloc(sizeof(matrix_t));
  new_matrix->rows = rows;
  new_matrix->cols = cols;
  new_matrix->colstride = rows;
  new_matrix->values = (double *) malloc(sizeof(double) * rows * cols);
  return new_matrix; 
}

/*
 * Frees an allocated matrix (not a submatrix)
 */
void free_matrix(matrix_t *m)
{
  free(m->values);
  free(m);
}

/*
 * Print Matrix
 */
void print_matrix(matrix_t *m)
{
  int i, j;
  printf("------------\n");
  for (i = 0; i < m->rows; i++) {
    for (j = 0; j < m->cols; j++) {
      printf("  %g  ", element(m,i,j));
    }
    printf("\n");
  }
  printf("------------\n");
}

/**
 * Multiply matrix A*B, store result in C.
 * Version 1: Vanilla ijk nested loops.
 * This can be easily modified for other index orders: ikj, jik, jki, kij, kji.
 */
int matrix_multiply_run_1(matrix_t *A, matrix_t *B, matrix_t *C)
{
  int i, j, k;
  for (i = 0; i < A->rows; i++) {
    for (j = 0; j < B->cols; j++) {
      for (k = 0; k < A->cols; k++) {
        element(C,i,j) += element(A,i,k) * element(B,k,j);
      }
    }
  }
  return 0;
}

/**
 * Multiply matrix A*B, store result in C.
 * Version 2: ikj
 */
int matrix_multiply_run_2(matrix_t *A, matrix_t *B, matrix_t *C)
{
  int i, j, k;
  for (i = 0; i < A->rows; i++) {
    for (k = 0; k < A->cols; k++) {
      for (j = 0; j < B->cols; j++) {
        element(C,i,j) += element(A,i,k) * element(B,k,j);
      }
    }
  }
  return 0;
}

/**
 * Multiply matrix A*B, store result in C.
 * Version 3: jik
 */
int matrix_multiply_run_3(matrix_t *A, matrix_t *B, matrix_t *C)
{
  int i, j, k;
  for (j = 0; j < B->cols; j++) {
    for (i = 0; i < A->rows; i++) {
      for (k = 0; k < A->cols; k++) {
        element(C,i,j) += element(A,i,k) * element(B,k,j);
      }
    }
  }
  return 0;
}

/**
 * Multiply matrix A*B, store result in C.
 * Version 4: jki
 */
int matrix_multiply_run_4(matrix_t *A, matrix_t *B, matrix_t *C)
{
  int i, j, k;
  for (j = 0; j < B->cols; j++) {
    for (k = 0; k < A->cols; k++) {
      for (i = 0; i < A->rows; i++) {
        element(C,i,j) += element(A,i,k) * element(B,k,j);
      }
    }
  }
  return 0;
}

/**
 * Multiply matrix A*B, store result in C.
 * Version 5: kij
 */
int matrix_multiply_run_5(matrix_t *A, matrix_t *B, matrix_t *C)
{
  int i, j, k;
  for (k = 0; k < A->cols; k++) {
    for (i = 0; i < A->rows; i++) {
      for (j = 0; j < B->cols; j++) {
        element(C,i,j) += element(A,i,k) * element(B,k,j);
      }
    }
  }
  return 0;
}

/**
 * Multiply matrix A*B, store result in C.
 * Version 6: kji
 */
int matrix_multiply_run_6(matrix_t *A, matrix_t *B, matrix_t *C)
{
  int i, j, k;
    for (k = 0; k < A->cols; k++) {
      for (j = 0; j < B->cols; j++) {
 	for (i = 0; i < A->rows; i++) {
          element(C,i,j) += element(A,i,k) * element(B,k,j);
      }
    }
  }
  return 0;
}

