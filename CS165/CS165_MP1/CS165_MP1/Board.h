#ifndef _BOARD_H
	#define _BOARD_H

#include <exception>
#include <cstdlib>
#include <cmath>

class InvalidSwap : public std::exception {};

//TODO describe classes here

struct Queen {
	Queen();
	Queen(int x, int y);
	~Queen();
	int row;
	int col;
	int getSum();
	int getDif();
	int direction;
};

struct Board {
	Board(int n);
	Board(const Board& other);
	Board& operator=(const Board& other);
	~Board();

	int n;
	int stepCount;
	int numQueens;
	int numConflicts;

	int* sumConflicts;
	int* difConflicts;

	Queen* queenArray;

	void addQueen(int x, int y);
	int swapQueens(int first, int second);
	bool movePiece(int y, int dir);
	int getConflictTotal() const;
	double getHCost() const;
};
#endif
