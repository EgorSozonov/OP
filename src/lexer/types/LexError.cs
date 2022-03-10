namespace O7;

class LexError {}

class EndOfInputError : LexError {
}

class ExtraClosingCurlyBraceError : LexError {
}

class ExtraClosingParenError : LexError {
}

class ExtraClosingBracketError : LexError {
}

class NonAsciiError : LexError {
}

class EmptyStackError : LexError {
}


class UnexpectedSymbolError : LexError {
    public string val;

    public UnexpectedSymbolError(string val) {
        this.val = val;
    }

    public UnexpectedSymbolError(byte val) {
        this.val = val.ToString();
    }

    public override string ToString() {
        return $"UnexpectedSymbolError: {val}";
    }
}


class IntError : LexError {
    public string val = "";
    public IntError(string val) {
        this.val = val;
    }

    public override string ToString() {
        return "IntError: $val";
    }
}


class FloatError : LexError {
    string val = "";
    public FloatError(string val) {
        this.val = val;
    }

    public override string ToString() {
        return "FloatError: $val";
    }
}


class WordError : LexError {
    string val = "";
    public WordError(string val) {
        this.val = val;
    }

    public override string ToString() {
        return "WordError: $val";
    }
}


class OperatorError : LexError {
    string val = "";
    public OperatorError(string val) {
        this.val = val;
    }

    public override string ToString() {
        return "OperatorError: $val";
    }
}


class BoolError : LexError {
    public string val = "";
    public BoolError(string val) {
        this.val = val;
    }
}
