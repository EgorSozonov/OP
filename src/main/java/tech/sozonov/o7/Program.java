package tech.sozonov.o7;
import tech.sozonov.o7.lexer.Lexer;
import static tech.sozonov.o7.utils.ListUtils.*;

import java.nio.charset.StandardCharsets;

import static tech.sozonov.o7.utils.ByteArrayUtils.*;


class Program {
    public static void main(String[] args) {
        var inp = """
    x = 5 + 2
""";

        var innp = """
            a b
            #cd.# he  he  he
            {
                12 + y
                x foo [12.34 fa]
            }
        """;

        var res = Lexer.lexicallyAnalyze(inp.getBytes(StandardCharsets.UTF_8));
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

            // var parseRes = PreParser.parse(lexed);
            // if (parseRes.item1 != null) {
            //     l("Parser error");
            //     l(parseRes.item1.ToString());
            //     l(parseRes.item0.ToString());
            // } else {
            //     l("Parser successful");
            //     l(parseRes.item0.ToString());
            // }

            // print("Expected:");
            // print(expected);
        }
    }
}
