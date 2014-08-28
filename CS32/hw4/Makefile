#Steven Cabral, Austin Smith
grading2: main.o grading2.o
	g++ main.o grading2.o -o grading2
main.o: main.cpp grading2.h
	g++ main.cpp -c
grading2.o: grading2.cpp grading2.h
	g++ grading2.cpp -c

clean:
	rm grading2 main.o grading2.o
