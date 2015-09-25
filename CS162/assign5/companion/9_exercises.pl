% For this exercise, we are working with coordinates on a
% two-dimensional plane.  Each coordinate is represented
% by the structure:
%
% coordinate(X, Y)
%
% ...where the 'X' coordinate is represented by `X`, and the
% 'Y' coordinate is represented by `Y`.

% Consider the clause below, which takes a coordinate and
% determines if X is greater than Y:
xGreaterY(coordinate(X, Y)) :-
        X > Y.

% Now write a clause that will determine if the Y coordinate
% is greater than the X coordinate.  Your clause should take
% the coordinate as a parameter, and be named `yGreaterX`.

% ---REPLACE ME WITH CODE---

% Your definition should allow the following query to succeed:
%
% QUERY: yGreaterX(coordinate(0, 1)).
%
% It should also fail on the following query:
%
% QUERY: yGreaterX(coordinate(1, 0)).

% Consider the clause below, which adds 2 to the Y portion
% of some input coordinate, returning a new coordinate:

add2ToY(coordinate(X, Y), coordinate(X, NewY)) :-
        NewY is Y + 2.

% Now write a clause that will add a particular value
% to the X value, returning an adjusted coordinate.
% The clause should be named `addX`, and take:
% -The original coordinate
% -The amount to add to the X coordinate
% -The new coordinate

% ---REPLACE ME WITH CODE---

% Your implementation of `addX` should allow the
% following query to succeed:
%
% QUERY: addX(coordinate(1, 2), 2, coordinate(3, 2)).
