package tech.sozonov.o7;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.nio.charset.StandardCharsets;
import lombok.val;
import tech.sozonov.o7.lexer.Lexer;
import tech.sozonov.o7.parser.Parser;


public class ParserTest {
    @Test
    @DisplayName("Given an unbounded syntax form like \"if\" with another if nested, parsing should be correct")
    void unbounded1() {
        val input = """
        if x > 5 -> ({
                if
                    y < 10 -> 11
                    else -> _1
                })
           x > 1 -> 2
           else  -> 0

        print 5
        """;
        val lexResult = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        var lexed = lexResult.i0;
        assertNull(lexResult.i1);

        var parseRes = Parser.parse(lexed);
        assertNull(parseRes.i1);
    }

}
