#include "Lexer.h"
#include "../utils/String.h"
#include "Expr.h"

typedef struct {
    Expr* result;
    String *errMsg;
} LexResult;
