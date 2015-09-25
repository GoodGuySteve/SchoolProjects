/*
validate.cpp - Test the result for selected test cases.
*/

#include<stdio.h>
#include<string.h>
#include<stdlib.h>

int main(int argc, char **argv)
{

	if(argc < 4)
	{
		printf("Usage : ./validate <Array Size> <Number of iterations> <Test case>\n");
		exit(0);
	}

	int n = atoi(argv[1]);
	unsigned int iter = atoi(argv[2]);
	int which = atoi(argv[3]);

	if(((n%10) != 0) && (iter != 5*n))
	{
		printf("The test cases work for n in multiples of 10, and iter = 5*n\n");
		exit(0);
	}


	int livecount[10];

    FILE *fp = fopen(argv[4], "r");
	for(int i = 0; i < 10; i++)
	{
	    fscanf(fp, "%d", &livecount[i]);
	}
    fclose(fp);
	if(which == 0)
	{
		for(int i = 0; i < 10; i++)
		{
			if(livecount[i])
			{
				printf("Result did not match\n");
				return -1;
			}
		}
	}
	else if(which == 1)
	{
		int sum = 0;

		int index = 0;

		switch(n)
		{
			case 10:
				index = 0;
				break;
			case 20:
				index = 1;
				break;
			case 100:
				index = 2;
				break;
			case 200:
				index = 3;
				break;
			default:
				printf("Only 4 values of n allowed for Test Case 1 - (10,20,100,200)\n");
				break;
		}
		int checkarr[][10] = {{ 13,19,21,6,5,5,5,5,5,5 },
				      {19, 35, 66, 70, 49, 43, 60, 52, 36, 31},
				      {55, 72, 95, 94,118, 82,204,121,143,142},
				      {72, 94, 82,121,142,154,185,206,216,185}};

		for(int i = 0; i < 10; i++)
		{
			sum += (livecount[i] == checkarr[index][i]);
		}

		if(sum != 10)
		{
			printf("Result did not match\n");
			return -1;
		}
	}
	else if(which == 2)
	{
		for(int i = 0; i < 10; i++)
		{
			if(livecount[i] != 5)
			{
				printf("Result did not match\n");
				return -1;
			}
		}

	}

	printf("Result matched\n");
	return 1;
}
