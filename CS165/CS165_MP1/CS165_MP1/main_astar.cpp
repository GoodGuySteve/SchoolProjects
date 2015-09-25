#pragma once

#include <iostream>
#include <cstdlib>
#include <list>
#include <queue>
#include <fstream>
#include <sstream>
#include <ctime>
#include "Board.h"

#define MAX_SET_SIZE 10000

using namespace std;

class comparison {
public:
	comparison(){}
	~comparison(){}
	//return the greater step cost of 2 boards (so that priority queue puts lowest cost at front)
	bool operator() (const Board* lhs, const Board* rhs) const {
		return (lhs->getHCost() > rhs->getHCost());
	}
};

int uniqueRow(Board* board, int n, int curRow, priority_queue<Board*, vector<Board*>, comparison>* frontierSet, int maxSteps);
int readFromStdin(Board** board);

int main(int argc, char* argv[]) {

	time_t start = time(nullptr);
	Board* board = nullptr;
	int maxSteps = readFromStdin(&board);
	int n = board->n;
	Board bestBoard(*board);

	priority_queue<Board*, vector<Board*>, comparison> frontierSet;
	frontierSet.push(board);
	while (frontierSet.size() > 0) {
		board = frontierSet.top();
		frontierSet.pop();
		if (board->getConflictTotal() < bestBoard.getConflictTotal()) {
			bestBoard = (*board);
		}
		if (bestBoard.getConflictTotal() == 0) break;
		if (board->stepCount >= maxSteps) {
			delete board;
			continue;
		}
		Board* addBoard = new Board(*board);
		for (int i = 0; i < n; ++i) {
			if (addBoard->movePiece(i, -1)) {
				frontierSet.push(addBoard);
				addBoard = new Board(*board);
			}
			if (addBoard->movePiece(i, 1)) {
				frontierSet.push(addBoard);
				addBoard = new Board(*board);
			}
		}
		delete board;
	}

	//exit and write to output
	time_t end = time(nullptr);
	cout << bestBoard.getConflictTotal() << " conflicts" << endl;
	for (int i = 0; i < n; ++i) {
		cout << bestBoard.queenArray[i].row << " " << bestBoard.queenArray[i].col << endl;
	}
	cout << end - start << " seconds" << endl;
	return 0;
}


//function to read in from stdin and create a Board from the data (stored in board)
//returns the maximum number of steps allowed
int readFromStdin(Board** board) {

	int lineNum = 0;
	int n;
	int maxSteps;
	int x, y;
	string stdline;
	if (!cin.eof()) {
		lineNum++;

		getline(cin, stdline);
		istringstream curLine(stdline);
		if (!(curLine >> n)) {
			cout << "Incorrect entry on line " << lineNum << " of input - expecting integer." << endl;
			throw 1;
		}
	}
	(*board) = new Board(n);
	if (!cin.eof()) {
		++lineNum;
		getline(cin, stdline);
		istringstream curLine(stdline);
		if (!(curLine >> maxSteps)) {
			cout << "Incorrect entry on line " << lineNum << " of input - expecting integer." << endl;
			throw 1;
		}
	}
	for (int i = 0; i < n; ++i) {
		if (cin.eof()) throw 1;

		lineNum++;
		getline(cin, stdline);
		istringstream curLine(stdline);

		if (!(curLine >> x)) {
			cout << "Incorrect entry on line " << lineNum << " of input.txt - expecting integer." << endl;
			throw 1;
		}
		if (!(curLine >> y)) {
			cout << "Incorrect entry on line " << lineNum << " of input.txt - expecting integer." << endl;
			throw 1;
		}
		(*board)->addQueen(x - 1, y - 1); //program indexes rows from 0 to n-1
	}
	return maxSteps;
}
