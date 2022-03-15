pub enum Expr<'a> {
    intToken(i64),
    floatToken(f64),
    wordToken(&'a[u8]),
    stringToken(&'a[u8]),
    commentToken(&'a[u8]),
    operatorToken([Option<OperatorSymb>; 3]),
    listExpr(Box<Expr<'a>>),
}


impl<'a, 'b> PartialEq for Expr<'b> {
    fn eq(&self, other: &Self) -> bool {
        match(self, other) {
            (Expr::intToken(a), Expr::intToken(b)) => a == b,
            (Expr::floatToken(a), Expr::floatToken(b)) => a == b,
            (Expr::wordToken(a), Expr::wordToken(b)) => a == b,
            (Expr::stringToken(a), Expr::stringToken(b)) => a == b,
            (Expr::commentToken(a), Expr::commentToken(b)) => a == b,
            (Expr::operatorToken(a), Expr::operatorToken(b)) => {
                let mut i = 0;
                while i < a.len() {
                    match (a[i], b[i]) {
                        (Some(oper1), Some(oper2)) => if oper1 != oper2 { return false },
                        (None, None) => (),
                        _ => return false;
                    }
                    i += 1;
                }
                return true;
            },
            (Expr::listExpr(a), Expr::listExpr(b)) => {
                return true;
            },
        }
    }
}

impl<'a> Eq for Expr<'a> {}


#[derive(PartialEq, Copy)]
/// & + - / * ! ~ $ % ^ | > < ? =
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
}


#[derive(Eq, Copy)]
enum ExprLexicalType {
    statement,
    dataInitializer,
    curlyBraces,
    parens,
}


enum LexError {
    endOfInput,
    extraClosingCurlyBrace,
    extraClosingParen,
    extraClosingBracket,
    nonASCII,
    emptyStack,
    unexpectedSymbol(&str),
    intError(&str),
    floatError(&str),
    wordError(&str),
    operatorError(&str),
    boolError(&str),
}
