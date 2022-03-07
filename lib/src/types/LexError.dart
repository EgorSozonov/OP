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

    @override
    String toString() {
        return "UnexpectedSymbolError: $val";
    }
}


class IntError extends LexError {
    String val = "";
    IntError(this.val);

    @override
    String toString() {
        return "IntError: $val";
    }
}


class FloatError extends LexError {
    String val = "";
    FloatError(this.val);

    @override
    String toString() {
        return "FloatError: $val";
    }
}


class WordError extends LexError {
    String val = "";
    WordError(this.val);

    @override
    String toString() {
        return "WordError: $val";
    }
}


class OperatorError extends LexError {
    String val = "";
    OperatorError(this.val);

    @override
    String toString() {
        return "OperatorError: $val";
    }
}


class BoolError extends LexError {
    String val = "";
    BoolError(this.val);
}
