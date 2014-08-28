// vec.cpp - implements Vec3 functions
// Austin Smith, Steven Cabral, 5/9/14

#include <iostream>
#include "vec.h"
using namespace std;

// constructors done
Vec3::Vec3() : x(0), y(0), z(0) { }
Vec3::Vec3(double x, double y, double z) : x(x), y(y), z(z) { }
/* Do not modify the following line. If you see the message "Pass by value
   fail." in your output, it means you aren't using a reference somewhere
   that you should be. */
Vec3::Vec3(const Vec3 &other) { cout << "Pass by value fail."; }

// getters and setters done
double Vec3::get_x() const { return x; }
double Vec3::get_y() const { return y; }
double Vec3::get_z() const { return z; }
void Vec3::set_x(double value) { x = value; }
void Vec3::set_y(double value) { y = value; }
void Vec3::set_z(double value) { z = value; }

// YOUR WORK GOES HERE
ostream& operator<< (ostream& os, const Vec3& vec){
	os << "(" << vec.get_x() << ", " << vec.get_y() << ", " << vec.get_z() << ")";
	return os;
}

istream& operator>> (istream& is, Vec3& vec){
  double x,y,z;
  is >> x >> y >> z;
  vec.set_x(x);
  vec.set_y(y);
  vec.set_z(z);
  return is;
}

Vec3 operator+(const Vec3& vec1, const Vec3& vec2){
	Vec3 vec3;
	vec3.set_x(vec1.get_x()+vec2.get_x());
	vec3.set_y(vec1.get_y()+vec2.get_y());
	vec3.set_z(vec1.get_z()+vec2.get_z());
	return vec3;
}

Vec3 operator*(const Vec3& vec, const double &x){
  Vec3 vec2;
  vec2.set_x(vec.get_x()*x);
  vec2.set_y(vec.get_y()*x);
  vec2.set_z(vec.get_z()*x);
  return vec2;
}
