package tech.sozonov.o7;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.charset.StandardCharsets;
import lombok.val;
import tech.sozonov.o7.lexer.Lexer;
import tech.sozonov.o7.lexer.types.Expr.*;
import java.util.Arrays;


@DisplayName("Lexer tests")
public class LexerTest {
    @Test
    @DisplayName("Given negative integer input, the result should be an integer token")
    void integer1() {
        val input = "_1234567";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapOneToken(new IntToken(-1234567));
        assertTrue(ListExpr.equal(output.i0, expected));
    }

    @Test
    @DisplayName("Given positive single-digit input, the result should be an integer token")
    void integer2() {
        val input = "2";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapOneToken(new IntToken(2));
        assertTrue(ListExpr.equal(output.i0, expected));
    }

    @Test
    @DisplayName("Given an integer input with internal underscores, the result should be an integer token")
    void integer3() {
        val input = "_12_3456_7";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapOneToken(new IntToken(-1234567));
        assertTrue(ListExpr.equal(output.i0, expected));
    }

    @Test
    @DisplayName("Given an integer input with two leading underscores, the result should be an error")
    void integer4() {
        val input = "__1234567";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        assertNotNull(output.i1);
    }

    @Test
    @DisplayName("Given integer input with max allowed 64-bit integer, the result should be an integer token")
    void integer5() {
        val input = "9_223_372_036_854_775_807";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapOneToken(new IntToken(Long.MAX_VALUE));
        assertTrue(ListExpr.equal(output.i0, expected));
    }

    @Test
    @DisplayName("Given integer input larger than the max allowed 64-bit integer, the result should be an error")
    void integer6() {
        val input = "9_223_372_036_854_775_808";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        assertNotNull(output.i1);
    }

    @Test
    @DisplayName("Given integer input with min allowed 64-bit integer, the result should be an integer token")
    void integer7() {
        val input = "_9_223_372_036_854_775_808";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapOneToken(new IntToken(Long.MIN_VALUE));
        assertTrue(ListExpr.equal(output.i0, expected));
    }

    @Test
    @DisplayName("Given integer input lower than the min allowed 64-bit integer, the result should be an error")
    void integer8() {
        val input = "_9_223_372_036_854_775_809";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        assertNotNull(output.i1);
    }

    @Test
    @DisplayName("Given a word as input, the result should be a word token literal")
    void word1() {
        val input = "false";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));

        val expected = ExprBase.wrapOneToken(new WordToken("false"));
        assertTrue(ListExpr.equal(output.i0, expected));
    }

    @Test
    @DisplayName("Given a word containing digits and a leading underscore as input, the result should be a word token literal")
    void word2() {
        val input = "_a0";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapOneToken(new WordToken("_a0"));
        assertTrue(ListExpr.equal(output.i0, expected));
    }

    @Test
    @DisplayName("Given a word containing internal underscores as input, the result should be an error")
    void word3() {
        val input = "a_b";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        assertNotNull(output.i1);
    }

    @Test
    @DisplayName("Given a simple statement of different tokens, they should be lexed correctly")
    void statement1() {
        val input = "abc _5 4.21";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapListTokens(Arrays.asList(new WordToken("abc"), new IntToken(-5), new FloatToken(4.21)));
        assertTrue(ExprBase.checkEquality(expected, output));
    }

}
