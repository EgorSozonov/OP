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
            l(lexed.toString());
            l("");

            var parseRes = Parser.parse(lexed);
            if (parseRes.i1 != null) {
                l("Parser error");
                l(parseRes.i1.toString());
                l(parseRes.i0.toString());
            } else {


                val expected = new ASTList(SyntaxContext.curlyBraces);

                val outerIf = new ASTList(SyntaxContext.iff);
                outerIf.data.get(0).add(new Ident("x"));
                outerIf.data.get(0).add(new FunctionOperatorAST(CoreOperator.greaterThan));
                outerIf.data.get(0).add(new IntLiteral(5));
                outerIf.data.add(new ArrayList<>());
                val outerCB = new ASTList(SyntaxContext.curlyBraces);
                val innerCB = new ASTList(SyntaxContext.curlyBraces);
                val innerIf = new ASTList(SyntaxContext.iff);
                innerIf.data.get(0).add(new Ident("y"));
                innerIf.data.get(0).add(new FunctionOperatorAST(CoreOperator.lessThan));
                innerIf.data.get(0).add(new IntLiteral(10));
                innerIf.data.add(new ArrayList<>());
                innerIf.data.get(1).add(new IntLiteral(11));
                innerIf.data.add(new ArrayList<>());
                innerIf.data.get(2).add(new ReservedLiteral(ReservedWord.elsee));
                innerIf.data.add(new ArrayList<>());
                innerIf.data.get(3).add(new IntLiteral(-1));

                innerCB.data.get(0).add(innerIf);
                outerCB.data.get(0).add(innerCB);
                outerIf.data.get(1).add(outerCB);

                outerIf.data.add(new ArrayList<>());
                outerIf.data.get(2).add(new Ident("z"));
                outerIf.data.get(2).add(new FunctionOperatorAST(CoreOperator.greaterThan));
                outerIf.data.get(2).add(new IntLiteral(1));

                outerIf.data.add(new ArrayList<>());
                outerIf.data.get(3).add(new IntLiteral(2));

                outerIf.data.add(new ArrayList<>());
                outerIf.data.get(4).add(new ReservedLiteral(ReservedWord.elsee));

                outerIf.data.add(new ArrayList<>());
                outerIf.data.get(5).add(new IntLiteral(0));
                expected.data.get(0).add(outerIf);


                val print5 = new ASTList(SyntaxContext.funcall);
                print5.data.get(0).add(new Ident("print"));
                print5.data.get(0).add(new IntLiteral(5));
                expected.data.get(0).add(print5);

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
