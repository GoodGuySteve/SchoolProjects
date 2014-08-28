// grading2.cpp - uses C++ style object-handling (AFTER CHANGES BELOW)
// Austin Smith, Steven Cabral  4/18/14

#include <iostream>
using namespace std;

class Record {                      
    
// DECLARE A public SECTION, AND DEFINE A NEW METHOD NAMED setGrades,
// THAT TAKES FOUR double ARGUMENTS
    public:
	Record();
	Record(double, double, double, double);
	void setGrades(double quiz1, double quiz2, double midterm, double f);
	char overallGrade() const;
	char overallGrade(double, double, double) const;
	double getQuiz1(){return quiz1;}
	double getQuiz2(){return quiz2;}
	double getMidterm(){return midterm;}
	double getFinal(){return f;}
	void setQuiz1(const double quiz1){this->quiz1 = quiz1;}
	void setQuiz2(const double quiz2){this->quiz2 = quiz2;}
	void setMidterm(const double midterm){this->midterm = midterm;}
	void setFinal(const double f){this->f = f;}

// DECLARE A private SECTION AND MAKE ALL DATA PART OF IT
    private:
	double quiz1, quiz2; 
    	double midterm, f;
	char letterEquiv(const double grade) const;
};

Record getScores();

int main() {
    Record rec = getScores();
    cout << "Grade is " << rec.overallGrade(25.0, 25.0, 50.0) << endl; 
    return 0;
}

Record::Record(){
	this->quiz1 = 0;
	this->quiz2 = 0;
	this->midterm = 0;
	this->f = 0;
}

Record::Record(double quiz1, double quiz2, double midterm, double f){
	if(quiz1 < 0) this->quiz1 = 0;
	else if(quiz1 > 10) this->quiz1 = 10;
	else this->quiz1 = quiz1;

	if(quiz2 < 0) this->quiz2 = 0;
	else if(quiz2 > 10) this->quiz2 = 10;
	else this->quiz2 = quiz2;

	if(midterm < 0) this->midterm = 0;
	else if(midterm > 100) this->midterm = 10;
	else this->midterm = midterm;
	
	if(f < 0) this->f = 0;
	else if(f > 100) this->f = 10;
	else this->f = f;
	return;
}

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
    result = Record(quiz1, quiz2, midterm, f);
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

char Record::overallGrade(double quizWt, double midtermWt, double finalWt) const{
	double sum = quizWt + midtermWt + finalWt;
	quizWt /= sum;
	midtermWt /= sum;
	finalWt /= sum;
	double quizPct = (this->quiz1 / 10 + this->quiz2 / 10)/2;
	double overall = quizWt * quizPct + midtermWt/100 * this->midterm + finalWt/100 * this->f;
	overall *= 100;
	return letterEquiv(overall);
}

// ASK A TA TO HELP IF YOU GET STUCK, BUT FIRST TRY YOUR BEST TO FIGURE IT OUT!
