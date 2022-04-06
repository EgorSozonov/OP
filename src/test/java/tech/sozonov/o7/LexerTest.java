package tech.sozonov.o7;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.charset.StandardCharsets;
import lombok.val;
import tech.sozonov.o7.lexer.Lexer;
import tech.sozonov.o7.lexer.types.Expr.*;


@DisplayName("Lexer tests")
public class LexerTest {
    @Test
    @DisplayName("Given negative integer input, the result should be an integer token")
    void integer1() {
        val input = "_1234567";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapOneToken(new IntToken(-1234567));
        assertTrue(ListExpr.equal(output.item0, expected));
    }

    @Test
    @DisplayName("Given positive single-digit input, the result should be an integer token")
    void integer2() {
        val input = "2";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapOneToken(new IntToken(2));
        assertTrue(ListExpr.equal(output.item0, expected));
    }

    @Test
    @DisplayName("Given an integer input with internal underscores, the result should be an integer token")
    void integer3() {
        val input = "_12_3456_7";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapOneToken(new IntToken(-1234567));
        assertTrue(ListExpr.equal(output.item0, expected));
    }

    @Test
    @DisplayName("Given an integer input with two leading underscores, the result should be an error")
    void integer4() {
        val input = "__1234567";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        assertNotNull(output.item1);
    }

    @Test
    @DisplayName("Given a word as input, the result should be a word token literal")
    void word1() {
        val input = "false";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));

        val expected = ExprBase.wrapOneToken(new WordToken("false"));
        assertTrue(ListExpr.equal(output.item0, expected));
    }

}
