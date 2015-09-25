% A graph is represented via vertices and edges.
% For our purposes, we have a simple graph with the
% following vertices:
% - a
% - b
% - c
% - d
%
% Additionally, we have the following edges in this graph:
% - a -> b
% - b -> c
% - c -> d
% - a -> d

% We represent the vertices and edges as a series of facts:
vertex(a).
vertex(b).
vertex(c).
vertex(d).

edge(a, b). % edge from a to b, and so on
edge(b, c).
edge(c, d).
edge(a, d).

% -Start: Vertex
% -Goal:  Vertex
%
% Succeeds if we can reach the `Goal` vertex from the
% `Start` vertex.
canReach(Start, Start). % if we are at our target already, we are done
canReach(Start, Goal) :-
        % Get the edges which start at `Start`.  Nondeterministically
        % bind `Intermediate` to target nodes.
        edge(Start, Intermediate),

        % Starting from the `Intermediate` node, which is one step away
        % from the `Start` node, try to reach the `Goal` node.
        canReach(Intermediate, Goal).