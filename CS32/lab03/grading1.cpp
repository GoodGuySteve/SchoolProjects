// grading1.cpp - uses C style object-handling
// cmc, 9/17/2011

#include <iostream>
using namespace std;

struct Record {
    double quiz1, quiz2;
    double midterm, final;
};

Record getScores();
char letterEquiv(double grade);
char overallGrade(const Record r);

int main() {
    Record rec = getScores();
    cout << "Grade is " << overallGrade(rec) << endl;
    return 0;
}

Record getScores() {
    Record result;
    cout << "Enter scores in this order: 2 quizzes, midterm, final: ";
    cin >> result.quiz1 >> result.quiz2 >> result.midterm >> result.final;
    return result;
}

char letterEquiv(double grade) {
    if (grade >= 90) return 'A';
    if (grade >= 80) return 'B';
    if (grade >= 70) return 'C';
    if (grade >= 60) return 'D';
    return 'F';
}

char overallGrade(const Record r) {
    double quizPct = (r.quiz1 / 10 + r.quiz2 / 10) / 2;
    double overall = 25 * quizPct + 0.25 * r.midterm + 0.5 * r.final;
    return letterEquiv(overall);
}
