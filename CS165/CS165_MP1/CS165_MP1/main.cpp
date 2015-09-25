#ifndef _MAIN_CPP
	#define _MAIN_CPP

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

//used with priority queue
class comparison {
public:
	comparison(){}
	~comparison(){}
	//return the greater step cost of 2 boards (so that priority queue puts lowest cost at front)
	bool operator() (const Board* lhs, const Board* rhs) const {
		if (lhs->stepCount > rhs->stepCount) return true;
		else if (lhs->stepCount < rhs->stepCount) return false;
		else {
			if (lhs->getConflictTotal() > rhs->getConflictTotal()) return true;
			else return false;
		}
	}
};

int uniqueRow(Board* board, int n, int curRow, priority_queue<Board*, vector<Board*>, comparison>* frontierSet, int maxSteps);
int readFromStdin(Board** board);

int main(int argc, char* argv[]) {

	time_t start = time(nullptr);
	Board* board = nullptr;
	int maxSteps = readFromStdin(&board);
	int n = board->n;
	Board bestBoard(n);

	//no searched set needed
	priority_queue<Board*, vector<Board*>, comparison> frontierSet;

	//First step: enqueue all unique-row boards that can be reached in minimum steps
	int test = uniqueRow(board, n, 0, &frontierSet, maxSteps);

	if (test != frontierSet.size()) throw 4; //DEBUG
	if (test == 0) {
		cout << "No Solution" << endl;
		return -1;
	}

	//Second step: test all swaps for each of those boards, exploring swaps which minimize conflicts first
	board = frontierSet.top();
	frontierSet.pop();
	bestBoard = *board;
	while (bestBoard.getConflictTotal() > 0) {
		if (time(nullptr) - start > 1500) break; //quit after 25 minutes
		if (board->stepCount < maxSteps) {
			//if board isn't a solution, add all potential paths
			for (int i = 0; i < n; ++i) {
				for (int j = i + 1; j < n; ++j) {
					Board boardCopy(*board);
					try {
						int steps = board->stepCount;
						steps += board->swapQueens(i, j);
						//only adds new entry if swap successful
						frontierSet.push(board);
						board = new Board(boardCopy);
					}
					catch (...) {}				
				}
			}
		}
		delete board;
		//then check whether the next board is better than the previous
		board = frontierSet.top();
		frontierSet.pop();
		if (board->getConflictTotal() < bestBoard.getConflictTotal()) bestBoard = *board;
	}
	//exit and write to output
	time_t end = time(nullptr);
	cout << bestBoard.getConflictTotal() << " conflicts" << endl;
	for (int i = 0; i < n; ++i) {
		cout << bestBoard.queenArray[i].row + 1 << " " << bestBoard.queenArray[i].col + 1 << endl;
	}
	cout << end - start << " seconds" << endl;
	return 0;
}

//recursive algorithm used on a board to put every queen into a unique row
//effectively utilizes depth-limited DFS with a strict cutoff on maximum states
int uniqueRow(Board* board, int n, int curRow, priority_queue<Board*, vector<Board*>, comparison>* frontierSet, int maxSteps) {
	
	if (frontierSet->size() > MAX_SET_SIZE) {
		delete board;
		return 0;
	}

	//construct list of all queens in the row
	list<Queen> rowList;
	int retval;
	int queenInd;
	for (int col = 0; col < n; ++col) {
		if (board->queenArray[col].row == curRow) {
			rowList.emplace_back(board->queenArray[col]);
		}
	}

	if (rowList.size() == 1) {
		//end condition - when each board is fully moved
		if (curRow == n - 1) { 
			//make sure other board parameters are consistent as well (e.g. num collisions)
			for (int i = 0; i < 2 * n; ++i) {
				board->sumConflicts[i] = 0;
				board->difConflicts[i] = 0;
			}
			for (int i = 0; i < n; ++i) {
				++board->sumConflicts[board->queenArray[i].getSum()];
				++board->difConflicts[board->queenArray[i].getDif() + (n - 1)];
			}
			if (board->stepCount > maxSteps) {
				delete board;
				return 0;
			}
			else {
				frontierSet->push(board);
				return 1;
			}
		}
		else retval = uniqueRow(board, n, curRow + 1, frontierSet, maxSteps);
		return retval;
	}
	else {
		if (rowList.size() == 0) { //when the current row is empty
			int otherRow = curRow;
			while (rowList.size() == 0) { //keep searching until find a non-empty row
				//DEBUG
				if (otherRow >= n) throw 3;

				++otherRow;
				for (int col = 0; col < n; ++col) {
					if (board->queenArray[col].row == otherRow) {
						rowList.emplace_back(board->queenArray[col]);
					}
				}
			}
			board->stepCount += (otherRow - curRow);
			retval = 0;
			list<Queen>::iterator it = rowList.begin();
			++it;
			for (; it != rowList.end(); ++it) {
				queenInd = it->col;
				//make a new board for each single queen shift
				board->queenArray[queenInd].row = curRow;
				board->queenArray[queenInd].direction = -1;
				Board* newBoard = new Board(*board);
				board->queenArray[queenInd].direction = 0;
				board->queenArray[queenInd].row = otherRow;
				retval += uniqueRow(newBoard, n, curRow + 1, frontierSet, maxSteps);
			}
			it = rowList.begin();
			queenInd = it->col;
			board->queenArray[queenInd].row = curRow;
			board->queenArray[queenInd].direction = -1;
			retval += uniqueRow(board, n, curRow + 1, frontierSet, maxSteps);
			return retval;

		}
		else { //when more than one queen is in current row
			retval = 0;
			board->stepCount += 1;
			list<Queen>::iterator it = rowList.begin();
			++it;
			for (list<Queen>::iterator it2 = rowList.begin(); it2 != rowList.end(); ++it2) {
				queenInd = it2->col;
				board->queenArray[queenInd].row++;
			}
			for (; it != rowList.end(); ++it) {
				//make a new board for each queen shift round
				//shift but one queens per new board
				queenInd = it->col;
				board->queenArray[queenInd].row--;
				board->queenArray[queenInd].direction = 1;
				Board* newBoard = new Board(*board);
				board->queenArray[queenInd].direction = 0;
				board->queenArray[queenInd].row++;
				retval += uniqueRow(newBoard, n, curRow + 1, frontierSet, maxSteps);
			}
			it = rowList.begin();
			queenInd = it->col;
			board->queenArray[queenInd].row--;
			board->queenArray[queenInd].direction = 1;
			retval += uniqueRow(board, n, curRow + 1, frontierSet, maxSteps);
			return retval;
		}
	}
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

#endif
