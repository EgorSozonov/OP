package tech.sozonov.o7.parser.types;


public final class SyntaxError {
    public String val;

    public SyntaxError(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
