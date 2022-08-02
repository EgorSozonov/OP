#ifndef RUNTIME_H
#define RUNTIME_H
#include "../utils/String.h"
#include "../utils/BoxedStackHeader.h"
#include "../types/Types.h"


typedef enum {
    intt,
    strr,
} ConstType;

typedef struct {
    ConstType tag;
    union {
        int num;
        String* str;
    } value;
} OConstant;
DEFINE_BOXED_STACK_HEADER(OConstant)

typedef struct {
    String* name;
    StackInstr* instructions;
} OFunction;
DEFINE_BOXED_STACK_HEADER(OFunction)

typedef struct {
    BoxedStackOConstant* constants;
    BoxedStackOFunction* functions;
    OFunction* entryPoint;
} OPackage;

OPackage makeForTest(Arena* ar);

int runPackage(OPackage package);


#endif
