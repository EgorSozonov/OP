package tech.sozonov.o7.parser.types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import tech.sozonov.o7.lexer.types.OperatorSymb;
import tech.sozonov.o7.lexer.types.Expr.ExprBase;
import tech.sozonov.o7.lexer.types.Expr.ListExpr;
import tech.sozonov.o7.lexer.types.Expr.OperatorToken;
import tech.sozonov.o7.parser.types.SyntaxContexts.CoreOperator;
import tech.sozonov.o7.parser.types.SyntaxContexts.SyntaxContext;
import tech.sozonov.o7.parser.types.ParseError.ParseErrorBase;


public class ASTUntyped {

public static class ASTUntypedBase {

    public static boolean isUnbounded(SyntaxContext ctx) {
        return (ctx == SyntaxContext.ifUnboundedd || ctx == SyntaxContext.matchUnboundedd
                || ctx == SyntaxContext.structUnboundedd || ctx == SyntaxContext.sumTypeUnboundedd);
    }

    public static boolean isUnboundable(SyntaxContext ctx) {
        return (ctx == SyntaxContext.iff || ctx == SyntaxContext.matchh
                || ctx == SyntaxContext.structt || ctx == SyntaxContext.sumTypee);
    }

    public static SyntaxContext makeUnbounded(SyntaxContext ctx) {
        if (ctx == SyntaxContext.iff) return SyntaxContext.ifUnboundedd;
        if (ctx == SyntaxContext.matchh) return SyntaxContext.matchUnboundedd;
        if (ctx == SyntaxContext.structt) return SyntaxContext.structUnboundedd;
        // if ctx == sumTypee
        return SyntaxContext.sumTypeUnboundedd;
    }

    @Override
    public final String toString() {
        if (this instanceof ASTList lsOuter) {
            return "";

            // var result = new StringBuilder();
            // var backtrack = new Stack<Tuple<ListStatements, Integer>>();
            // ASTList curr = lsOuter;
            // int i = 0;

            // do {
            //     while (i < curr.val.size()) {
            //         if (curr.val.get(i) instanceof ListStatements listElem) {
            //             if (hasValues(listElem.val)) {
            //                 backtrack.push(new Tuple<ListStatements, Integer>(curr, i));
            //                 curr = listElem;
            //                 i = 0;
            //                 if (curr.ctx == ParseContext.curlyBraces) {
            //                     result.append("{\n ");
            //                 } else if (curr.sType == SubexprType.dataInitializer) {
            //                     result.append("[");
            //                 } else if (curr.sType == SubexprType.parens){
            //                     result.append("(");
            //                 }
            //             } else {
            //                 ++i;
            //             }
            //         } else {
            //             result.append(curr.val.get(i).toString());
            //             result.append(", ");
            //             ++i;
            //         }
            //     }
            //     if (backtrack.peek() != null) {
            //         if (curr.sType == SubexprType.curlyBraces) {
            //             result.append("}\n ");
            //         } else if (curr.sType == SubexprType.dataInitializer) {
            //             result.append("], ");
            //         } else if (curr.sType == SubexprType.parens) {
            //             result.append(") ");
            //         } else {
            //             result.append(";\n ");
            //         }
            //         var back = backtrack.pop();
            //         curr = back.item0;
            //         i = back.item1 + 1;
            //     }
            // } while (backtrack.peek() != null || i < curr.val.size());

            // return result.toString();
        } else if (this instanceof Ident x) {
            return "id " + x.name;
        } else if (this instanceof ASTList x3) {
            return "AST List";
        } else if (this instanceof IntLiteral x4) {
            return Long.toString(x4.val);
        } else if (this instanceof FloatLiteral x5) {
            return Double.toString(x5.val);
        } else if (this instanceof CoreOperatorAST x7) {
            return x7.val.toString();
        } else if (this instanceof BoolLiteral x8) {
            return "boolean " + Boolean.toString(x8.val);
        } else {
            return "Something else";
        }
    }
}

public final static class ASTList extends ASTUntypedBase {
    public ArrayList<ArrayList<ASTUntypedBase>> data;
    public int indList;
    public int ind;
    public ArrayList<ASTUntypedBase> curr;
    public SyntaxContext ctx;
    public int itemsIngested;
    private final boolean isUnbounded;

    public ASTList(SyntaxContext ctx) {
        this.ctx = ctx;
        data = new ArrayList<>();
        curr = new ArrayList<>();

        data.add(curr);
        indList = 0;
        ind = 0;
        itemsIngested = 0;
        isUnbounded = ASTList.isUnbounded(ctx);
    }

    // public ASTList(ExprLexicalType listType) {
    //     if (listType == ExprLexicalType.curlyBraces) {
    //         this.ctx = ParseContext.curlyBraces;
    //     } else if (listType == ExprLexicalType.dataInitializer) {
    //         this.ctx = ParseContext.dataInitializer;
    //     } else if (listType == ExprLexicalType.parens) {
    //         this.ctx = ParseContext.parens;
    //     } else {
    //         this.ctx = ParseContext.statement;
    //     }
    // }

    /**
     * Try to add a new item to the current AST node. Returns a syntax error if unsuccessful.
     */
    public Optional<ParseErrorBase> add(ASTUntypedBase newItem) {
        // TODO adding of stuff in accordance to parse context
        // Validate all atoms + dataInitializer case

        // val mbPunctuation = mbParsePunctutation(сurrToken);
        // if (mbPunctuation.isEmpty()) {

        // }
        if (newItem instanceof CoreOperatorAST co) {
            if ((ctx == SyntaxContext.iff && co.val == CoreOperator.arrow)
                || (ctx == SyntaxContext.matchh && co.val == CoreOperator.arrow)
                || (ctx == SyntaxContext.matchh && co.val == CoreOperator.arrow)
                || (ctx == SyntaxContext.structt && co.val == CoreOperator.colon)) {
                curr = new ArrayList<>();
                data.add(curr);
                return Optional.empty();
            }
        }
        curr.add(newItem);
        ind++;
        return Optional.empty();
    }

    public void newStatement(SyntaxContext statType, ASTList newItem) {
        curr = new ArrayList<>();
        data.add(curr);
        curr.add(newItem);
    }



    /**
     * A list of tokens has ended, and we need to know whether this current AST is saturated.
     * Returns true if saturated, false if not yet saturated, and None if oversaturated (which shouldn't ever happen).
     *
     * Saturation is determined by the parsing context and number of ingested lists:
     * if/1
     * while/2
     * for/4
     * foreach/2
     * match/2
     */
    public Optional<Boolean> listHasEnded() {
        int saturationLength = 0;
        if (ctx == SyntaxContext.iff) {
            saturationLength = 1;
        } else if (ctx == SyntaxContext.whilee || ctx == SyntaxContext.foreachh || ctx == SyntaxContext.matchh) {
            saturationLength = 2;
        } else if (ctx == SyntaxContext.forr) {
            saturationLength = 4;
        } else {
            saturationLength = 0;
        }
        if (itemsIngested < saturationLength) return Optional.of(false);
        if (itemsIngested == saturationLength) return Optional.of(true);
        return Optional.empty();
    }



    public boolean unboundedNeedsToStop(ListExpr le) {
        // TODO
        return false;
    }

    public void startNewList() {
        curr = new ArrayList<>();
        data.add(curr);
        ++ind;
    }

    /**
     * Returns true iff the current context is about to ingest the next list item (i.e. the next {}, () or []).
     */
    public boolean isUnbounded() {
        return this.isUnbounded;
    }

    public void ingestItem() {
        startNewList();
        ++itemsIngested;
    }



    static Optional<ParsePunctuation> mbParsePunctutation(ExprBase token) {
        if (token instanceof OperatorToken op) {
            if (op.val.size() == 2 && op.val.get(0) == OperatorSymb.minus && op.val.get(1) == OperatorSymb.gt) {
                return Optional.of(ParsePunctuation.arrow);
            } else if (op.val.size() == 1 && op.val.get(0) == OperatorSymb.colon) {
                return Optional.of(ParsePunctuation.colon);
            } else if (op.val.size() == 1 && op.val.get(0) == OperatorSymb.dollar) {
                return Optional.of(ParsePunctuation.dollar);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    static Optional<SyntaxContext> getOperatorAssignmentType(OperatorToken ot) {
        if (ot.val.size() == 1 && ot.val.get(0) == OperatorSymb.equals) return Optional.of(SyntaxContext.assignImmutable);
        if (ot.val.size() == 2 && ot.val.get(1) == OperatorSymb.equals) {
            var f = ot.val.get(0);
            if (f == OperatorSymb.colon) return Optional.of(SyntaxContext.assignMutable);
            if (f == OperatorSymb.plus) return Optional.of(SyntaxContext.assignMutablePlus);
            if (f == OperatorSymb.minus) return Optional.of(SyntaxContext.assignMutableMinus);
            if (f == OperatorSymb.asterisk) return Optional.of(SyntaxContext.assignMutableTimes);
            if (f == OperatorSymb.slash) return Optional.of(SyntaxContext.assignMutableDiv);
        }
        return Optional.empty();
    }
}



// public final static class Statement extends ASTUntypedBase {
//     public List<ASTUntypedBase> val;
//     public Statement(List<ASTUntypedBase> val) {
//         this.val = val;
//     }

//     public Statement() {
//         this.val = new ArrayList<ASTUntypedBase>();
//     }
// }

public final static class Ident extends ASTUntypedBase {
    public String name;

    public Ident(String name) {
        this.name = name;
    }
}

public final static class IntLiteral extends ASTUntypedBase {
    public long val;
    public IntLiteral(long val) {
        this.val = val;
    }
}

public final static class FloatLiteral extends ASTUntypedBase {
    public double val;
    public FloatLiteral(double val) {
        this.val = val;
    }
}

public final static class BoolLiteral extends ASTUntypedBase {
    public boolean val;
    public BoolLiteral(boolean val) {
        this.val = val;
    }
}

public final static class StringLiteral extends ASTUntypedBase {
    public String val;
    public StringLiteral(String val) {
        this.val = val;
    }
}


/**
 * Built-in operators that are functions (arithmetic, bitwise etc).
 */
public final static class FunctionOperatorAST extends ASTUntypedBase {
    public CoreOperator val;
    public FunctionOperatorAST(CoreOperator val) {
        this.val = val;
    }
}

/**
 * Built-in operators that are syntactical (arrows, pipes etc).
 */
public final static class CoreOperatorAST extends ASTUntypedBase {
    public CoreOperator val;
    public CoreOperatorAST(CoreOperator val) {
        this.val = val;
    }
}

public final static class OperatorAST extends ASTUntypedBase {
    public List<OperatorSymb> val;
    public OperatorAST(List<OperatorSymb> val) {
        this.val = val;
    }
}

}
