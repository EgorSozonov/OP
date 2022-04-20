package tech.sozonov.o7.lexer.types;
import java.nio.charset.StandardCharsets;

public class LexError {

public static class LexErrorBase {}

public static class EndOfInputError extends LexErrorBase {
}

public static class ExtraClosingCurlyBraceError extends LexErrorBase {
}

public static class ExtraClosingParenError extends LexErrorBase {
}

public static class ExtraClosingBracketError extends LexErrorBase {
}

public static class NonAsciiError extends LexErrorBase {
}

public static class EmptyStackError extends LexErrorBase {
}


public static class UnexpectedSymbolError extends LexErrorBase {
    public String val;

    public UnexpectedSymbolError(String val) {
        this.val = val;
    }

    public UnexpectedSymbolError(byte val) {
        this.val = new String(new byte[] {val}, StandardCharsets.US_ASCII);
    }

    @Override
    public String toString() {
        return "UnexpectedSymbolError " + val;
    }
}


public static class IntError extends LexErrorBase {
    public String val = "";
    public IntError(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "IntError " + val;
    }
}


public static class FloatError extends LexErrorBase {
    String val = "";
    public FloatError(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "FloatError " + val;
    }
}


public static class WordError extends LexErrorBase {
    String val = "";
    public WordError(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "WordError " + val;
    }
}


public static class OperatorError extends LexErrorBase {
    String val = "";
    public OperatorError(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "OperatorError " + val;
    }
}


public static class BoolError extends LexErrorBase {
    public String val = "";
    public BoolError(String val) {
        this.val = val;
    }
}

}
