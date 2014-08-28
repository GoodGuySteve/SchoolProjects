all: numbers classes

numbers: numbers.cpp
	clang++ -g -pedantic numbers.cpp -o numbers

classes: part2.cpp myclass.h
	clang++ -g -pedantic part2.cpp -o classes


clean:
	rm -f numbers classes
