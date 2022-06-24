#include "Expr.h"


struct Expr {
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
        struct AST_NUMBER {
            int number;
        } AST_NUMBER;
        struct AST_ADD {
            Expr *left;
            Expr *right;
        } AST_ADD;
        struct AST_MUL {
            Expr *left;
            Expr *right;
        } AST_MUL;
    } content;
};

enum LexicalContext {
    statement,
    dataInitializer,
    curlyBraces,
    parens,
};

/**
 * & + - / * ! ~ $ % ^ | > < ? =
 */
enum OperatorSymb {
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
    notASymb,
};
