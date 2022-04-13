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
        var innp = """
        if x > 5 -> 5
           x > 1 -> 1
           else -> 0
        print 5
""";


        val res = Lexer.lexicallyAnalyze(innp.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapListTokens(Arrays.asList(new WordToken("abc"), new IntToken(-5), new FloatToken(4.21)));
        l(res.item0.toString());
        l(expected.toString());

        l("equality = " + ListExpr.equal(expected, res.item0));
        l("");
        if (res.item1 != null) {
            l("Lexer error");
            l(res.item1.toString());
            l(res.item0.toString());
        } else {
            l("Lexer successful");
            var lexed = res.item0;
            l(lexed.toString());
            l("");

            var parseRes = Parser.parse(lexed);
            if (parseRes.item1 != null) {
                l("Parser error");
                l(parseRes.item1.toString());
                l(parseRes.item0.toString());
            } else {
                l("Parser successful");
                l(parseRes.item0.toString());
            }

            // print("Expected:");
            // print(expected);
        }
    }
}
