#ifndef LEXER_H
#define LEXER_H


#include "../utils/EitherHeader.h"
#include <stdbool.h>

typedef struct {
    Expr* result;
    String *errMsg;
} LexResult;

DEFINE_EITHER_HEADER(String, ExprInd)


#endif
