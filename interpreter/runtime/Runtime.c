#include "Runtime.h"
#include "../utils/Arena.h"
#include "../utils/String.h"
#include "../utils/BoxedStack.h"
#include <stdio.h>
#include <string.h>


DEFINE_BOXED_STACK(OFunction);
DEFINE_BOXED_STACK(OConstant);


OPackage makeForTest(Arena* ar) {
    OConstant* stringConst = arenaAllocate(ar, sizeof(OConstant));
    stringConst->tag = strr;    
    stringConst->value.str = allocateString(ar, 11, "Hello world");
    OConstant* intConst = arenaAllocate(ar, sizeof(OConstant));
    intConst->tag = intt;
    intConst->value.num = 77;

    BoxedStackOConstant* constants = mkStackOConstant(ar, 2);
    pushBoxedOConstant(constants, stringConst);
    pushBoxedOConstant(constants, intConst);

    OFunction* entryPoint = arenaAllocate(ar, sizeof(OFunction));
    entryPoint->name = allocateString(ar, 3, "main");
    Instr* instructionsMain = arenaAllocate(ar, sizeof(Instr)*3);

    instructionsMain[0] =  (Instr){.opCode = OP_PRINT, .r2 = 0};
    instructionsMain[1] = (Instr){.opCode = OP_PRINT, .r2 = 1};
    instructionsMain[2] = (Instr){.opCode = OP_PRINT, .r1 = -5};
    entryPoint->instructions = instructionsMain;

    OPackage package = {.constants = constants, .functions = NULL, .entryPoint = entryPoint};
    return package;
}


int runPackage(OPackage package) {
    if (package.entryPoint == NULL) return -1;
    printf("Executing entrypoint...\n");


    return 0;
}


