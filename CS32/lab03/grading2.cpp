// grading2.cpp - uses C++ style object-handling (AFTER CHANGES BELOW)
// Austin Smith, Steven Cabral  4/18/14

#include <iostream>
using namespace std;

class Record {                      
    
// DECLARE A public SECTION, AND DEFINE A NEW METHOD NAMED setGrades,
// THAT TAKES FOUR double ARGUMENTS
    public:
	void setGrades(double quiz1, double quiz2, double midterm, double f);
	char overallGrade() const;
// DECLARE A private SECTION AND MAKE ALL DATA PART OF IT
    private:
	double quiz1, quiz2; 
    	double midterm, f;
	char letterEquiv(const double grade) const;
};

Record getScores();

int main() {
    Record rec = getScores();
    cout << "Grade is " << rec.overallGrade() << endl;    // FIX USAGE OF rec
    return 0;
}

// getScores IS AN external FUNCTION, SO IT MUST USE setGrades TO SET THE VALUES
// OF THE OBJECT'S private DATA (Hint: You'll need some local variables.)
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

// THE NEXT TWO FUNCTIONS ARE METHODS OF THE CLASS, SO THEY MUST PROPERLY
// BE IDENTIFIED AS SUCH WITH THE SCOPE RESOLUTION OPERATOR '::'
// REMEMBER TO IMPLEMENT THE NEW setGrades METHOD TOO.

char Record::letterEquiv(const double grade) const {      // FIX SIGNATURE
    if (grade >= 90) return 'A';
    if (grade >= 80) return 'B';
    if (grade >= 70) return 'C';
    if (grade >= 60) return 'D';
    return 'F';
}

char Record::overallGrade() const {   // FIX SIGNATURE
    // ALSO CHANGE HOW THE OBJECT'S DATA ARE ACCESSED (WHO IS r NOW?  ;-)
    double quizPct = (this->quiz1 / 10 + this->quiz2 / 10) / 2;
    double overall = 25 * quizPct + 0.25 * this->midterm + 0.5 * this->f;
    return letterEquiv(overall);
}

// ASK A TA TO HELP IF YOU GET STUCK, BUT FIRST TRY YOUR BEST TO FIGURE IT OUT!
