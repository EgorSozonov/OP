package tech.sozonov.o7.lexer.types;
import java.nio.charset.StandardCharsets;


public class LexError {
public String val;

public LexError(String val) {
    this.val = val;
}

public LexError(byte val) {
    this.val = new String(new byte[] {val}, StandardCharsets.US_ASCII);
}

@Override
public String toString() {
    return val;
}

}
