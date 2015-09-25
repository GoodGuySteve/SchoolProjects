#include "cachesim.h"
#include <math.h>

void *createAndInitialize(int blocksize, int cachesize, int type) {

	//create and initialize basic fields
	CacheSim* cache = malloc(sizeof(CacheSim));
	cache->type = type;
	cache->blocksize = blocksize;
	cache->cachesize = cachesize;
	cache->accessTime = 0;
	cache->numHits = 0;
	cache->numMisses = 0;

	// caluclate length in bits of each 32-bit address
	int numBlocks = cachesize / blocksize;
	cache->offsetlen = 0;
	cache->indexlen = 0;
	while (numBlocks >>= 1) ++cache->indexlen;
	while (blocksize >>= 1) ++cache->offsetlen;
	cache->taglen = 32 - (cache->offsetlen + cache->indexlen);

	// allocate memory
	cache->cache = calloc(cache->cachesize / cache->blocksize, sizeof(int));
	int i;
	//initialize cache to 1 (unused tag)
	for (i = 0; i < (cache->cachesize / cache->blocksize); ++i) cache->cache[i] = 1;
	//initialize victim cache to 1 (unused tag)
	for (i = 0; i < 6; ++i) {
		cache->sideCache[i] = 1;
		cache->sideCacheUse[i] = i;
	}
	cache->bufindex = 0;

	return (void*)cache;
}
// You have a struct that contains all of the information for one cache. 
// In this function, you create the cache and initialize it, 
// returning a pointer to the struct. Because you are determining the struct, 
// you return a void * to our main. Type 0 is a direct-mapped cache. 
// Type 1 is a direct-mapped cache with a victim cache. Type 2 is a direct-mapped cache with a stream prefetcher.

int accessCache(void *cache, int address) {
	CacheSim* mycache = (CacheSim*)cache;
	
	//create masks to use and extract information from address
	int offsetmask = ~(~0 << (mycache->offsetlen));
	int indexmask = ~(~0 << (mycache->indexlen + mycache->offsetlen)) & ~offsetmask;
	int offset = address & offsetmask;
	int index = (address & indexmask) >> mycache -> offsetlen;
	int tag = address & ~(offsetmask | indexmask);

	int i, j;
	int temp, tempi;
	int targetval;
	int retval;
	
	//access current block indicated by index
	//check tag to determine hit or miss
	if (mycache->cache[index] == tag) {
		//on hit, all have same behavior - return hit and don't touch anything
		++mycache->numHits;
		++mycache->accessTime;
		return 1;
	}
	else {
		//on miss, do a case statement for each one
		switch (mycache->type) {
		case 0: 
			//for direct map, report a miss and rewrite address (fetch from DRAM)
			mycache->cache[index] = tag;
			++mycache->numMisses;
			mycache->accessTime += 100;
			return 0;
			break;
		case 1:
			//for direct + victim, check victim cache
			targetval = tag | ((index << mycache->offsetlen) & indexmask);
			for (i = 0; i < 6; ++i) {
				if (mycache->sideCache[i] == targetval) {
					//on victim hit, swap address with direct map, report victim hit
					temp = mycache->cache[index];
					mycache->cache[index] = mycache->sideCache[i] & ~(offsetmask | indexmask);
					mycache->sideCache[i] = temp | ((index << mycache->offsetlen) & indexmask);
					mycache->sideCacheUse[i] = -1;
					for (j = 0; j < 6; ++j) ++mycache->sideCacheUse[j];
					++mycache->numHits;
					mycache->accessTime += 6;
					return 1;
				}
			}
			//on victim miss:
			//evict LRU from victim 
			//evict index to the new spot in victim cache 
			//write new address to direct map (fetch from DRAM) and report miss
			tempi = 0;
			temp = mycache->sideCacheUse[0];
			for (i = 1; i < 6; ++i) {
				if (mycache->sideCacheUse[i] > temp) { //something less recently used
					temp = mycache->sideCacheUse[i];
					tempi = i;
				}
			}
			mycache->sideCache[tempi] = mycache->cache[index] | ((index << mycache->offsetlen) & indexmask);
			mycache->sideCacheUse[tempi] = -1;
			for (j = 0; j < 6; ++j) ++mycache->sideCacheUse[j];
			mycache->cache[index] = tag;
			++mycache->numMisses;
			mycache->accessTime += 100;
			return 0;
			break;
		case 2:
			// find if desired data is in the the buffer or not
				//if it is, call hit, swap into main cache
				//else, call miss and replace main cache directly from DRAM
			// whether or not hit, prefetch the next block into the next available spot in the buffer
			retval = 0;
			targetval = tag | ((index << mycache->offsetlen) & indexmask);
			for (i = 0; i < 6; ++i) {
				if (mycache->sideCache[i] == targetval) {
					//hit: swap into main cache
					mycache->cache[index] = tag;
					++mycache->numHits;
					mycache->accessTime += 6;
					retval = 1;
					break;
				}
			}
			if (retval == 0) { //miss: not found in buffer
				mycache->cache[index] = tag;
				++mycache->numMisses;
				mycache->accessTime += 100;
			}
			//prefetch next block into next available spot in buffer
			temp = mycache->cache[index] | ((index << mycache->offsetlen) & indexmask);
			mycache->sideCache[mycache->bufindex] = (((temp >> mycache->offsetlen) + 1) << mycache->offsetlen) & (~offsetmask);
			mycache->bufindex = (mycache->bufindex + 1) % 6;
			return retval;
			break;
		default:
			return -2;
			break;
		}
	}
}
//In this function, we access the cache with a particular address. 
//If the address results in a hit, return 1. If it is a miss, return 0.

int missesSoFar(void *cache) { return ((CacheSim*)cache)->numMisses; }
//This returns the number of misses that have occurred so far

int accessesSoFar(void *cache) { 
	return ((CacheSim*)cache)->numMisses + ((CacheSim*)cache)->numHits + ((CacheSim*)cache)->numHitSide;
}
//This returns the number of accesses that have occurred so far

int totalAccessTime(void *cache) { return ((CacheSim*)cache)->accessTime; }
//This returns the total number of cycles that all of the accesses 
//have taken so far.