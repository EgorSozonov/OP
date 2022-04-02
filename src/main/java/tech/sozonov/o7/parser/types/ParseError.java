package tech.sozonov.o7.parser.types;

public class ParseError {

public static class ParseErrorBase {}

public final static class UnexpectedTokenError extends ParseErrorBase {
    public String val;

    public UnexpectedTokenError(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}


public final static class AssignmentError extends ParseErrorBase {
    public String val;

    public AssignmentError(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}

}
