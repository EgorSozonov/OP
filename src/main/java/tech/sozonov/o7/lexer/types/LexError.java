package main.java.tech.sozonov.o7.lexer.types;

class LexError {

static class LexErrorBase {}

static class EndOfInputError extends LexErrorBase {
}

static class ExtraClosingCurlyBraceError extends LexErrorBase {
}

static class ExtraClosingParenError extends LexErrorBase {
}

static class ExtraClosingBracketError extends LexErrorBase {
}

static class NonAsciiError extends LexErrorBase {
}

static class EmptyStackError extends LexErrorBase {
}


static class UnexpectedSymbolError extends LexErrorBase {
    public String val;

    public UnexpectedSymbolError(String val) {
        this.val = val;
    }

    public UnexpectedSymbolError(byte val) {
        this.val = val.toString();
    }

    @Override
    public String toString() {
        return "UnexpectedSymbolErrorextends " + val;
    }
}


static class IntError extends LexErrorBase {
    public String val = "";
    public IntError(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "IntErrorextends " + val;
    }
}


class FloatError extends LexErrorBase {
    String val = "";
    public FloatError(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "FloatErrorextends $val";
    }
}


class WordError extends LexErrorBase {
    String val = "";
    public WordError(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "WordErrorextends $val";
    }
}


class OperatorError extends LexErrorBase {
    String val = "";
    public OperatorError(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "OperatorErrorextends " + val;
    }
}


class BoolError extends LexErrorBase {
    public String val = "";
    public BoolError(String val) {
        this.val = val;
    }
}

}
