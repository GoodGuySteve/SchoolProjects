// BFSTEST : Test breadth-first search in a graph.
// 
// example: cat sample.txt | ./bfstest 1
//
// Author 1: Steven Cabral
// Author 2: Bronwyn Perry-Huston
//
// Modified version of code by John R. Gilbert, 17 Feb 2011

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <limits>
#include <cmath>
#include <iostream>
#include <cilk/cilk.h>
#include <cilk/cilk_api.h>

#include "Bag.h"
//#include "bag_reducer.h"

#define GRANULARITY 100 //granularity for the bag merge function
#define THREADS_PER_WORKER 4 //threads per worker distributed on main loop

using namespace std;

const int imax = std::numeric_limits<int>::max();
const int nworkers = __cilkrts_get_nworkers();

typedef struct graphstruct { // A graph in compressed-adjacency-list (CSR) form
  int nv;            // number of vertices
  int ne;            // number of edges
  int *nbr;          // array of neighbors of all vertices
  int *firstnbr;     // index in nbr[] of first neighbor of each vtx
} graph;


int read_edge_list (int **tailp, int **headp) {
  int max_edges = imax;
  int nedges, nr, t, h;
  *tailp = (int *) calloc(max_edges, sizeof(int));
  *headp = (int *) calloc(max_edges, sizeof(int));
  nedges = 0;
  nr = scanf("%i %i",&t,&h);
  while (nr == 2) {
    if (nedges >= max_edges) {
      printf("Limit of %d edges exceeded.\n",max_edges);
      exit(1);
    }
    (*tailp)[nedges] = t;
    (*headp)[nedges++] = h;
    nr = scanf("%i %i",&t,&h);
  }
  return nedges;
}


graph * graph_from_edge_list (int *tail, int* head, int nedges) {
  graph *G;
  int i, e, v, maxv;
  G = (graph *) calloc(1, sizeof(graph));
  G->ne = nedges;
  maxv = 0;

  // count vertices
  for (e = 0; e < G->ne; e++) {
    if (tail[e] > maxv) maxv = tail[e];
    if (head[e] > maxv) maxv = head[e];
  }
  G->nv = maxv+1;
  G->nbr = (int *) calloc(G->ne, sizeof(int));
  G->firstnbr = (int *) calloc(G->nv+1, sizeof(int));

  // count neighbors of vertex v in firstnbr[v+1],
  for (e = 0; e < G->ne; e++) G->firstnbr[tail[e]+1]++;

  // cumulative sum of neighbors gives firstnbr[] values
  for (v = 0; v < G->nv; v++) G->firstnbr[v+1] += G->firstnbr[v];

  // pass through edges, slotting each one into the CSR structure
  for (e = 0; e < G->ne; e++) {
    i = G->firstnbr[tail[e]]++;
    G->nbr[i] = head[e];
  }
  // the loop above shifted firstnbr[] left; shift it back right
  for (v = G->nv; v > 0; v--) G->firstnbr[v] = G->firstnbr[v-1];
  G->firstnbr[0] = 0;
  return G;
}


void print_CSR_graph (graph *G) {
  int vlimit = 20;
  int elimit = 50;
  int e,v;
  printf("\nGraph has %d vertices and %d edges.\n",G->nv,G->ne);
  printf("firstnbr =");
  if (G->nv < vlimit) vlimit = G->nv;
  for (v = 0; v <= vlimit; v++) printf(" %d",G->firstnbr[v]);
  if (G->nv > vlimit) printf(" ...");
  printf("\n");
  printf("nbr =");
  if (G->ne < elimit) elimit = G->ne;
  for (e = 0; e < elimit; e++) printf(" %d",G->nbr[e]);
  if (G->ne > elimit) printf(" ...");
  printf("\n\n");
}

Bag* bag_merge(Bag* bagArray, int size){
  Bag* retval;
  Bag* other;
  if (size > GRANULARITY) {
    retval = cilk_spawn bag_merge(bagArray, size / 2);
    other = bag_merge(bagArray + size/2, size/2+size%2);
    cilk_sync;
    retval = retval->bag_union(other);
    return retval;
  }
  else {
    for (int i = size - 2; i >= 0; --i) {
      bagArray[i].bag_union(&bagArray[i+1]);
    }
    return &bagArray[0];
  }
}

void bfs (int s, graph *G, int **levelp, int *nlevelsp, 
         int **levelsizep, int **parentp) {
  int *level, *levelsize, *parent;
  int thislevel;
  int *queue, back, front;
  int i, v, e;
  int scale = floor(log2(G->nv) + 1);
  int maxV = exp2(scale);
  int numchunks = nworkers * THREADS_PER_WORKER;
  int chunksize;
  level = *levelp = (int *) calloc(G->nv, sizeof(int));
  levelsize = *levelsizep = (int *) calloc(G->nv, sizeof(int));
  parent = *parentp = (int *) calloc(G->nv, sizeof(int));
  int* curLevelV = new int[G->nv];
  //bag_reducer nextLevelV(ceil(log2(G->nv)));
  Bag* nextLevelV = new Bag[numchunks + 1]; //makes a granular amount of bags

  // initially, nextLevel is empty, all levels and parents are -1
  for (v = 0; v < G->nv; v++) level[v] = -1;
  for (v = 0; v < G->nv; v++) parent[v] = -1;

  // assign the starting vertex level 0 and put it in the current array to explore
  thislevel = 0;
  level[s] = 0;
  levelsize[0] = 1;
  curLevelV[0] = s; //queue[back++] = s;

  //DEBUG
  //cout << "Entering main loop: " << endl;

  // loop over levels, then over vertices at this level, then over neighbors
  while (levelsize[thislevel] > 0) {
    //DEBUG
    //cout << "Elements in the current level set: " << endl;
    //for (int ind = 0; ind < levelsize[thislevel]; ++ind) cout << curLevelV[ind] << " ";
    //cout << endl;

    //DEBUG
    //cout << "Level "<< thislevel << " has "<< levelsize[thislevel] << " elements" << endl;

    levelsize[thislevel+1] = 0;
    chunksize = levelsize[thislevel] / numchunks;    

    nextLevelV[numchunks].init(scale);
    //first do remainder of the upcoming parallel split
    for (int i = chunksize*numchunks; i < levelsize[thislevel]; ++i) {
      int w, v;
      v = curLevelV[i];
      for (int e = G->firstnbr[v]; e < G->firstnbr[v+1]; e++) {
        w = G->nbr[e];
      
        if (level[w] == -1) {
          parent[w] = v;
          level[w] = thislevel+1;
          nextLevelV[numchunks].bag_insert(w);
        }
      }
    }

    //DEBUG
    //cout << "Remainder bag contains " << nextLevelV[numchunks].size << " items" << endl;

    //split memory into chunks for cilk parallelism
    cilk_for (int i = 0; i < numchunks; ++i) {
      nextLevelV[i].init(scale);
      for (int j = 0; j < chunksize; ++j) {
        int w, v;
        v = curLevelV[i * chunksize + j];       // v is the current vertex to explore from

        //DEBUG
        //cout << "index value is "<< i * numchunks + j << endl;      

        for (int e = G->firstnbr[v]; e < G->firstnbr[v+1]; e++) {
          w = G->nbr[e];          // w is the current neighbor of v
        
          if (level[w] == -1) {   // w has not already been reached
            parent[w] = v;
            level[w] = thislevel+1;
            //++levelsize[thislevel+1];
            nextLevelV[i].bag_insert(w);    // put w on next level to explore
          }
        }
      }
    }
    //DEBUG
    //cout << "combining bags... "<< endl;

    //combine all the bags
    //for (int i = numchunks - 1; i >= 0; --i) { recursive method here for better merge time? parallel?
      //cout << "bag " << i << " has size " << nextLevelV[i].size << endl;
      //nextLevelV[i].bag_union(&nextLevelV[i+1]);
    //}
    
    //recursively combine all bags into one (in parallel)
    bag_merge(nextLevelV, numchunks+1);

    //for (int i = 0; i < G->nv; ++i) { //TODO parallel this 
      //if (level[i] == thislevel+1) ++levelsize[thislevel+1];
    //}

    levelsize[thislevel+1] = nextLevelV[0].size;

    //DEBUG
    //cout << "Bags combined into bag of size " << nextLevelV[0].size << endl;
    //cout << "Level size is " << levelsize[thislevel+1] << endl;

    thislevel = thislevel+1;
    
    delete[] curLevelV;
    curLevelV = nextLevelV[0].toArray();	
  }
  *nlevelsp = thislevel;
}


int main (int argc, char* argv[]) {
  graph *G;
  int *level, *levelsize, *parent;
  int *tail, *head;
  int nedges;
  int nlevels;
  int startvtx;
  int i, v, reached;

  if (argc == 2) {
    startvtx = atoi (argv[1]);
  } else {
    printf("usage:   bfstest <startvtx> < <edgelistfile>\n");
    printf("example: cat sample.txt | ./bfstest 1\n");
    exit(1);
  }
  nedges = read_edge_list (&tail, &head);
  
  G = graph_from_edge_list (tail, head, nedges);
  free(tail);
  free(head);
  print_CSR_graph (G);

  printf("Starting vertex for BFS is %d.\n\n",startvtx);
  bfs (startvtx, G, &level, &nlevels, &levelsize, &parent);

  reached = 0;
  for (i = 0; i < nlevels; i++) reached += levelsize[i];
  printf("Breadth-first search from vertex %d reached %d levels and %d vertices.\n",
    startvtx, nlevels, reached);
  for (i = 0; i < nlevels; i++) printf("level %d vertices: %d\n", i, levelsize[i]);
  if (G->nv < 20) {
    printf("\n  vertex parent  level\n");
    for (v = 0; v < G->nv; v++) printf("%6d%7d%7d\n", v, parent[v], level[v]);
  }
  printf("\n");
}




