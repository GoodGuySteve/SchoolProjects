#include "nBody.h"

int main(int argc, char *argv[]) {

	MPI_Init(&argc,&argv);
	int myrank;
	int nprocs;
	MPI_Comm_rank(MPI_COMM_WORLD, &myrank);
	MPI_Comm_size(MPI_COMM_WORLD, &nprocs);

	int n, iters, timestep;

	double** s; // position in 3D space for each body
	double** v; // velocity in 3D space for each body
	double* m;  // mass of each body
	int size; // # of bodies stored on each proc.

	// arguments: ./nBody r #n #iter #timestep
	// or         ./nBody g #n #iter #timestep
	if (argc != 5) {
		fprintf(stderr, "invalid arguments\n");
		exit(0);
	}
	n = atoi(argv[2]);
	size = n / nprocs;
	iters = atoi(argv[3]);
	timestep = atoi(argv[4]);
	size = n / nprocs;
	
	int i; 
	int j;

	s = (double **)malloc(sizeof(double *) * size);
	for (i = 0; i < size; i++) {
		s[i] = (double*)malloc(sizeof(double) * 3);

		for(j = 0; j < 3; j++) {
			s[i][j] = 0;
		}
	}

	v = (double **)malloc(sizeof(double *) * size);
	for (i = 0; i < size; i++) {
		v[i] = (double*)malloc(sizeof(double) * 3);
		
		for(j = 0; j < 3; j++) {
			v[i][j] = 0;
		}
	}

	m = (double *)malloc(sizeof(double) * size);

	for(i = 0; i < size; i++) {
		m[i] = 0;
	}

	if (strcmp(argv[1], "r") == 0) {
		readnbody(s, v, m, n); 
	} else {
		gennbody(s, v, m, n);
	}

	nbody(s, v, m, n, iters, timestep);

	for (i = 0; i < size; i++) {
		free(s[i]);
		free(v[i]);
	}

	free(s);
	free(v);
	free(m);

	MPI_Finalize();
	return 0;
}
