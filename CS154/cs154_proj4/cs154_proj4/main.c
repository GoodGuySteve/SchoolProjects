//
// main.c
//
// CS154 Spring 2015
// Project 4 -- Branch Prediction
//

#include <stdio.h>
#include <stdlib.h>

#include "functions.h"

int main(int argc, char *argv[])
{
	int i, inputSize, tableSize, type, prediction;
	int *addrArray, *resultArray;

	// Read input file
	FILE *inputFile = fopen(argv[1], "r");
	fscanf(inputFile, "%d %d %d/n", &inputSize, &tableSize, &type);
	addrArray = (int *)malloc(inputSize * sizeof(int));
	resultArray = (int *)malloc(inputSize * sizeof(int));
	for (i=0; i<inputSize; i++)
		fscanf(inputFile, "%d %d\n", &addrArray[i], &resultArray[i]);

	printf("Table size: %d\n", tableSize);
	printf("Predictor type: %s\n", type>0?"2 bit counter":"1 bit predictor");

	// initialize predictor
	void *predictor = initPredictor(tableSize, type);

	// iterate through input
	printf("\nAddress: prediction/result\n");
	for (i=0; i<inputSize; i++) {
		prediction = access(predictor, addrArray[i]);
		printf("%d: ", addrArray[i]);
		printf("%s/", prediction ? " T" : "NT");
		printf("%s -- ", resultArray[i] ? "T " : "NT");
		printf("%s\n", resultArray[i] == prediction ? "Hit" : "Miss");
		update(predictor, addrArray[i], resultArray[i]);
	}

	printf("\nAccesses: %d\n", numAccesses(predictor));
	printf("Hit rate: %2.1f%%\n", hitRate(predictor)*100);

	return 0;
}
