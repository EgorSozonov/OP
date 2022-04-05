package tech.sozonov.o7;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.*;
import java.nio.charset.StandardCharsets;
import lombok.val;
import tech.sozonov.o7.lexer.Lexer;
import tech.sozonov.o7.lexer.types.ExprLexicalType;
import tech.sozonov.o7.lexer.types.Expr.*;

@DisplayName("Lexer tests")
public class LexerTest {
    @Test
    void a0() {
        assertTrue(true);
    }

    @Test
    @DisplayName("integer test")
    void integer() {
        val input = "_1234567";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.US_ASCII));

        val expected = new ListExpr(ExprLexicalType.curlyBraces);
        val expectedStatement = new ListExpr(ExprLexicalType.statement);
        expectedStatement.val.add(new IntToken(-1234567));
        expected.val.add(expectedStatement);



        assertTrue(ListExpr.equal(output.item0, expected));
    }

    @Test
    void a() {
        assertTrue(true);
    }
}
