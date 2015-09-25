Note for students:

1. You have been given two harnesses, one for the program life, and the other for validation.
2. The program harness consists of 2 files - life.cpp and submit.cpp. The program harness 
   can be used with and without debug option, using a DEBUG flag which can be set to 1 or 0.
   With the debugging, the code prints out the live cells for 10 selected iterations. Which iterations
   are to be used for data collecting is specified as comments in submit.cpp::life() function. The 
   Makefile generates two executables, with the DEBUG flag on/off. Use the life_debug for validation.    
   You should collect the result in an output file, to be used by the executable validate.

3. The validation harness takes your result as input and verifies it for certain cases. These
   cases are provided in the input/ folder. There are in all 3 test cases - input-0, input-1.1
   to input-1.4 and input-2. ./validate should read at standard input the result of ./life.
   
   e.g.
   		./validate 10 50 1 < input/result-1.1
	
   The first argument is the matrix size, the second is the number of iterations, and the third is the test case number.
   Here is the test case number list you should use for different input files.

   Input file		Test number

   input-0		0
   input-1.x		1
   input-2		2


Example execution for life and validate:

make
./life_debug r 10 50 < input/input-1.1 > result-1.1
./validate 10 50 1 < result-1.1


   
