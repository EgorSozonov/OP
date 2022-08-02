#ifndef FILE_READER_H
#define FILE_READER_H
#include "../utils/Arena.h"
#include "../utils/String.h"
#include "../types/Types.h"


typedef struct {
    StackInstr* opcodes;
    String* errMsg;
} BytecodeRead;

BytecodeRead readBytecode(char* fName, Arena* ar);


#endif
