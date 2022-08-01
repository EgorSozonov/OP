#include "Runtime.h"


typedef struct {
    String* name;
    Instr instructions[];
} OFunction;


typedef struct {
    enum ConstType;
    union {
        int num;
        String* str;
    };
} OConstant;

typedef struct {
    OConstant* constants;
    OFunction* functions;
    OFunction* entryPoint;
} OPackage;
