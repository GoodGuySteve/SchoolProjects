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
int matrix_multiply_run_7(matrix_t *A, matrix_t *B, matrix_t *C)
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

/**
 * Multiply matrix A*B, store result in C.
 * Version 7: jki
 */
int matrix_multiply_run_6(matrix_t *A, matrix_t *B, matrix_t *C)
{
  int i, j, k;
  int rowBlockNum = 1, colBlockNum = 1, rowBlockSize = B->cols, colBlockSize = A->rows;
  int maxBlockSize = 64;
  if (B->cols < 64) {
	  for (j = 0; j < B->cols; j++) {
		for (k = 0; k < A->cols; k++) {
		  for (i = 0; i < A->rows; i++) {
			element(C,i,j) += element(A,i,k) * element(B,k,j);
		  }
		}
	  }
  }
  else {
  /*
	while (rowBlockSize > maxBlockSize) {
		rowBlockSize = rowBlockSize / 2;
		rowBlockNum = rowBlockNum * 2;
	}
	while (colBlockSize > maxBlockSize) {
		colBlockSize = colBlockSize / 2;
		colBlockNum = rowBlockNum * 2;
	}
	//TODO odd numbers matter too
	int row, col;
	for (col = 0; col < colBlockNum; col++){
		for (row = 0; row < rowBlockNum; row++) {
			for (j = col * colBlockSize; j < (col + 1) * colBlockSize; j++) {
				for (k = 0; k < A->cols; k++) {
					for (i = row * rowBlockSize; i < (row + 1) * rowBlockSize; i++) {
						element(C,i,j) += element(A,i,k) * element(B,k,j);
					}
				}
			}
		}
	} */
	int colStart, rowStart, colEnd, rowEnd;
	for (colStart = 0; colStart < B->cols; colStart = colStart + maxBlockSize) {
		int colEnd = colStart + maxBlockSize;
		if (B->cols < colEnd) colEnd = B->cols;
		for (rowStart = 0; rowStart < A->rows; rowStart = rowStart + maxBlockSize) {
			int rowEnd = rowStart + maxBlockSize;
			if (A->rows < rowEnd) rowEnd = A->rows;
			for (j = colStart; j < colEnd; j++) {
				for (k = 0; k < A->cols; k++) {
					for (i = rowStart; i < rowEnd; i++) {
						element(C,i,j) += element(A,i,k) * element(B,k,j);
					}
				}
			}
		}
	}
  }
  return 0;
}


