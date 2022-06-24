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
