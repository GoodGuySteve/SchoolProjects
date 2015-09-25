#include <stdio.h>
#include "cachesim.h"

void printHit(int a) {
	if (a == 1) printf("Hit!\n");
	else if (a == 0) printf("Miss!\n");
	else printf("Failure!\n");
}

int main(int argc, char** argv) {

	void* mycache = createAndInitialize(16, 64, 2);
	printHit(accessCache(mycache, 0x0));
	printHit(accessCache(mycache, 0x0));
	printHit(accessCache(mycache, 0x0));
	printHit(accessCache(mycache, 0x80000000));
	printHit(accessCache(mycache, 0x0));
	printHit(accessCache(mycache, 0x80000000));
	printHit(accessCache(mycache, 0x00000000));
	printHit(accessCache(mycache, 0x00000010));
	printHit(accessCache(mycache, 0x00000020));
	printHit(accessCache(mycache, 0x00000070));

	return 0;
}