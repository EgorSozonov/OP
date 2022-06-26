#include "Lexer.h"
#include "../utils/String.h"
#include "Expr.h"

LexResult lexicallyAnalyze(String* inp) {
    String *errMsg = NULL;


    if (inp == NULL || inp->length < 1) return (LexResult){ .result = NULL, .errMsg = errMsg};


    Expr tmp = (Expr){.tag = IntToken, .content = {.intToken = 5}};
    return (LexResult){ .result = NULL, .errMsg = errMsg};
}
