
#include <stdio.h>
#include <stdlib.h>
//#include <strings.h>
//#include <stdlib.h>
#include "functions.h"

extern InstInfo nop;
extern InstInfo fInst;
extern InstInfo dInst;
extern InstInfo exInst;
extern InstInfo memInst;
extern InstInfo wbInst;
extern int stall;
extern int instnum;

extern void printP2(InstInfo *fetchInst, InstInfo *decodeInst, InstInfo *executeInst, \
	InstInfo *memoryInst, InstInfo *writebackInst, int cycle);

int main(int argc, char *argv[])
{	
	//initialize the "nop" value
	nop.aluout = 0;
	nop.destdata = 0;
	nop.destreg = -1;
	nop.fields.func = 0;
	nop.fields.imm = 0;
	nop.fields.op = 0;
	nop.fields.rd = 0;
	nop.fields.rs = 0;
	nop.fields.rt = 0;
	nop.input1 = 0;
	nop.input2 = 0;
	nop.inst = 0;
	nop.memout = 0;
	nop.pc = 0;
	nop.s1data = 0;
	nop.s2data = 0;
	nop.sourcereg = 0;
	nop.targetreg = 0;
	nop.string[0] = '\0';
	nop.signals.aluop = 0;
	nop.signals.asrc = 0;
	nop.signals.btype = 0;
	nop.signals.mr = 0;
	nop.signals.mtr = 0;
	nop.signals.mw = 0;
	nop.signals.rdst = 0;
	nop.signals.rw = 0;

	InstInfo curInst;
	InstInfo *instPtr = &curInst;
	fInst = curInst;
	dInst = nop;
	exInst = nop;
	memInst = nop;
	wbInst = nop;
	stall = 0;

	instnum = 0;
	int cyclenum = 0;
	int maxpc;
	FILE *program;
	if (argc != 2)
	{
		printf("Usage: sim filename\n");
		exit(0);
	}

	maxpc = load(argv[1]);
	//printLoad(maxpc--);

	//while pipeline is not empty and still has instructions to read
	while (pc < maxpc || fInst.string[0] != '\0' || dInst.string[0] != '\0' || \
		exInst.string[0] != '\0' || memInst.string[0] != '\0' || wbInst.string[0] != '\0')
	{
		//hazard detection function here
		//hazards to detect (all RAW):
			//between wb and mem
			//between ex and wb 
			//between ex and mem
			//dont care if decode got the wrong data, since we switch between it
		//necessary forwards:
			//memout or aluout from previous mem stage (should be in wb now) to ex
			//aluout to ex (should be from current mem to ex)
			//wb mem to ex (???) shouldn't need this if wb is done before decode
			//stall if necessary to forward memout to ex


		if (exInst.string[0] != '\0') {
			//first input (jal only instruction without first input)
			if (exInst.fields.op != 0x08) {
				if (wbInst.signals.rw) {
					if (exInst.input1 == wbInst.destreg) {
						if (!wbInst.signals.mr) {
							if (wbInst.fields.op == 0x08) { //jal case
								exInst.s1data = pc;
							}
							else { //all other ALU cases
								exInst.s1data = wbInst.aluout;
							}
						}
						else { //lw case
							exInst.s1data = wbInst.memout;
						}
					}
				}
				if (memInst.signals.rw) {
					if (exInst.input1 == memInst.destreg) {
						if (memInst.signals.mr) { //stall on memread hazard
							fInst = dInst;
							dInst = exInst;
							exInst = nop;
							stall = 1;
						}
						else {
							exInst.s1data = memInst.aluout;
						}
					}
				}

			}
			//second input
			//no second input on addi, lw, beq, sw, jr, or jal. Also make sure input1 wasn't a stall
			//NOTE: sw data forwarding for third input is done separately in the mem function
			if (exInst.string[0] != '\0' && exInst.fields.op != 0x31 && exInst.fields.op != 0x11 && \
				exInst.fields.op != 0x12&& exInst.fields.op != 0x0a && exInst.fields.op != 0x25 && exInst.fields.op != 0x08) {
				if (wbInst.signals.rw) {
					if (exInst.input2 == wbInst.destreg) {
						if (!wbInst.signals.mr) {
							if (wbInst.fields.op == 0x08) { //jal case
								exInst.s2data = pc;
							}
							else { //all other ALU cases
								exInst.s2data = wbInst.aluout;
							}
						}
						else { //lw case
							exInst.s2data = wbInst.memout;
						}
					}
				}
				if (memInst.signals.rw) {
					if (exInst.input2 == memInst.destreg) {
						if (memInst.signals.mr) { //stall on memread hazard
							fInst = dInst;
							dInst = exInst;
							exInst = nop;
							stall = 1;
						}
						else {
							exInst.s2data = memInst.aluout;
						}
					}
				}
			}
		}

		writeback(&wbInst); //need to do register writes before register reads
		//used for printing results
		InstInfo fInstCopy;
		InstInfo dInstCopy;

		if (stall == 1) { //don't fetch on a stall cycle - instead hold the instruction
			stall = 0;
		}
		else {
			if (pc < maxpc) { //only fetch if program counter is within bounds
				fetch(&fInst);
				++instnum;
			}
			else fInst = nop;
		}
		fInstCopy = fInst;
		dInstCopy = dInst;
		decode(&dInst); //branch logic after fetch
		execute(&exInst);
		memory(&memInst);

		printP2(&fInstCopy, &dInstCopy, &exInst, &memInst, &wbInst ,cyclenum++);
		wbInst = memInst;
		memInst = exInst;
		exInst = dInst;
		dInst = fInst;
	}

	printf("Cycles: %d\n", cyclenum);
	printf("Instructions Executed: %d\n", instnum);

	exit(0);
}

/* print
 *
 * prints out the state of the simulator after each instruction
 */
void print(InstInfo *inst, int count)
{
	int i, j;
	printf("Instruction %d: %d\n",count,inst->inst);
	printf("%s\n",inst->string);
	printf("Fields: {rd: %d, rs: %d, rt: %d, imm: %d}\n",
		inst->fields.rd, inst->fields.rs, inst->fields.rt, inst->fields.imm);
	printf("Control Bits:\n{alu: %d, mw: %d, mr: %d, mtr: %d, asrc: %d, bt: %d, rdst: %d, rw: %d}\n",
		inst->signals.aluop, inst->signals.mw, inst->signals.mr, inst->signals.mtr, inst->signals.asrc,
		inst->signals.btype, inst->signals.rdst, inst->signals.rw);
	printf("ALU Result: %d\n",inst->aluout);
	if (inst->signals.mr)
		printf("Mem Result: %d\n",inst->memout);
	else
		printf("Mem Result: X\n");
	for(i=0;i<8;i++)
	{
		for(j=0;j<32;j+=8)
			printf("$%d: %4d ",i+j,regfile[i+j]);
		printf("\n");
	}
	printf("\n");
}
/*
 * print out the loaded instructions.  This is to verify your loader
 * works.
 */
void printLoad(int max)
{
	int i;
	for(i=0;i<max;i++)
		printf("%d\n",instmem[i]);
}
