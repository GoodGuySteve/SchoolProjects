// grading2.cpp - uses C++ style object-handling (AFTER CHANGES BELOW)
// Austin Smith, Steven Cabral  5/01/14

#include "grading2.h"
using namespace std;

void Record::setGrades(double quiz1, double quiz2, double midterm, double f){
	this->quiz1 = quiz1;
	this->quiz2 = quiz2;
	this->midterm = midterm;
	this->f = f;
	return;
}

Record getScores() {
    Record result;
    double quiz1, quiz2, midterm, f;
    cout << "Enter scores in this order: 2 quizzes, midterm, final: ";
    cin >> quiz1 >> quiz2 >> midterm >> f;
    result.setGrades(quiz1, quiz2, midterm, f);
    return result;
}

char Record::letterEquiv(const double grade) const {      // FIX SIGNATURE
    if (grade >= 90) return 'A';
    if (grade >= 80) return 'B';
    if (grade >= 70) return 'C';
    if (grade >= 60) return 'D';
    return 'F';
}

char Record::overallGrade() const {  
    double quizPct = (this->quiz1 / 10 + this->quiz2 / 10) / 2;
    double overall = 25 * quizPct + 0.25 * this->midterm + 0.5 * this->f;
    return letterEquiv(overall);
}

