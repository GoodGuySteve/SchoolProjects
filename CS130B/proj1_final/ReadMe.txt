========================================================================
    CONSOLE APPLICATION : cs130b_proj1 Project Overview
    AUTHOR : Steven Cabral
========================================================================

Contains 7 files plus a makefile. To make the executable, type main with all the files in the current directory. To run, type ./a.out < input   where input is the name of the desired input file.

PowerStation.cpp and PowerStation.h define a simple class used mainly for keeping track of the station coordinates, ID's, and the markings used for various functions.

Input.cpp and Input.h uses a modular way of reading in the input from a file and storing it in an object.

Algorithm.cpp and Algorithm.h contain the four different algorithms that are being tested: ACSF, ACSet, ACF, and DC. It also contains the tree function that all of these functions make use of. This file also contains many helper functions for those algorithms.

Main.cpp contains the main function, which runs the input function and then selects which of the algorithms to initialize.

Test files named inp (given by the TA) were used in correctness testing. Test files named input were used for the timing experiments. 
