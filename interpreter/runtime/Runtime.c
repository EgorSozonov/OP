#include "Runtime.h"
#include "../utils/Arena.h"
#include "../utils/String.h"


OPackage* makeForTest(Arena* ar) {
    OConstant* stringConst = arenaAllocate(ar, sizeof(OConstant));
    stringConst->tag = ConstType.strr;
    stringConst->value = allocateString(ar, 11, "Hello world");
    OConstant* intConst = arenaAllocate(ar, sizeof(OConstant));
    intConst->tag = ConstType.intt;
    intConst->value = 5;

    OConstant** constants = arenaAllocate(ar, sizeof(OConstant)*2);
    constants[0] = stringConst;
    constants[1] = intConst;

    OFunction* entryPoint = arenaAllocate(ar, sizeof(OFunction));
    entryPoint.name = allocateString(ar, 3, "main");
    Instr* instructionsMain = arenaAllocate(ar, sizeof(Instr)*3);
    instructionsMain[0] = {.opCode = OP_PRINT, .r1 = 0};
    instructionsMain[1] = {.opCode = OP_PRINT, .r2 = 0};
    instructionsMain[2] = {.opCode = OP_PRINT, .r1 = 11};
    entryPoint->instructions = instructrionsMain;

    OPackage* package = arenaAllocate(ar, sizeof(OPackage));
    package.constants = constants;
    return package;
}


int run(OPackage package) {
    if (package.entryPoint == NULL) return 0;


    return 0;
}
