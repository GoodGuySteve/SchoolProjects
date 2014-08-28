// Austin Smith, Steven Cabral  5/1/14

#include "grading2.h"

int main() {
    Record rec = getScores();
    cout << "Grade is " << rec.overallGrade() << endl;    // FIX USAGE OF rec
    return 0;
}
