% For this portion of the assignment, you must write code
% at the portions marked `---FILL ME IN---`.  We have provided
% example code and hints along the way.

% myAppend: Takes two input lists and a result list.
% Determines if the third list is the result of appending
% the first two lists together.
% Example:
%
% myAppend([1, 2, 3], [], L). % L = [1, 2, 3]
% myAppend([], [1, 2, 3], L). % L = [1, 2, 3]
% myAppend([1, 2], [3, 4], L). % L = [1, 2, 3, 4]
%

% BASE CASE - appending an empty list onto some other
% list results in the other list
myAppend([], L, L).

% RECURSIVE CASE - A non-empty list appended onto
% some other list.  In this case, we know the result
% list needs to begin with whatever the non-empty
% list begins with (`H` below).  We can then recursively
% process the rest of the non-empty list and the result
% list.
myAppend([H|T], L, [H|R]) :-
        myAppend(T, L, R).

% myMember: Takes an element and a list.  Determines if
% the element is in the provided list.
% Example:
%
% myMember(1, [1, 2, 3]). % true
% myMember(0, [1, 2, 3]). % false
%

% BASE CASE - A list begins with the element of interest
myMember(X, [X|_]).

% RECURSIVE CASE - The head of the list isn't interesting.
% Recursively process the tail.
myMember(X, [_|R]) :-
        myMember(X, R).

% myNextto: Takes two list elements and a list.  Determines
% if the list contains the first element followed by the second.
% Example:
%
% myNextto(1, 2, [1, 2, 3]). % true
% myNextto(1, 3, [1, 2, 3]). % false
%

% BASE CASE - The list begins with X and Y.
% ---FILL ME IN---

% RECURSIVE CASE - The head of the list isn't interesting.
% Recursively process the tail.
% ---FILL ME IN---


% myNth0: Takes an index, a list, and a result element.  Gets the
% nth element in the list, starting from 0.  For example:
%
% myNth0(0, [4, 5, 6], 4). % true
% myNth0(1, [4, 5, 6], 5). % true
% myNth0(0, [], _). % false
%

% BASE CASE - We have 0 and a non-empty list.  The result
% should simply be the head of the non-empty list.
% ---FILL ME IN---

% RECURSIVE CASE - We have a number greater than 0 and a
% non-empty list.  Process the tail of the list recursively
% with the number - 1.  Keep in mind that to perform any sort
% of arithmetic, we must use `is` in Prolog.
% ---FILL ME IN---

% myLast: Gets the last element of a list (the first parameter),
% returning it in the second parameter.  For example:
%
% myLast([1], 1). % true
% myLast([1, 2, 3], X). % X = 3
% myLast([], _). % false
%

% BASE CASE - A list that contains one element.  The last element
% is that element directly.
% ---FILL ME IN---

% RECURSIVE CASE - A list that contains more than one element.
% Recursively process on the list, starting from the second element
% ---FILL ME IN---

% myLength: takes an input list, and returns the length of the input
% list in the second parameter.  For example:
%
% myLength([], 0). % true
% myLength([0, 1, 2], I). % I = 3

% ---FILL ME IN---

% mySum: takes a list (presumably of integers) and returns their
% summation in the second parameter.  For example:
%
% mySum([], 0). % true
% mySum([1, 2, 3], 6). % true
%

% ---FILL ME IN---
