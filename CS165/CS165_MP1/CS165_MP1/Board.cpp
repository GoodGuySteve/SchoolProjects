#ifndef _BOARD_CPP
	#define _BOARD_CPP

#include "Board.h"

Queen::Queen() : direction(0) {
}

Queen::Queen(int x, int y) : row(x), col(y) {
}
Queen::~Queen() {
}

int Queen::getDif() { return row - col; }
int Queen::getSum() { return row + col; }


Board::Board(int n) : n(n), stepCount(0),
numQueens(0), numConflicts(0) {
	queenArray = new Queen [n];
	sumConflicts = new int[2 * n];
	difConflicts = new int[2 * n];
	for (int i = 0; i < 2 * n; ++i) {
		sumConflicts[i] = 0;
		difConflicts[i] = 0;
	}
}

Board::Board(const Board& other) : n(other.n), 
stepCount(other.stepCount), numQueens(other.numQueens){

	queenArray = new Queen[n];
	sumConflicts = new int[2 * n];
	difConflicts = new int[2 * n];
	for (int i = 0; i < n; ++i) {
		queenArray[i] = other.queenArray[i];
	}
	for (int i = 0; i < 2 * n; ++i) {
		sumConflicts[i] = other.sumConflicts[i];
		difConflicts[i] = other.difConflicts[i];
	}
}

Board::~Board() {
	delete[] difConflicts;
	delete[] sumConflicts;
	delete[] queenArray;
}

Board & Board::operator=(const Board& other) {
	n = other.n;
	stepCount = other.stepCount;
	numQueens = other.numQueens;

	queenArray = new Queen[n];
	sumConflicts = new int[2 * n];
	difConflicts = new int[2 * n];
	for (int i = 0; i < n; ++i) {
		queenArray[i] = other.queenArray[i];
	}
	for (int i = 0; i < 2 * n; ++i) {
		sumConflicts[i] = other.sumConflicts[i];
		difConflicts[i] = other.difConflicts[i];
	}
	return *this;
}

//adds a queen to the board - queens cannot share columns
void Board::addQueen(int x, int y) { //make sure row numbers are from 0 to n-1
	//DEBUG
	if (x < 0 || x >= n || y < 0 || y >= n) throw 1;

	queenArray[y].row = x;
	queenArray[y].col = y;
	sumConflicts[x + y] += 1;
	difConflicts[x - y + (n - 1)] += 1;
	++numQueens;
}


//swaps the row position of two queens and returns the step cost
//throws InvalidSwap exception when the swap would cause backtracking
int Board::swapQueens(int first, int second) {
	int cost;
	int temp;

	Queen& firstq = queenArray[first];
	Queen& secondq = queenArray[second];

	//first make sure that the swap does not cause back-tracking for either queen
	int dir; //direction the first entry will have to swap
	dir = (firstq.row > secondq.row) ? -1 : 1; 
	if (dir == -firstq.direction || dir == secondq.direction) {
		throw "InvalidSwap"; //TODO make actual execption?
	}
	firstq.direction = dir;
	secondq.direction = -dir;

	sumConflicts[firstq.getSum()] -= 1;
	sumConflicts[secondq.getSum()] -= 1;
	difConflicts[firstq.getDif() + (n-1)] -= 1;
	difConflicts[secondq.getDif() + (n-1)] -= 1;

	cost = abs(firstq.row - secondq.row) * 2;
	temp = secondq.row;
	secondq.row = firstq.row;
	firstq.row = temp;

	sumConflicts[firstq.getSum()] += 1;
	sumConflicts[secondq.getSum()] += 1;
	difConflicts[firstq.getDif() + (n - 1)] += 1;
	difConflicts[secondq.getDif() + (n - 1)] += 1;

	return cost;

}

bool Board::movePiece(int y, int dir) {
	if (-dir == queenArray[y].direction) return false;
	if (queenArray[y].row + dir >= n || queenArray[y].row + dir < 0) return false;
	queenArray[y].direction = dir;
	queenArray[y].row += dir;
	return true;
}

int Board::getConflictTotal() const {
	int sum = 0;
	for (int i = 0; i < 2 * n; ++i) {
		if (sumConflicts[i] > 0) sum += sumConflicts[i] - 1;
		if (difConflicts[i] > 0) sum += difConflicts[i] - 1;
	}
	return sum;
}
#endif
