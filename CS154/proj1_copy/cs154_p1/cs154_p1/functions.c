
#include <stdio.h>
#include <stdlib.h>
//#include <strings.h>
//#include <stdlib.h>
#include "functions.h"

#define BUFFERSIZE 64

// these are the structures used in this simulator


// global variables
// register file
int regfile[32];
// instruction memory
int instmem[100];  // only support 100 static instructions
// data memory
int datamem[1024];
// program counter
int pc;

// these are the different functions you need to write
int load(char *filename);
void fetch(InstInfo *);
void decode(InstInfo *);
void execute(InstInfo *);
void memory(InstInfo *);
void writeback(InstInfo *);

/* load
 *
 * Given the filename, which is a text file that 
 * contains the instructions stored as integers 
 *
 * You will need to load it into your global structure that 
 * stores all of the instructions.
 *
 * The return value is the maxpc - the number of instructions
 * in the file
 */
int load(char *filename)
{
	FILE* file = fopen(filename, "r");
	const size_t buffersize = BUFFERSIZE;
	char line[BUFFERSIZE];
	int linenum = 0;
	
	while ( fgets(line, buffersize, file) != NULL) {
		instmem[linenum++] = atoi(line);		
	}

	fclose(file);
	return linenum;
}

/* fetch
 *
 * This fetches the next instruction and updates the program counter.
 * "fetching" means filling in the inst field of the instruction.
 */
void fetch(InstInfo *instruction)
{
	instruction->inst = instmem[pc];
	instruction->pc = pc++;	
}

/* decode
 *
 * This decodes an instruction.  It looks at the inst field of the 
 * instruction.  Then it decodes the fields into the fields data 
 * member.  The first one is given to you.
 *
 * Then it checks the op code.  Depending on what the opcode is, it
 * fills in all of the signals for that instruction.
 */
void decode(InstInfo *instruction)
{
	// fill in the signals and fields
	int val = instruction->inst;
	int op, func;
	op = (val >> 26) & 0x03f;
	func = val & 0x03f;
	instruction->fields.op = (val >> 26) & 0x03f;
	instruction->fields.func = func;
	// fill in the rest of the fields here
	instruction->fields.rs = (val >> 21) & 0x01f;
	instruction->fields.rt = (val >> 16) & 0x01f;
	instruction->fields.rd = (val >> 11) & 0x01f;
	instruction->fields.imm = ((val & 0xffff) << 16) >> 16;

	// now fill in the signals

	// if it is an add (example only, please modify the code accordingly!)
	if (op == 0x20 && func == 0x20) {
		//instruction->fields.imm = -1;
		instruction->signals.aluop = 2;
		instruction->signals.mw = 0;
		instruction->signals.mr = 0;
		instruction->signals.mtr = 0;
		instruction->signals.asrc = 0;
		instruction->signals.btype = 0;
		instruction->signals.rdst = 1;
		instruction->signals.rw = 1;
		sprintf(instruction->string, "add $%d, $%d, $%d",
			instruction->fields.rd, instruction->fields.rs,
			instruction->fields.rt);
		instruction->destreg = instruction->fields.rd;
	}
	//and
	else if (op == 0x20 && func == 0x24) {
		//instruction->fields.imm = -1;
		instruction->signals.aluop = 0;
		instruction->signals.mw = 0;
		instruction->signals.mr = 0;
		instruction->signals.mtr = 0;
		instruction->signals.asrc = 0;
		instruction->signals.btype = 0;
		instruction->signals.rdst = 1;
		instruction->signals.rw = 1;
		sprintf(instruction->string, "and $%d, $%d, $%d",
			instruction->fields.rd, instruction->fields.rs,
			instruction->fields.rt);
		instruction->destreg = instruction->fields.rd;
	}
	//sub
	else if (op == 0x20 && func == 0x28) {
		//instruction->fields.imm = -1;
		instruction->signals.aluop = 3;
		instruction->signals.mw = 0;
		instruction->signals.mr = 0;
		instruction->signals.mtr = 0;
		instruction->signals.asrc = 0;
		instruction->signals.btype = 0;
		instruction->signals.rdst = 1;
		instruction->signals.rw = 1;
		sprintf(instruction->string, "sub $%d, $%d, $%d",
			instruction->fields.rd, instruction->fields.rs,
			instruction->fields.rt);
		instruction->destreg = instruction->fields.rd;
	}
	//sgt
	else if (op == 0x20 && func == 0x30) {
		//instruction->fields.imm = -1;
		instruction->signals.aluop = 7;
		instruction->signals.mw = 0;
		instruction->signals.mr = 0;
		instruction->signals.mtr = 0;
		instruction->signals.asrc = 0;
		instruction->signals.btype = 0;
		instruction->signals.rdst = 1;
		instruction->signals.rw = 1;
		sprintf(instruction->string, "sgt $%d, $%d, $%d",
			instruction->fields.rd, instruction->fields.rs,
			instruction->fields.rt);
		instruction->destreg = instruction->fields.rd;
	}
	//addi
	else if (op == 0x31) {
		//instruction->fields.rd = -1;
		//instruction->fields.func = -1;
		instruction->signals.aluop = 2;
		instruction->signals.mw = 0;
		instruction->signals.mr = 0;
		instruction->signals.mtr = 0;
		instruction->signals.asrc = 1;
		instruction->signals.btype = 0;
		instruction->signals.rdst = 0;
		instruction->signals.rw = 1;
		sprintf(instruction->string, "addi $%d, $%d, %d",
			instruction->fields.rt, instruction->fields.rs,
			instruction->fields.imm);
		instruction->destreg = instruction->fields.rt;
	}
	//lw
	else if (op == 0x11) {
		//instruction->fields.rd = -1;
		//instruction->fields.func = -1;
		instruction->signals.aluop = 2;
		instruction->signals.mw = 0;
		instruction->signals.mr = 1;
		instruction->signals.mtr = 1;
		instruction->signals.asrc = 1;
		instruction->signals.btype = 0;
		instruction->signals.rdst = 0;
		instruction->signals.rw = 1;
		sprintf(instruction->string, "lw $%d, %d($%d)",
			instruction->fields.rt, instruction->fields.imm,
			instruction->fields.rs);
		instruction->destreg = instruction->fields.rt;
	}
	//sw
	else if (op == 0x12) {
		//instruction->fields.rd = -1;
		//instruction->fields.func = -1;
		instruction->signals.aluop = 2;
		instruction->signals.mw = 1;
		instruction->signals.mr = 0;
		instruction->signals.mtr = -1;
		instruction->signals.asrc = 1;
		instruction->signals.btype = 0;
		instruction->signals.rdst = -1;
		instruction->signals.rw = 0;
		sprintf(instruction->string, "sw $%d, %d($%d)",
			instruction->fields.rt, instruction->fields.imm,
			instruction->fields.rs);
		instruction->destreg = -1;
	}
	//beq
	else if (op == 0x0a) {
		//instruction->fields.rd = -1;
		//instruction->fields.func = -1;
		instruction->signals.aluop = 3;
		instruction->signals.mw = 0;
		instruction->signals.mr = 0;
		instruction->signals.mtr = -1;
		instruction->signals.asrc = 0; //why isn't this 1?
		instruction->signals.btype = 3;
		instruction->signals.rdst = -1;
		instruction->signals.rw = 0;
		sprintf(instruction->string, "beq $%d, $%d, %d",
			instruction->fields.rs, instruction->fields.rt,
			instruction->fields.imm);
		instruction->destreg = instruction->fields.rt;
	}
	//jr
	else if (op == 0x25) {
		//instruction->fields.imm = -1;
		instruction->signals.aluop = -1;
		instruction->signals.mw = 0;
		instruction->signals.mr = 0;
		instruction->signals.mtr = -1;
		instruction->signals.asrc = -1;
		instruction->signals.btype = 2;
		instruction->signals.rdst = -1;
		instruction->signals.rw = 0;
		sprintf(instruction->string, "jr $%d",
			instruction->fields.rs);
		instruction->destreg = instruction->fields.rd;
	}
	//jal
	else if (op == 0x08) {
		//instruction->fields.rd = -1;
		//instruction->fields.func = -1;
		//instruction->fields.rs = -1;
		//instruction->fields.rt = -1; 
		instruction->fields.imm = val & 0x3ffffff;
		instruction->signals.aluop = -1;
		instruction->signals.mw = 0;
		instruction->signals.mr = 0;
		instruction->signals.mtr = 2;
		instruction->signals.asrc = -1;
		instruction->signals.btype = 1;
		instruction->signals.rdst = 2;
		instruction->signals.rw = 1;
		sprintf(instruction->string, "jal %d",
			instruction->fields.imm);
		instruction->destreg = 31;
	}

	// fill in s1data and input2
	instruction->input1 = instruction->fields.rs;
	instruction->input2 = instruction->fields.rt;
	instruction->s1data = regfile[instruction->input1];
	if (instruction->signals.asrc) instruction->s2data = instruction->fields.imm;
	else instruction->s2data = regfile[instruction->input2];
}

/* execute
 *
 * This fills in the aluout value into the instruction and destdata
 */

void execute(InstInfo *instruction)
{
	switch (instruction->signals.aluop) {

	case 0: //and
		if (instruction->signals.asrc) {
			instruction->aluout = regfile[instruction->input1] & instruction->fields.imm;
		}
		else {
			instruction->aluout = regfile[instruction->input1] & regfile[instruction->input2];
		}
		break;
	case 1: //not
		instruction->aluout = ~regfile[instruction->input1];
		break;
	case 2: //add
		if (instruction->signals.asrc) {
			instruction->aluout = regfile[instruction->input1] + instruction->fields.imm;
		}
		else {
			instruction->aluout = regfile[instruction->input1] + regfile[instruction->input2];
		}
		break;
	case 3: //sub
		if (instruction->signals.asrc) {
			instruction->aluout = regfile[instruction->input1] - instruction->fields.imm;
		}
		else {
			instruction->aluout = regfile[instruction->input1] - regfile[instruction->input2];
		}
		break;
	case 4: //xor
		if (instruction->signals.asrc) {
			instruction->aluout = regfile[instruction->input1] ^ instruction->fields.imm;
		}
		else {
			instruction->aluout = regfile[instruction->input1] ^ regfile[instruction->input2];
		}
		break;
	case 5: //or
		if (instruction->signals.asrc) {
			instruction->aluout = regfile[instruction->input1] | instruction->fields.imm;
		}
		else {
			instruction->aluout = regfile[instruction->input1] | regfile[instruction->input2];
		}
		break;
	case 6: //slt
		if (regfile[instruction->input1] < regfile[instruction->input2]) {
			instruction->aluout = 1;
		}
		else {
			instruction->aluout = 0;
		}
		break;
	case 7: //sgt
		if (regfile[instruction->input1] > regfile[instruction->input2]) {
			instruction->aluout = 1;
		}
		else {
			instruction->aluout = 0;
		}
		break;
	default:
		instruction->aluout = -1;
	}

}

/* memory
 *
 * If this is a load or a store, perform the memory operation
 */
void memory(InstInfo *instruction)
{
	//lw
	if (instruction->fields.op == 0x11) {
		instruction->memout = datamem[instruction->aluout / 4];
	} //sw
	else if (instruction->fields.op == 0x12) {
		datamem[instruction->aluout / 4] = regfile[instruction->fields.rt];
	}
}

/* writeback
 *
 * If a register file is supposed to be written, write to it now
 */
void writeback(InstInfo *instruction)
{
	if (instruction->signals.rw) {
		switch (instruction->signals.rdst) {
		case 0: //use rt
			switch (instruction->signals.mtr) {
			case 0: //choose ALU output
				regfile[instruction->fields.rt] = instruction->aluout;
				break;
			case 1: //choose memory output
				regfile[instruction->fields.rt] = instruction->memout;
				break;
			case 2: //choose incremented pc
				regfile[instruction->fields.rt] = pc;
				break;
			} 
			break;
		case 1: //use rd
			switch (instruction->signals.mtr) {
			case 0: //choose ALU output
				regfile[instruction->fields.rd] = instruction->aluout;
				break;
			case 1: //choose memory output
				regfile[instruction->fields.rd] = instruction->memout;
				break;
			case 2: //choose incremented pc
				regfile[instruction->fields.rd] = pc;
				break;
			}
			break;
		case 2: //use reg 31 ($ra)
			switch (instruction->signals.mtr) {
			case 0: //choose ALU output
				regfile[31] = instruction->aluout;
				break;
			case 1: //choose memory output
				regfile[31] = instruction->memout;
				break;
			case 2: //choose incremented pc
				regfile[31] = pc;
				break;
			}
			break;
		}
	}
}

/* setPCWithInfo
 *
 * branch instruction will overwrite PC
*/
void setPCWithInfo( InstInfo *instruction) {
	switch (instruction->signals.btype) {
	case 0: //no branch
		return;
	case 1: //jump to immediate concatenated with top 4 bits of PC (NOT PC+1!)
		pc = ((pc - 1) & 0xf0000000) + instruction->fields.imm;
		break;
	case 2: //jump to register-stored address
		pc = regfile[instruction->input1];
		break;
	case 3: //conditional jump
		if (instruction->aluout == 0) {
			pc = pc + instruction->fields.imm;
		}
		break;
	}
}
