Steven Cabral
CS165A
M 4PM Section
2/9/15
Perm: 6801179
MP1 – N-Queens problem

NOTE: Instructions were unclear for the report, so I attached it and am submitting it as the readme.
However, I'm also attaching what would normally go in a readme as well:

README
	Included files: Board.h, Board.cpp, main.cpp, Makefile
	
	Build instructions: make command generates puzzle executable
	
	Run instructions: puzzle < inputFile
	
	Program is compiled using C++11 (-std=c++11 flag in g++). I have tested it on csil, and it runs as expected,
	but I have also had a grader have an issue with this flag in the past. 
	
	Input file must be formatted as follows: first line contains 1 integer (n), next line contains 1 integer (maxsteps),
	next n lines contain 2 integers separated by whitespace
	
	Program will use search algorithms to try to find an optimal configuration for n queens on a chessboard with minimal
	conflicts and minimal steps.

REPORT
	To solve this problem, the approach I used was a variation on A* search. The algorithm works in two steps:
	first, the board is shifted into a state where each queen not only has its own row, but its own column as well
	(which is done by depth-limited depth-first search). Then, the algorithm moves the pieces only by swapping the rows
	instead of making step-by-step movements, which gives a large branching factor but skips many of the steps that a
	step-by-step algorithm would need. The swaps are performed using  A* search, moving towards lower conflicts.
	
	More specifically, the program stores the queens as (x,y) coordinates, and it represents the queens on a board 
	structure, which is effectively an array of queens. Each board represents a state for the search algorithm. The 
	function uniqueRow recursively puts the board into different states in which the pieces can move by swaps, done 
	in the main function. All of the board states are stored in a priority queue, from which they are removed according
	to the heuristic.
	
	A few key properties of the problem were taken advantage of for this solution. First, the property that
	the solution has each queen in its unique row and column allows for the second algorithm step to move by 
	swapping instead of step-by-step movements. Another property of the board is that, assuming the board is 
	now in a format with no row conflicts, the diagonal conflicts can be calculated by seeing which pieces share a sum
	of their coordinates (x + y) or a difference of their coordinates (x – y). Another important property is that the optimal solution will not include pieces that will double back; therefore, the branching factor can be lowered by fixing the direction for each piece once it makes a move.
	The heuristic used was exploring solutions with a lower step cost, then solutions with a lower amount of conflicts.
	Step cost was placed at a high priority to prioritize finding the optimal solution. This heuristic will expand more 
	nodes than prioritizing amount of conflicts, but the solution it finds is guaranteed to be an optimal one. 
	
	The advantage to using this variation of A* search is that, using the properties of the problem, the search space 
	was narrowed down so that the same state will never be explored again by this program, and no memory needs to be 
	dedicated to already-explored states. The biggest challenge lies in the second step. It can move queens more steps
	at a time than a step-wise A* search, but it necessitates an additional search step to get the board in a workable
	form. For the first part, I used a depth-first search, which is very inefficient, but it is guaranteed to find 
	an optimal path to get some number of workable boards in proper format. Because this number could get large, I 
	used a strict cut-off to keep the set in a workable size. Since the second step does more optimization more 
	efficiently, it is acceptable to give the step less refined initial data.
	
	This algorithm gives up a lot of speed in exchange for optimality. A heuristic change would likely effect
	a much higher speed, but may end up sacrificing optimality, depending on the heuristic used. Additionally, 
	because of the two-step process, the algorithm is very inconsistent, and its efficiency varies wildly with the
	file input to it.
