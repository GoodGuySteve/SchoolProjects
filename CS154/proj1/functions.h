
#ifndef FUNCTIONS_H
#define FUNCTIONS_H

// structures
typedef struct _signals{
        int aluop;
        int mw;
        int mr;
        int mtr;
        int asrc;
        int btype;
        int rdst;
        int rw;
} Signals;

/* these are the decoded fields for a single instruction */
typedef struct _fields{
        int rd;
        int rs;
        int rt;
        int imm;
        int op;
        int func;
} Fields;

/* this is all of the information for a single instruction */
typedef struct _instinfo{
        int inst;
        Signals signals;
        Fields fields;
        int pc;
        int aluout;
        int memout;
        int sourcereg;
        int targetreg;
        int destreg;
        int destdata;
        char string[30];
        int s1data;
        int s2data;
        int input1;
        int input2;
} InstInfo;

// You need to fill in
int load(char *filename);
void fetch(InstInfo *);
void decode(InstInfo *);
void execute(InstInfo *);
void memory(InstInfo *);
void writeback(InstInfo *);

// this function is provided for you
 void print(InstInfo *, int);
 void printLoad(int);

extern int pc;
extern int instmem[100];
extern int datamem[1024];
extern int regfile[32];

#endif

