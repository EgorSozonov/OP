package tech.sozonov.o7;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import lombok.val;
import tech.sozonov.o7.lexer.Lexer;
import tech.sozonov.o7.parser.Parser;
import tech.sozonov.o7.parser.types.SyntaxContexts.CoreOperator;
import tech.sozonov.o7.parser.types.SyntaxContexts.ReservedWord;
import tech.sozonov.o7.parser.types.SyntaxContexts.SyntaxContext;
import tech.sozonov.o7.parser.types.ASTUntyped.*;
import static tech.sozonov.o7.utils.ArrayUtils.*;

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
        val expected = new ASTList(SyntaxContext.curlyBraces);

        val outerIf = new ASTList(SyntaxContext.curlyBraces);
        outerIf.data.get(0).add(new Ident("x"));
        outerIf.data.get(0).add(new FunctionOperatorAST(CoreOperator.greaterThan));
        outerIf.data.get(0).add(new IntLiteral(5));
        outerIf.data.add(new ArrayList<>());
        val outerCB = new ASTList(SyntaxContext.curlyBraces);
        val innerCB = new ASTList(SyntaxContext.curlyBraces);
        val innerIf = new ASTList(SyntaxContext.iff);
        innerIf.data.get(0).add(new Ident("y"));

        innerCB.data.get(0).add(innerIf);
        outerCB.data.get(0).add(innerCB);
        outerIf.data.get(1).add(outerCB);

        outerIf.data.add(new ArrayList<>());
        outerIf.data.get(2).add(new Ident("x"));
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

        val lexResult = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        var lexed = lexResult.i0;
        assertNull(lexResult.i1);

        var parseRes = Parser.parse(lexed);
        assertNull(parseRes.i1);
    }

}
