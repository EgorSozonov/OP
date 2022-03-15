#![allow(non_snake_case)]
#![allow(non_camel_case_types)]

mod Lexer;
mod LexerTypes;
mod Parser;


fn main() {
    println!("hello world");
    Lexer::lexicallyAnalyze();
    let a = LexerTypes::IntToken(5);
}
