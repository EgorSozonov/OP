#![allow(non_snake_case)]
#![allow(non_camel_case_types)]

mod Lexer;
mod LexerTypes;
mod Parser;

use LexerTypes::*;

fn main() {
    println!("hello world");
    Lexer::lexicallyAnalyze();
    let a = LexerTypes::Expr::intToken(5);
    let s: [u8; 3] = [1, 2, 3];
    let b = LexerTypes::Expr::wordToken(&s);
    let c = LexerTypes::Expr::wordToken(&s);

    let testExpr = Expr::listExpr(ListExpr {
        pType: ExprLexicalType::statement, val: Box::new(vec![Expr::intToken(6), Expr::wordToken(&[1, 2, 3, 4])])
    });

    println!("start");
    println!("{}", testExpr);
    println!("end");
}
