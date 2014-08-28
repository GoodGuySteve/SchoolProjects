// Austin Smith, Steven Cabral  5/1/14

#include <iostream>
using namespace std;

class Record {                      
    public:
	void setGrades(double quiz1, double quiz2, double midterm, double f);
	char overallGrade() const;
    private:
	double quiz1, quiz2; 
    	double midterm, f;
	char letterEquiv(const double grade) const;
};

Record getScores();