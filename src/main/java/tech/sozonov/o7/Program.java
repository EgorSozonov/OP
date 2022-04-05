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



        val input = "_1234567";
        val output = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.US_ASCII));
        val expected = new ListExpr(ExprLexicalType.curlyBraces);
        val expectedStatement = new ListExpr(ExprLexicalType.statement);
        expectedStatement.val.add(new IntToken(-1234567));
        expected.val.add(expectedStatement);

        l(output.item0.toString());
        l(expected.toString());
        l(ListExpr.equal(output.item0, expected) + " ");

        if (2 > 1) return;
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
