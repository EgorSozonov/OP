package tech.sozonov.o7;
import tech.sozonov.o7.lexer.Lexer;
import tech.sozonov.o7.lexer.types.ExprLexicalType;
import tech.sozonov.o7.lexer.types.Expr.*;
import tech.sozonov.o7.parser.Parser;
import static tech.sozonov.o7.utils.ListUtils.*;
import java.nio.charset.StandardCharsets;
import lombok.val;
import static tech.sozonov.o7.utils.ByteArrayUtils.*;


class Program {
    public static void main(String[] args) {



        var inp = """
    x = 5 + 2
""";

        var innp = "9_223_372_036_854_775_807";

        val res = Lexer.lexicallyAnalyze(innp.getBytes(StandardCharsets.UTF_8));
        val expected = ExprBase.wrapOneToken(new IntToken(Integer.MAX_VALUE));
        l(res.item0.toString());
        l(expected.toString());
        // var eqRes = Expr.equal(expected, expected2);
        // print("eqRes = $eqRes");
        // return;


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
