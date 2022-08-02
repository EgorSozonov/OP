#ifndef RUNTIME_H
#define RUNTIME_H
#include "../utils/String.h"
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

typedef struct {
    String* name;
    Instr* instructions;
} OFunction;




typedef struct {
    OConstant** constants;
    OFunction** functions;
    OFunction* entryPoint;
} OPackage;



#endif
