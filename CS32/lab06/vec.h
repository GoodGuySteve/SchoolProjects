// vec.h - defines class Vec3
// Steven Cabral, Austin Smith, 5/9/14

#include <iostream>
using namespace std;

class Vec3 {
 public:
    
    // DECLARE (NON-MEMBER) FRIENDS HERE

    friend ostream& operator<< (ostream&, const Vec3&);
    friend istream& operator>> (istream&, Vec3&);
    friend Vec3 operator+ (const Vec3&, const Vec3&);
    friend Vec3 operator* (const Vec3&, const double&);
    // constructors already done
    Vec3();
    Vec3(double, double, double);
    Vec3(const Vec3 &other);
    
    // getters and setters already done too
    double get_x() const;
    double get_y() const;
    double get_z() const;
    void set_x(double);
    void set_y(double);
    void set_z(double);
    
    // DECLARE ANY MORE MEMBER FUNCTIONS HERE


 private:
    double x, y, z;
};
