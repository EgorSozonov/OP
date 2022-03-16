use std::fmt;


pub enum Expr<'a> {
    intToken(i64),
    floatToken(f64),
    wordToken(&'a[u8]),
    stringToken(&'a[u8]),
    commentToken(&'a[u8]),
    operatorToken([Option<OperatorSymb>; 3]),
    listExpr(ListExpr<'a>),
}

pub struct ListExpr<'a> {
    pub val: Box<Vec<Expr<'a>>>,
    pub pType: ExprLexicalType,
}

use Expr::*;
use ExprLexicalType::*;

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

impl<'a> fmt::Display for Expr<'a> {
    fn fmt(&self, out: &mut fmt::Formatter) -> fmt::Result {
        match self {
            listExpr(le) => {
                println!("listExpr");
                if le.val.last().is_none() {
                    out.write_str("Empty ListExpr");
                    return Ok(());
                }
                let mut backtrack: Vec<(&ListExpr, usize)> = Vec::new();
                let mut curr = le;
                let mut i: usize;
                loop {
                    while i < curr.val.len() {
                        match curr.val[i] {
                            listExpr(x) => {
                                if x.val.len() > 0 {
                                    backtrack.push((curr, i));
                                    curr = listElem;
                                    i = 0;
                                    out.write_str(match x.pType {
                                        curlyBraces => " { ",
                                        statement => " | ",
                                        dataInitializer => " [ ",
                                        parens => " ( ",
                                    });
                                } else {
                                    out.write_str(&format!("!!empty {:?}!! ", x.pType));
                                    i += 1;
                                }
                            },
                            _ => {
                                out.write_str("other");
                            },
                        }
                    }
                    if backtrack.last().is_some() {
                        out.write_str(match curr.pType {
                            curlyBraces => " }, ",
                            statement => " |, ",
                            dataInitializer => " ], ",
                            parens => " ), ",
                        });
                        let back = backtrack.pop().unwrap();
                        curr = back.0;
                        i = back.1 + 1;
                    }
                    if backtrack.last().is_none() && i >= curr.val.len() { break; }
                }
            },

            intToken(x) => {
                println!("intToken");
                out.write_str(&format!("Int {}", x));
            },
            floatToken(x) => {
                println!("floatToken");
                out.write_str(&format!("Float {}", x));
            },
            operatorToken(x) => {
                println!("operatorToken");
                out.write_str(&"Operator [");
                for v in x {
                    match v {
                        Some(op) => {
                            out.write_str(&format!("{} ", op));
                        },
                        None => break,
                    }
                }
                out.write_str("]");
            },
            wordToken(x) => { println!("wordToken"); out.write_str(&std::str::from_utf8(x).unwrap()); },
            stringToken(x) => { println!("stringToken"); out.write_str(&std::str::from_utf8(x).unwrap()); },
            commentToken(x) => { println!("commentToken"); out.write_str(&std::str::from_utf8(x).unwrap()); },
        }
        Ok(())
    }
}



#[derive(PartialEq, Clone, Copy, Debug)]
/// & + - / * ! ~ $ % ^ | > < ? =
pub enum OperatorSymb {
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


impl fmt::Display for OperatorSymb {
    fn fmt(&self, out: &mut fmt::Formatter) -> fmt::Result {
        let name = format!("{:?}", self);
        out.write_str(&name);
        Ok(())
    }
}


#[derive(PartialEq, Eq, Clone, Copy, Debug)]
pub enum ExprLexicalType {
    statement,
    dataInitializer,
    curlyBraces,
    parens,
}


pub enum LexError {
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
