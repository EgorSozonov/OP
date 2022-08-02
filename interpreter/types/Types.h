#ifndef TYPES_H
#define TYPES_H
#include "../utils/StackHeader.h"
#include "../utils/Arena.h"


enum OpCode {
    OP_ADD,
    OP_SUB,
    OP_MUL,
    OP_DIV,
    OP_LOADI,
    OP_PRINT,
    OP_READGLOB,
    OP_WRITEGLOB,
    OP_CALL,
    OP_MOVE,
};

typedef struct {
    enum OpCode opCode;
    int r1;
    int r2;
    int r3;
} Instr;

DEFINE_STACK_HEADER(Instr)




#endif
