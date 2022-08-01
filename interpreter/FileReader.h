#ifndef FILE_READER_H
#define FILE_READER_H


#include "../utils/StackHeader.h"
#include "../utils/Arena.h"
#include "../utils/String.h"
#include "../utils/Stack.h"


BytecodeRead readBytecode(char* fName, Arena* ar);
#endif
