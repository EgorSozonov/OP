pub enum Expr<'a> {
    intToken(i64),
    floatToken(f64),
    wordToken(&'a[u8]),
    stringToken(&'a[u8]),
    commentToken(&'a[u8]),
    operatorToken([Option<OperatorSymb>; 3]),
    listExpr(ListExpr<'a>),
}

struct ListExpr<'a> {
    val: Box<Vec<Expr<'a>>>,
    pType: ExprLexicalType,
}

use Expr::*;

impl<'a, 'b> PartialEq for Expr<'b> {
    fn eq(&self, other: &Self) -> bool {
        match(self, other) {
            (intToken(a), intToken(b)) => a == b,
            (floatToken(a), floatToken(b)) => a == b,
            (wordToken(a), wordToken(b)) => a == b,
            (stringToken(a), stringToken(b)) => a == b,
            (commentToken(a), commentToken(b)) => a == b,
            (operatorToken(a), operatorToken(b)) => {
                let mut i = 0;
                while i < a.len() {
                    match (a[i], b[i]) {
                        (Some(oper1), Some(oper2)) => if oper1 != oper2 { return false },
                        (None, None) => (),
                        _ => return false,
                    }
                    i += 1;
                }
                return true;
            },
            (listExpr(a), listExpr(b)) => {
                let mut backtrackA : Vec<(&ListExpr, usize)> = Vec::new();
                let mut backtrackB : Vec<&ListExpr> = Vec::new();
                backtrackA.push((a, 0));
                backtrackB.push(b);
                let mut i: usize;
                while backtrackA.last().is_some() {
                    if backtrackB.last().is_none() { return false }
                    let mut listA: (&ListExpr, usize) = backtrackA.last().unwrap().clone();
                    let mut listB: &ListExpr = backtrackB.last().unwrap();
                    i = listA.1;
                    if listA.0.pType != listB.pType || listA.0.val.len() != listB.val.len() {
                        return false;
                    }
                    while i < listA.0.val.len() {
                        let itmA = &listA.0.val[i];
                        let itmB = &listB.val[i];
                        match (itmA, itmB) {
                            (listExpr(itmAList), listExpr(itmBList)) => {
                                backtrackA.push((listA.0, i + 1));
                                backtrackB.push(listB);
                                listA = (&itmAList, 0usize);
                                listB = &itmBList;
                                if listA.0.pType != listB.pType || listA.0.val.len() != listB.val.len() {
                                    return false;
                                }
                                i = 0;
                            },
                            _ => {
                                if itmA != itmB { return false }
                                i += 1
                            },
                        }
                    }
                }
                true
            },
            _ => false,
        }
    }
}

impl<'a> Eq for Expr<'a> {}


#[derive(PartialEq, Clone, Copy)]
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


#[derive(PartialEq, Eq, Clone, Copy)]
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
    unexpectedSymbol(String),
    intError(String),
    floatError(String),
    wordError(String),
    operatorError(String),
    boolError(String),
}
