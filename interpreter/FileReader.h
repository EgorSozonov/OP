#ifndef FILE_READER_H
#define FILE_READER_H


#include "../utils/StackHeader.h"
#include "../utils/Arena.h"

enum OpCode {

};

typedef struct {
    enum OpCode opCode;
    int r1;
    int r2;
    int r2;
} Instr;

DEFINE_STACK_HEADER(Instr)


#endif
