//
// functions.h
//
// CS154 Spring 2015
// Project 4 -- Branch Prediction
//

//
void *initPredictor(int size, int type);

//
// Predict the outcome of a branch
//
// Parameters:
//	PCOfBranch -- address of branch instruction
//
// Return:
// 	1 -- predict branch taken
// 	0 -- predict branch not taken
//
int access(void *predictor, int PCOfBranch);

//
// Inform the predictor of the result of the actual branch calculation
//
// Parameters:
//	PCOfBranch -- address of branch instruction
//	result
// 		1 --  branch was taken
// 		0 --  branch was not taken
//
void update(void *predictor, int PCOfBranch, int result);

//
// Return total number of accesses
//
int numAccesses(void *predictor);

//
// Return predictor hit rate
//
float hitRate(void *predictor);
