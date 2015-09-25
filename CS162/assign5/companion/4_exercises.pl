% Below we have some logical facts and some potential queries
% that could be run with those facts.  Your goal is to fill
% in what these queries do.  Ignore any warnings for this file.
% As a hint, you can run this file directly by:
%
% swipl -s 4_exercises.pl
%
% ...and then issuing the queries at the prompt.

% Consider the following Prolog logicbase:

easy(1).
easy(2).
easy(3).

gizmo(a,1).
gizmo(b,3).
gizmo(a,2).
gizmo(d,5).
gizmo(c,3).
gizmo(a,3).
gizmo(c,4).

harder(a,1).
harder(c,X).
harder(b,4).
harder(d,2).

% Write out whether or not the following queries are true or
% false, replacing `???` where appropriate.
% If a query is true and involves variable instantiations, then write out
% all possible variable instantiations which make the query
% true.  Some have already been done for you, to give an
% example of the expected format.  If you wish, you may simply
% copy and paste the output of SWI-PL here directly; we will be grading
% this by hand, so the output format isn't important.

% QUERY: easy(Y), gizmo(X,Y).
% ANSWER: (X = a, Y = 1), (X = a, Y = 2), (X = b, Y = 3), (X = c, Y = 3), (X = a, Y = 3)

% QUERY: gizmo(a,X), easy(X).
% ANSWER: ???

% QUERY: gizmo(c,X), easy(X).
% ANSWER: ???

% QUERY: gizmo(d,Z), easy(Z).
% ANSWER: ???

% QUERY: easy(X), harder(Y,X).
% ANSWER: ???

% QUERY: harder(Y,X), easy(X).
% ANSWER: ???
