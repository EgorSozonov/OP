package tech.sozonov.o7;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import tech.sozonov.o7.lexer.Lexer;
import tech.sozonov.o7.parser.Parser;
import tech.sozonov.o7.parser.types.SyntaxContexts.CoreOperator;
import tech.sozonov.o7.parser.types.SyntaxContexts.ReservedWord;
import tech.sozonov.o7.parser.types.SyntaxContexts.SyntaxContext;
import tech.sozonov.o7.parser.types.ASTUntyped.*;


public class ParserTest {
    @Test
    @DisplayName("Given a simple unbounded syntax form like \"if\" , parsing should be correct")
    void unbounded1() {
        val input = """
        if x > 5 && (x < 10) -> 0
           x > 1 -> 10
           else  -> 20
        """;
        List<ArrayList<ASTUntypedBase>> innards = List.of(
            new ArrayList<>(
                List.of(
                    new ASTList(SyntaxContext.iff, List.of(
                        new ArrayList<>(
                            List.of(new Ident("x"), new FunctionOperatorAST(CoreOperator.greaterThan), new IntLiteral(5),
                            new FunctionOperatorAST(CoreOperator.and),
                            new ASTList(SyntaxContext.funcall, List.of(new ArrayList<>(List.of(new Ident("x"), new FunctionOperatorAST(CoreOperator.lessThan), new IntLiteral(10))))
                            ))),
                        new ArrayList<>(List.of(new IntLiteral(0))
                        ),
                        new ArrayList<>(List.of(new Ident("x"), new FunctionOperatorAST(CoreOperator.greaterThan), new IntLiteral(1))),
                        new ArrayList<>(List.of(new IntLiteral(10))),
                        new ArrayList<>(List.of(new ReservedLiteral(ReservedWord.elsee))),
                        new ArrayList<>(List.of(new IntLiteral(20)))
                    )
                )
                )));
        val expected = new ASTList(SyntaxContext.curlyBraces, innards);

        val lexResult = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        var lexed = lexResult.i0;
        assertNull(lexResult.i1);
        var parseRes = Parser.parse(lexed);
        assertNull(parseRes.i1);

        assertTrue(ASTUntypedBase.equal(parseRes.i0, expected));
    }

    @Test
    @DisplayName("Given an unbounded syntax form like \"if\" with another if nested, parsing should be correct")
    void unbounded2() {
        val input = """
        if x > 5 -> ({
                if
                    y.z < 10 -> 11
                    else -> _1
                })
           z > 1 -> 2
           else  -> 0

        print 5
        """;
        List<ArrayList<ASTUntypedBase>> innards = List.of(
            new ArrayList<>(
                List.of(
                    new ASTList(SyntaxContext.iff, List.of(
                        new ArrayList<>(
                            List.of(new Ident("x"), new FunctionOperatorAST(CoreOperator.greaterThan), new IntLiteral(5))),
                        new ArrayList<>(List.of(new ASTList(SyntaxContext.curlyBraces, List.of(
                            new ArrayList<>(List.of(new ASTList(SyntaxContext.curlyBraces,
                                List.of(new ArrayList<>(List.of( new ASTList(SyntaxContext.iff, List.of(
                                        new ArrayList<>(List.of(new Ident("y.z"),
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

        val lexResult = Lexer.lexicallyAnalyze(input.getBytes(StandardCharsets.UTF_8));
        var lexed = lexResult.i0;
        assertNull(lexResult.i1);
        var parseRes = Parser.parse(lexed);
        assertNull(parseRes.i1);

        assertTrue(ASTUntypedBase.equal(parseRes.i0, expected));
    }

}
