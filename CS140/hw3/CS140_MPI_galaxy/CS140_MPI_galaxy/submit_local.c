/*
Assignment 3 
Team Member 1 : Steven Cabral
Team Member 2 : Liam Cardenas
*/

#include "nBody.h"
#include <math.h>
#include <time.h>
#define pi 3.14159265358979

//function to create random double
//this function sometimes returns -1.#IND (Not a number) - why is that?
//function not used
/*
double fRand(void) {
	//Seed the random number generator
	srand((unsigned)time(NULL) + rand());
	double retval = (double)(rand()) / (double)(RAND_MAX);
	if (retval == retval) return retval; //if statement returns false if retval is NaN
	else return fRand();
}*/

void readnbody(double** s, double** v, double* m, int n) {
	int myrank;
	int nprocs;
	int i;
	//MPI_Comm_rank(MPI_COMM_WORLD, &myrank);
	//MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
	//DEBUG
	myrank = 0;
	nprocs = 1;
	
	// This is an example of reading the body parameters from the input file. 
	if (myrank == 0) {
		for (i = 0; i < n; i++) {
			double x, y, z, vx, vy, vz, mass;

			int result = fscanf(stdin, INPUT_BODY, &x, &y, &z, &vx, &vy, &vz, &mass);
			if (result != 7) {
				fprintf(stderr, "error reading body %d. Check if the number of bodies is correct.\n", i);
				exit(0);
			}
			s[i][0] = x;
			s[i][1] = y;
			s[i][2] = z;
			v[i][0] = vx;
			v[i][1] = vy;
			v[i][2] = vz;
			m[i] = mass;
		}
	}
}

void gennbody(double** s, double** v, double* m, int n) {
	int myrank;
	int nprocs;
	int i;
	//MPI_Comm_rank(MPI_COMM_WORLD, &myrank);
	//MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
	//DEBUG
	myrank = 0;
	nprocs = 1;

	n = n / nprocs;

	//Seed the random number generator
	srand((unsigned)time(NULL) + rand());

	// solar system case: n == 0
	// This case does not work because memory is not allocated to it properly from nBody.c
	// in the n == 0 case
	if (n == 0) {
		/*n = 10;
		int earthIndex = 3;
		double earthspeed = 29800;
		double mass[] = { 1.99e30, 3.30e23, 4.87e24, 5.97e24, 6.42e23, \
			1.90e27, 5.69e26, 1.02e26, 8.68e25, 1.31e22 };
		double dist[] = { 0, 5.8e10, 1.08e11, 1.50e11, 2.28e11, \
			7.78e11, 1.43e12, 2.87e12, 4.50e12, 5.91e12 };
		double theta[10];
		for (i = 0; i < 10; ++i) {
			theta[i] = 2 * pi * (double) rand() / RAND_MAX;
			s[i][0] = dist[i] * cos(theta[i]);
			s[i][1] = dist[i] * sin(theta[i]);
			s[i][2] = 0;
			m[i] = mass[i];
		}

		v[0][0] = 0;
		v[0][1] = 0;
		v[0][2] = 0;
		for (i = 1; i < 10; ++i) {
			double speed = earthspeed * sqrt(dist[earthIndex] / dist[i]);
			double dirX = -s[i][1];
			double dirY = s[i][0];
			double dirZ = 0;
			double temp = sqrt(dirX*dirX + dirY*dirY);
			if (temp != 0) {
				dirX = dirX * 1 / temp;
				dirY = dirY * 1 / temp;
			}
			v[i][0] = speed * dirX;
			v[i][1] = speed * dirY;
			v[i][2] = speed * dirZ;
		}*/
	}

	else {
		for (i = 0; i < n; ++i) {
			m[i] = 1e30 * (double) rand() / RAND_MAX;
			double dist = 0.5e13 * (double)rand() / RAND_MAX;
			double theta = 2 * pi * (double)rand() / RAND_MAX;
			s[i][0] = dist * cos(theta);
			s[i][1] = dist * sin(theta);
			s[i][2] = ((double)rand()/RAND_MAX - 0.5) * 1.0e11;
			v[i][0] = 0;
			v[i][1] = 0;
			v[i][2] = 0;
		}

	}

	//printf("Generate nBody initial condition here.\n");
}

void nbody(double** s, double** v, double* m, int n, int iter, int timestep) {
	if (iter == 0) iter = 500;
	int myrank;
	int nprocs;
	int i, j;
	//MPI_Comm_rank(MPI_COMM_WORLD, &myrank);
	//MPI_Comm_size(MPI_COMM_WORLD, &nprocs);

	//DEBUG
	myrank = 0;
	nprocs = 1;

	int size = n / nprocs;

	double G = 6.674e-11;             // gravitational constant, m ^ 3 kg^-1 s^-2

	int itNum;
	int procStep;
	double r, f;
	
	double* sendBuf = (double*)malloc(sizeof(double) * size * 7);
	double* recBuf = (double*)malloc(sizeof(double) * size * 7);

	//acceleration each body for the time step stored here
	double** axyz = (double **)malloc(sizeof(double *) * size);
	for (i = 0; i < size; i++) {
		axyz[i] = (double*)malloc(sizeof(double) * 3);
		for (j = 0; j < 3; j++) {
			axyz[i][j] = 0;
		}
	}

	for (itNum = 0; itNum < iter; ++itNum) {

		//store the state at the start of the timestep for MGR
		for (i = 0; i < size; ++i) {
			sendBuf[7 * i] = m[i];
			sendBuf[7 * i + 1] = s[i][0];
			sendBuf[7 * i + 2] = s[i][1];
			sendBuf[7 * i + 3] = s[i][2];
			sendBuf[7 * i + 4] = v[i][0]; //sending velocity is technically unnecessary
			sendBuf[7 * i + 5] = v[i][1];
			sendBuf[7 * i + 6] = v[i][2];
		}

		//first do all processing that can be done on local processor
		for (i = 0; i < size; ++i) {
			for (j = 0; j < size; ++j) {
				r = sqrt((s[j][0] - s[i][0])*(s[j][0] - s[i][0]) + (s[j][1] - s[i][1])*(s[j][1] - s[i][1]) \
					+ (s[j][2] - s[i][2])*(s[j][2] - s[i][2]));
				if (r == 0) continue; //acceleration already initialized to 0
				f = G * m[j] * m[i] / (r * r); //this gives force divided by m[i]
				
				axyz[i][0] = axyz[i][0] + f * (s[i][0] - s[j][0]) / (r);
				axyz[i][1] = axyz[i][1] + f * (s[i][1] - s[j][1]) / (r);
				axyz[i][2] = axyz[i][2] + f * (s[i][2] - s[j][2]) / (r);

			}
			axyz[i][0] = -axyz[i][0] / m[i];
			axyz[i][1] = -axyz[i][1] / m[i];
			axyz[i][2] = -axyz[i][2] / m[i];
		}

		//next do send and receive to process remaining data
		for (procStep = 0; procStep < nprocs - 1; ++procStep) { //for each send and receive round

			if (myrank % 2 == 0) {
				//send to p+1 mod nprocs then receive
				//MPI_Send(sendBuf, size * 7, MPI_DOUBLE, (myrank + 1) % nprocs, 1, MPI_COMM_WORLD);
				//MPI_Recv(recBuf, size * 7, MPI_DOUBLE, (nprocs + myrank - 1) % nprocs, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			}
			else {
				//receive from p-1 mod nprocs then send
				//MPI_Recv(recBuf, size * 7, MPI_DOUBLE, (nprocs + myrank - 1) % nprocs, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
				//MPI_Send(sendBuf, size * 7, MPI_DOUBLE, (myrank + 1) % nprocs, 1, MPI_COMM_WORLD);
			}

			for (i = 0; i < size; ++i) { //for each planet stored on this processor
				for (j = 0; j < size; ++j) { //for each planet j received from MGR calls
					r = sqrt((recBuf[j * 7 + 1] - s[i][0])*(recBuf[j * 7 + 1] - s[i][0]) \
						+ (recBuf[j * 7 + 2] - s[i][1])*(recBuf[j * 7 + 2] - s[i][1]) \
						+ (recBuf[j * 7 + 3] - s[i][2])*(recBuf[j * 7 + 3] - s[i][2]));
					if (r == 0) continue; //acceleration already initialized to 0 - don't need to worry about that
					f = G * recBuf[j*7] * m[i] / (r * r); //this gives force divided by m[i]

					axyz[i][0] = axyz[i][0] + f * (s[i][0] - recBuf[j * 7 + 1]) / (r);
					axyz[i][1] = axyz[i][1] + f * (s[i][1] - recBuf[j * 7 + 2]) / (r);
					axyz[i][2] = axyz[i][2] + f * (s[i][2] - recBuf[j * 7 + 3]) / (r);

				}
				axyz[i][0] = -axyz[i][0] / m[i];
				axyz[i][1] = -axyz[i][1] / m[i];
				axyz[i][2] = -axyz[i][2] / m[i];
			}
		}

		//final updates on bodies
		for (i = 0; i < size; ++i) {
			v[i][0] = v[i][0] + timestep * axyz[i][0];
			v[i][1] = v[i][1] + timestep * axyz[i][1];
			v[i][2] = v[i][2] + timestep * axyz[i][2];
			s[i][0] = s[i][0] + timestep * v[i][0];
			s[i][1] = s[i][1] + timestep * v[i][1];
			s[i][2] = s[i][2] + timestep * v[i][2];
		}

	}

	// This is an example of printing the body parameters to the stderr. Your code should print out the final body parameters
	// in the exact order as the input file. Since we are writing to the stderr in this case, rather than the stdout, make
	// sure you dont add extra debugging statements in stderr.

	if (myrank == 0) {
		for (i = 0; i < n / nprocs; i++) {
			fprintf(stderr, OUTPUT_BODY, s[i][0], s[i][1], s[i][2], v[i][0], v[i][1], v[i][2], m[i]);
		}
	}

	for (i = 0; i < size; i++) {
		free(axyz[i]);
	}
	free(axyz);
	free(sendBuf);
	free(recBuf);
}

