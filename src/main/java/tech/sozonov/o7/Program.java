package tech.sozonov.o7;
import tech.sozonov.o7.lexer.Lexer;
import tech.sozonov.o7.lexer.types.Expr.*;
import tech.sozonov.o7.parser.Parser;
import java.nio.charset.StandardCharsets;
import lombok.val;
import static tech.sozonov.o7.utils.ArrayUtils.*;
import java.util.Arrays;

class Program {
    public static void main(String[] args) {


        //val innp = "abc _5 4.21";

        val innp = """
        while x > 5 {
            print x
            x += 1
        }
""";


        val res = Lexer.lexicallyAnalyze(innp.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapListTokens(Arrays.asList(new WordToken("abc"), new IntToken(-5), new FloatToken(4.21)));
        l(res.i0.toString());
        l(expected.toString());

        l("equality = " + ListExpr.equal(expected, res.i0));
        l("");
        if (res.i1 != null) {
            l("Lexer error");
            l(res.i1.toString());
            l(res.i0.toString());
        } else {
            l("Lexer successful");
            var lexed = res.i0;
            l(lexed.toString());
            l("");

            var parseRes = Parser.parse(lexed);
            if (parseRes.i1 != null) {
                l("Parser error");
                l(parseRes.i1.toString());
                l(parseRes.i0.toString());
            } else {
                l("Parser successful");
                l(parseRes.i0.toString());
            }

            // print("Expected:");
            // print(expected);
        }
    }
}
