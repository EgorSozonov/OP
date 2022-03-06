class LexError {}

class EndOfInputError extends LexError {
}

class ExtraClosingCurlyBraceError extends LexError {
}

class ExtraClosingParenError extends LexError {
}

class ExtraClosingBracketError extends LexError {
}

class NonAsciiError extends LexError {
}

class EmptyStackError extends LexError {
}

class UnexpectedSymbolError extends LexError {
    String val = "";
    UnexpectedSymbolError(this.val);
}

class IntError extends LexError {
    String val = "";
    IntError(this.val);
}

class BoolError extends LexError {
    String val = "";
    BoolError(this.val);
}
