% Write out whether or not the following queries are true or
% false, replacing `???` where appropriate.
% If a query is true and involves variable instantiations, then write out
% all possible variable instantiations which make the query
% true.  Some have already been done for you, to give an
% example of the expected format, and to provide additional examples.
% If you wish, you may simply
% copy and paste the output of SWI-PL here directly; we will be grading
% this by hand, so the output format isn't important.

% QUERY: [] = [H|T].
% ANSWER: false

% QUERY: [1] = [H|T].
% ANSWER: (H = 1, T = [])

% QUERY: [1, 2, 3] = [H|T].
% ANSWER: (H = 1, T = [2, 3])

% QUERY: [1, 2, 3, 4] = [H1, H2|T].
% ANSWER: (H1 = 1, H2 = 2, T = [3, 4])

% QUERY: [1, 2, 3] = [A, B, C].
% ANSWER: (A = 1, B = 2, C = 3)

% QUERY: [a,b,c,d] = [H|T].
% ANSWER: ???

% QUERY: [a,[b,c,d]] = [H|T].
% ANSWER: ???

% QUERY: [a] = [H|T].
% ANSWER: ???

% QUERY: [apple,3,X,'What?'] = [A,B|Z].
% ANSWER: ???

% QUERY: [[a,b,c],[d,e,f],[g,h,i]] = [H|T].
% ANSWER: ???

% QUERY: [a(X,c(d,Y)), b(2,3), c(d,Y)] = [H|T].
% ANSWER: ???
