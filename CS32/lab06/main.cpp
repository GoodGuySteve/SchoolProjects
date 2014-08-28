// main.cpp - tests Vec3 class

#include <iostream>
#include "vec.h"

using namespace std;

int main(int argc, char *argv[]) {

    Vec3 vec_0;
    const Vec3 vec_1(5.5, 6.6, 7.7);
  
    cout << "From default constructor: ";
    cout << vec_0.get_x() << " " << vec_0.get_y() << " " << vec_0.get_z() << endl;
    cout << "From 3-param constructor: ";
    cout << vec_1.get_x() << " " << vec_1.get_y() << " " << vec_1.get_z() << endl;

  
    /* Uncomment the following 2 lines when << is overloaded for ostream */
    cout << "From default constructor: " << vec_0 << endl;
    cout << "From 3-param constructor: " << vec_1 << endl;

  
    /* Uncomment the following 3 lines when >> is overloaded for istream */
    cout << "Input three doubles: ";
    cin >> vec_0;
    cout << "Updated using >> overloading: " << vec_0 << endl;
  
  
    /* Uncomment the following 2 lines when + is overloaded */
    cout << "The previous two added together: " << (vec_0 + vec_1) << endl;
    cout << "More addition: " << (vec_0 + vec_1 + vec_0 + vec_1) << endl;

  
    /* Uncomment the following 2 lines when * is overloaded */
    cout << "Simple multiplication: " << (vec_0 * 8) << endl;
    cout << "More multiplication: " << (vec_0 * 3 * 2) << endl;
}
