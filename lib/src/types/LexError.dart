class LexError {}

class EndOfInputError extends LexError {
}

class IntError extends LexError {
    String val = "";
    IntError(this.val);
}

class BoolError extends LexError {
    String val = "";
    BoolError(this.val);
}
