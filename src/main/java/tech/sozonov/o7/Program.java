package tech.sozonov.o7;
import tech.sozonov.o7.lexer.Lexer;
import tech.sozonov.o7.lexer.types.Expr.*;
import tech.sozonov.o7.parser.Parser;
import tech.sozonov.o7.parser.types.ASTUntyped.*;
import tech.sozonov.o7.parser.types.SyntaxContexts.CoreOperator;
import tech.sozonov.o7.parser.types.SyntaxContexts.ReservedWord;
import tech.sozonov.o7.parser.types.SyntaxContexts.SyntaxContext;
import java.nio.charset.StandardCharsets;
import lombok.val;
import static tech.sozonov.o7.utils.ArrayUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Program {
    public static void main(String[] args) {


        //val innp = "abc _5 4.21";
        // val a = new ASTList(SyntaxContext.funcall);
        // a.data.get(0).add(new IntLiteral(5));

        // val b = new ASTList(SyntaxContext.funcall);
        // b.data.get(0).add(new IntLiteral(5));
        // b.data.get(0).add(new FloatLiteral(5.2));

        // l(ASTUntypedBase.equal(a, b) + "");




//         val innp = """
//         while x > 5 {
//             print x
//             x += 1
//         }
// """;
        val innp = """
        if x > 5 -> ({
                if
                    y < 10 -> 11
                    else -> _1
                })
           z > 1 -> 2
           else  -> 0

        print 5
        """;

        val res = Lexer.lexicallyAnalyze(innp.getBytes(StandardCharsets.UTF_8));
        if (res.i1 != null) {
            l("Lexer error");
            l(res.i1.toString());
            l(res.i0.toString());
        } else {
            l("Lexer successful");
            var lexed = res.i0;
            //l(lexed.toString());
            l("");

            var parseRes = Parser.parse(lexed);
            if (parseRes.i1 != null) {
                l("Parser error");
                l(parseRes.i1.toString());
                l(parseRes.i0.toString());
            } else {
                List<ArrayList<ASTUntypedBase>> innards = List.of(
                        new ArrayList<>(
                            List.of(
                                new ASTList(SyntaxContext.iff, List.of(
                                    new ArrayList<>(
                                        List.of(new Ident("x"), new FunctionOperatorAST(CoreOperator.greaterThan), new IntLiteral(5))),
                                    new ArrayList<>(List.of(new ASTList(SyntaxContext.curlyBraces, List.of(
                                        new ArrayList<>(List.of(new ASTList(SyntaxContext.curlyBraces,
                                            List.of(new ArrayList<>(List.of( new ASTList(SyntaxContext.iff, List.of(
                                                    new ArrayList<>(List.of(new Ident("y"),
                                                                            new FunctionOperatorAST(CoreOperator.lessThan),
                                                                            new IntLiteral(10))),
                                                    new ArrayList<>(List.of(new IntLiteral(11))),
                                                    new ArrayList<>(List.of(new ReservedLiteral(ReservedWord.elsee))),
                                                    new ArrayList<>(List.of(new IntLiteral(-1)))
                                                )
                                            ))))
                                        ))))))
                                    ),
                                    new ArrayList<>(List.of(new Ident("z"), new FunctionOperatorAST(CoreOperator.greaterThan), new IntLiteral(1))),
                                    new ArrayList<>(List.of(new IntLiteral(2))),
                                    new ArrayList<>(List.of(new ReservedLiteral(ReservedWord.elsee))),
                                    new ArrayList<>(List.of( new IntLiteral(0)))
                                )
                            ),

                            new ASTList(SyntaxContext.funcall, List.of(
                                new ArrayList<>(List.of(new Ident("print"), new IntLiteral(5)))
                                ))
                            )));
                val expected = new ASTList(SyntaxContext.curlyBraces, innards);

                l(ASTUntypedBase.equal(parseRes.i0, expected) + "");

                l(expected.toString());

                l("Parser successful");
                //l(parseRes.i0.toString());
            }

            // print("Expected:");
            // print(expected);
        }
    }
}
