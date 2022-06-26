#ifndef EXPR_H
#define EXPR_H


#include "../utils/StackHeader.h"


enum LexicalContext {
    statement,
    dataInitializer,
    curlyBraces,
    parens,
};

typedef struct Expr Expr;

/**
 * & + - / * ! ~ $ % ^ | > < ? =
 */
typedef enum  {
    notASymb = 0,
    ampersand,
    plus,
    minus,
    slash,
    asterisk,
    exclamation,
    tilde,
    dollar,
    percent,
    caret,
    pipe,
    lt,
    gt,
    question,
    equals,
    colon,
} OperatorSymb;

DEFINE_STACK_HEADER(Expr)


typedef struct  {
    enum LexicalContext lType;
    StackExpr content;
} ListExpr;

typedef struct {
    long content;
} IntToken;

typedef struct {
    double content;
} FloatToken;

typedef struct {
    String* content;
    String* capitalizedPrefix;
} WordToken;

typedef struct {
    String* content;
    String* capitalizedPrefix;
} DotWordToken;

typedef struct {
    OperatorSymb content[3];
} OperatorToken;

typedef struct {
    String* content;
} StringToken;

typedef struct {
    String* content;
} CommentToken;


typedef struct {
    enum {
        ListExpr,
        IntToken,
        FloatToken,
        WordToken,
        DotWordToken,
        OperatorToken,
        StringToken,
        CommentToken,
    } tag;
    union {
        ListExpr listExpr;
        IntToken intToken;
        FloatToken floatToken;
        WordToken wordToken;
        DotWordToken dotWordToken;
        OperatorToken operatorToken;
        StringToken stringToken;
        CommentToken commentToken;
    } content;
} Expr;

typedef struct {
    Expr expr;
    int ind;
} ExprInd;

#endif
