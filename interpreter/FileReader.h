#ifndef FILE_READER_H
#define FILE_READER_H


#include "../utils/StackHeader.h"
#include "../utils/Arena.h"
#include "../utils/String.h"
#include "../utils/Stack.h"

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


typedef struct {
    StackInstr* opcodes;
    String* errMsg;
} BytecodeRead;

BytecodeRead readBytecode(char* fName, Arena* ar);
#endif
