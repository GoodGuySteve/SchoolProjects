#ifndef CACHEFUNCTIONS_H
#define CACHEFUNCTIONS_H

typedef struct {
	int type;
	int blocksize;
	int cachesize;
	int accessTime;
	int numHits;
	int numMisses;
	int numHitSide;
	int taglen;
	int indexlen;
	int offsetlen;

	int* cache;
	int sideCacheUse[6];
	int sideCache[6];
	int bufindex;
} CacheSim;

void *createAndInitialize(int blocksize, int cachesize, int type); 
// You have a struct that contains all of the information for one cache. 
// In this function, you create the cache and initialize it, 
// returning a pointer to the struct. Because you are determining the struct, 
// you return a void * to our main. Type 0 is a direct-mapped cache. 
// Type 1 is a direct-mapped cache with a victim cache. Type 2 is a direct-mapped cache with a stream prefetcher.

int accessCache(void *cache, int address); 
//In this function, we access the cache with a particular address. 
//If the address results in a hit, return 1. If it is a miss, return 0.

int missesSoFar(void *cache); 
//This returns the number of misses that have occurred so far

int accessesSoFar(void *cache); 
//This returns the number of accesses that have occurred so far

int totalAccessTime(void *cache); 
//This returns the total number of cycles that all of the accesses 
//have taken so far.


// If you want to declare structs or extra helper functions, do so at the 
// top of your cachefunctions.c.  Do not add anything to this file.


#endif

