#include "../utils/Arena.h"
#include "../utils/Stack.h"
#include "../utils/String.h"
#include "FileReader.h"
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

#include <unistd.h>



int main(int argc, char* argv[]) {
    printf("HW\n");

    char *buf;
    buf=(char *)malloc(100*sizeof(char));
    getcwd(buf,100);
    printf("\n %s \n",buf);

    Arena *ar = mkArena();
    BytecodeRead res = readBytecode("./bytecode.txt", ar);
    arenaDelete(ar);
    return 0;


// main <hw.lua:0,0> (12 instructions at 0x12c804110)
// 0+ params, 2 slots, 1 upvalue, 0 locals, 4 constants, 4 functions
// 	1	[1]	VARARGPREP	0
// 	2	[7]	CLOSURE  	0 0	; 0x12c8042c0
// 	3	[1]	SETTABUP 	0 0 0	; _ENV "sumUp"
// 	4	[12]	CLOSURE  	0 1	; 0x12c804950
// 	5	[10]	SETTABUP 	0 1 0	; _ENV "addOne"
// 	6	[16]	CLOSURE  	0 2	; 0x12c8049d0
// 	7	[14]	SETTABUP 	0 2 0	; _ENV "doubleAdd"
// 	8	[24]	CLOSURE  	0 3	; 0x12c8046b0
// 	9	[18]	SETTABUP 	0 3 0	; _ENV "main"
// 	10	[26]	GETTABUP 	0 0 3	; _ENV "main"
// 	11	[26]	CALL     	0 1 1	; 0 in 0 out
// 	12	[26]	RETURN   	0 1 1	; 0 out

// function <hw.lua:1,7> (12 instructions at 0x12c8042c0)
// 1 param, 9 slots, 1 upvalue, 8 locals, 1 constant, 0 functions
// 	1	[2]	LOADI    	1 0
// 	2	[3]	GETTABUP 	2 0 0	; _ENV "ipairs"
// 	3	[3]	MOVE     	3 0
// 	4	[3]	CALL     	2 2 5	; 1 in 4 out
// 	5	[3]	TFORPREP 	2 2	; to 8
// 	6	[4]	ADD      	1 1 7
// 	7	[4]	MMBIN    	1 7 6	; __add
// 	8	[3]	TFORCALL 	2 2
// 	9	[3]	TFORLOOP 	2 4	; to 6
// 	10	[5]	CLOSE    	2
// 	11	[6]	RETURN   	1 2 0	; 1 out
// 	12	[7]	RETURN   	2 1 0	; 0 out

// function <hw.lua:10,12> (4 instructions at 0x12c804950)
// 1 param, 2 slots, 0 upvalues, 1 local, 0 constants, 0 functions
// 	1	[11]	ADDI     	1 0 1
// 	2	[11]	MMBINI   	0 1 6 0	; __add
// 	3	[11]	RETURN1  	1
// 	4	[12]	RETURN0

// function <hw.lua:14,16> (6 instructions at 0x12c8049d0)
// 2 params, 3 slots, 0 upvalues, 2 locals, 1 constant, 0 functions
// 	1	[15]	ADD      	2 0 1
// 	2	[15]	MMBIN    	0 1 6	; __add
// 	3	[15]	MULK     	2 2 0	; 2
// 	4	[15]	MMBINK   	2 0 8 1	; __mul 2 flip
// 	5	[15]	RETURN1  	2
// 	6	[16]	RETURN0

// function <hw.lua:18,24> (13 instructions at 0x12c8046b0)
// 0 params, 6 slots, 1 upvalue, 4 locals, 3 constants, 0 functions
// 	1	[19]	LOADI    	0 0
// 	2	[20]	GETTABUP 	1 0 0	; _ENV "addOne"
// 	3	[20]	MOVE     	2 0
// 	4	[20]	CALL     	1 2 2	; 1 in 1 out
// 	5	[21]	LOADI    	2 150
// 	6	[22]	GETTABUP 	3 0 1	; _ENV "doubleAdd"
// 	7	[22]	MOVE     	4 1
// 	8	[22]	MOVE     	5 2
// 	9	[22]	CALL     	3 3 2	; 2 in 1 out
// 	10	[23]	GETTABUP 	4 0 2	; _ENV "print"
// 	11	[23]	MOVE     	5 3
// 	12	[23]	CALL     	4 2 1	; 1 in 0 out
// 	13	[24]	RETURN0

}
