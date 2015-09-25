#pragma once

// GRANULARITY is the maximum number of documents each branch of the 
// tree will contain
#define GRANULARITY 40

// Tree won't consider splitting on a word if its frequency is below FREQ_CUTOFF
//NOTE: this is the value for training sets of size 2000
#define FREQ_CUTOFF 50.0

// Maximum frequency allowed before word gets pruned as a stop word
// NOTE: this is the value for training sets of size 2000
#define FREQ_MAX 700.0

// TF_NORM_B is the document length corrective factor (notated as "b" in the 
// slides describing term frequency normalization)
#define TF_NORM_B 0.75

// TF_NORM_K is the is the other parameter "k" in the Okapi_BM25 term frequency
// correction
#define TF_NORM_K 1.8

//Branching factor for the tree function - used to tune speedup
#define BFAC 10