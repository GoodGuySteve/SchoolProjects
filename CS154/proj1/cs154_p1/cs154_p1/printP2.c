#include "functions.h"

// print function for project 2
void printP2(InstInfo *fetchInst, InstInfo *decodeInst, InstInfo *executeInst,
		InstInfo *memoryInst, InstInfo *writebackInst, int cycle)
{
	int i, j;

	printf("Cycle: %d:\n", cycle);

	// print pipeline stages
	printf("Fetch Instruction: ");
	printf("%s\n", fetchInst->string ? fetchInst->string : "");
	printf("Decode Instruction: ");
	printf("%s\n", decodeInst->string ? decodeInst->string : "");
	printf("Execute Instruction: ");
	printf("%s\n", executeInst->string ? executeInst->string : "");
	printf("Memory Instruction: ");
	printf("%s\n", memoryInst->string ? memoryInst->string : "");
	printf("Writeback Instruction: ");
	printf("%s\n", writebackInst->string ? writebackInst->string : "");

	// print registers
	for(i=0;i<8;i++) {
		for(j=0;j<32;j+=8)
			printf("$%d: %4d ",i+j ,regfile[i+j]);
		printf("\n");
	}
	printf("\n");
}
