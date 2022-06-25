#include "Expr.h"

enum LexicalContext {
    statement,
    dataInitializer,
    curlyBraces,
    parens,
};

struct ListExpr {
    enum LexicalContext lType;
    VectorExpr content;

};

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
        struct ListExpr {
            int number;
        } ListExpr;
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
