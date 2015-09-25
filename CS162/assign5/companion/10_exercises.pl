% Write out whether or not the following queries are true or
% false, replacing `???` where appropriate.
% If a query is true and involves variable instantiations, then write out
% all possible variable instantiations which make the query
% true.  Some have already been done for you, to give an
% example of the expected format, and to provide additional examples.
% If you wish, you may simply
% copy and paste the output of SWI-PL here directly; we will be grading
% this by hand, so the output format isn't important.

% QUERY: 1 = 1.
% ANSWER: true

% QUERY: 2 = 2.
% ANSWER: ???

% QUERY: 1 = 2.
% ANSWER: false

% QUERY: 2 = 3.
% ANSWER: ???

% QUERY: X = 1.
% ANSWER: X = 1

% QUERY: X = 2.
% ANSWER: ???

% QUERY: 1 = X.
% ANSWER: X = 1

% QUERY: 2 = X.
% ANSWER: ???

% QUERY: foo = foo.
% ANSWER: true

% QUERY: bar = bar.
% ANSWER: ???

% QUERY: foo = bar.
% ANSWER: false

% QUERY: bar = baz.
% ANSWER: ???

% QUERY: X = foo.
% ANSWER: X = foo

% QUERY: bar = X.
% ANSWER: ???

% QUERY: X = X.
% ANSWER: true

% QUERY: X = Y.
% ANSWER: X = Y

% QUERY: X = 1, X = Y.
% ANSWER: (X = Y, Y = 1)

% QUERY: a(X) = a(1).
% ANSWER: X = 1

% QUERY: b(X) = b(1).
% ANSWER: ???

% QUERY: a(X, Y) = a(1).
% ANSWER: false

% QUERY: a(1) = a(X, Y).
% ANSWER: ???

% QUERY: a(X) = b(1).
% ANSWER: false

% QUERY: b(1) = c(1).
% ANSWER: ???

% QUERY: a(b,c) = a(X,Y).
% ANSWER: ???

% QUERY: a(X,c(d,X)) = a(2,c(d,Y)).
% ANSWER: ???

% QUERY: a(X,Y) = a(b(c,Y),Z).
% ANSWER: ???

% QUERY: tree(left, root, Right) = tree(left, root, tree(a, b, tree(c, d, e))).
% ANSWER: ???
