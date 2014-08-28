// main2.cpp - tests optional and homework parts of Vec3 class
// depends on working parts of the "Overloading operators in C++" lab

#include <iostream>
#include "vec.h"

using namespace std;

int main(int argc, char *argv[]) {

    Vec3 vec_0;
    const Vec3 vec_1(5.5, 6.6, 7.7);
 
    cout << "Input three doubles: ";
    cin >> vec_0;
    cout << "Input vector: " << vec_0 << endl;
    cout << "Constant vector: " << vec_1 << endl;
  
// For testing the three optional, "After lab-work" items:

    /* Uncomment the following 2 lines when * is overloaded for double on left*/
    //cout << "Simple multiplication: " << (8 * vec_0) << endl;
    //cout << "More multiplication: " << (3 * vec_0 * 2) << endl;

    /* Uncomment the following 2 lines when binary - is overloaded */
    //cout << "Input vector - const vector: " << (vec_0 - vec_1) << endl;
    //cout << "More subtraction: " << (vec_0 - vec_1 - vec_0 - vec_1) << endl;

    /* Uncomment the following 3 lines when *= is overloaded*/
    //vec_0 *= 2; 
    //cout << "Input vector after *= 2: " << vec_0 << endl;
    //cout << "Last result *=.5: " << (vec_0 *= .5)  << endl;

// For testing the three homework items (actually five functions):

    /* Uncomment the following 2 lines when += is overloaded*/
    //cout << "Input vec += constant vec: " << (vec_0 += vec_1) << endl;
    //cout << "     Verify still changed: " << vec_0 << endl;
    
    /* Uncomment the following 4 lines when both versions of ++ overloaded */
    //cout << "++(Last vec) : " << ++vec_0 << endl;
    //cout << "  Verify same: " << vec_0 << endl;
    //cout << "(Last vec)++ : " << vec_0++ << endl;
    //cout << "  Verify diff: " << vec_0 << endl;

    /* Uncomment the following 3 lines when both versions of [] overloaded */
    //cout << "Constant's data: " << vec_1[0] << " " << vec_1[1] << " " << vec_1[2] << endl;
    //vec_0[0] = vec_0[1] = vec_0[2] = 77.77;
    //cout << "Final vec: " << vec_0 << endl;
}
