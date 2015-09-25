//
// functions.h
//
// CS154 Spring 2015
// Project 4 -- Branch Prediction
//

typedef struct {
	int type;
	int state;
} Predictor;


typedef struct {
	Predictor* table;
	int size;
	int tablemask;
	int hits;
	int accesses;
} PredTable;


void *initPredictor(int size, int type) {
	int i;
	PredTable* predtable = (PredTable*)malloc(sizeof(PredTable));
	predtable->size = size;
	predtable->table = (PredTable*)malloc(sizeof(Predictor) * size);
	for (i = 0; i < size; ++i) {
		predtable->table[i].state = 0;
		predtable->table[i].type = type;
	}

	int logsize = 0;
	while (size >>= 1) ++logsize;
	predtable->tablemask = ~((~0) << logsize);

	predtable->hits = 0;
	predtable->accesses = 0;

	return (void*)predtable;
}

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
int accessElement(Predictor* pred) {
	switch (pred->type) {
	case 0: //1-bit predictor
		return pred->state;
		break;
	case 1:
	case 2: //access is same for counter and predictor
		switch (pred->state) {
		case 0:
		case 1:
			return 0;
			break;
		case 2:
		case 3:
			return 1;
			break;
		default:
			return -1;
			break;
		}
		break;
	default:
		return -1;
		break;
	}
}

int access(void *predictor, int PCOfBranch) {
	PredTable* predtable = (PredTable*)predictor;
	int index = PCOfBranch & predtable->tablemask;
	return accessElement(&predtable->table[index]);
}

//
// Inform the predictor of the result of the actual branch calculation
//
// Parameters:
//	PCOfBranch -- address of branch instruction
//	result
// 		1 --  branch was taken
// 		0 --  branch was not taken
//
void updateElement(Predictor* pred, int result, PredTable* predtable) {
	switch (pred->type) {
	case 0: //1-bit predictor
		if (result == pred->state) ++predtable->hits;
		pred->state = result;
		break;
	case 1: //2-bit counter ((tester doesn't seem to include a predictor))
		switch (pred->state) {
		case 0:
			if (result == 0) {
				++predtable->hits;
				//don't change state
			}
			else {
				++pred->state;
			}
			break;
		case 1:
			if (result == 0) {
				++predtable->hits;
				--pred->state;
			}
			else {
				++pred->state;
			}
			break;
		case 2:
			if (result == 0) {
				--pred->state;
			}
			else {
				++predtable->hits;
				++pred->state;
			}
			break;
		case 3:
			if (result == 0) {
				--pred->state;
			}
			else {
				++predtable->hits;
				//don't change state
			}
			break;
		}
		break;
	}
	++predtable->accesses;
}

void update(void *predictor, int PCOfBranch, int result) {
	PredTable* predtable = (PredTable*)predictor;
	int index = PCOfBranch & predtable->tablemask;
	updateElement(&predtable->table[index], result, predtable);
}

//
// Return total number of accesses
//
int numAccesses(void *predictor) {
	PredTable* pred = (PredTable*)predictor;
	return pred->accesses;
}

//
// Return predictor hit rate
//
float hitRate(void *predictor) {
	PredTable* pred = (PredTable*)predictor;
	float a = (float)(pred->hits);
	float b = (float)(pred->accesses);
	return a / b;
}