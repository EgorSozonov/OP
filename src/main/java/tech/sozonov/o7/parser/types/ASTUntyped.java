package tech.sozonov.o7.parser.types;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import tech.sozonov.o7.lexer.types.OperatorSymb;
import tech.sozonov.o7.lexer.types.Expr.ExprBase;
import tech.sozonov.o7.lexer.types.Expr.ListExpr;
import tech.sozonov.o7.lexer.types.Expr.OperatorToken;
import tech.sozonov.o7.lexer.types.Expr.WordToken;
import tech.sozonov.o7.parser.types.SyntaxContexts.CoreOperator;
import tech.sozonov.o7.parser.types.SyntaxContexts.ReservedWord;
import tech.sozonov.o7.parser.types.SyntaxContexts.SyntaxContext;
import static tech.sozonov.o7.utils.ArrayUtils.*;
import static tech.sozonov.o7.utils.ListUtils.*;
import tech.sozonov.o7.parser.types.ParseError.ParseErrorBase;


public class ASTUntyped {

public static class ASTUntypedBase {
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
    public final boolean isUnbounded;

    public ASTList(SyntaxContext ctx) {
        this.ctx = ctx;
        data = new ArrayList<>();
        curr = new ArrayList<>();
        data.add(curr);
        indList = 0;
        ind = 0;
        itemsIngested = 0;
        isUnbounded = this.isUnbounded();
    }

    /**
     * Try to add a new item to the current AST node. Returns a syntax error if unsuccessful.
     */
    public Optional<ParseErrorBase> add(ASTUntypedBase newItem) {
        // TODO adding of stuff in accordance to parse context
        // Validate all atoms + dataInitializer case

        // val mbPunctuation = mbParsePunctutation(сurrToken);
        // if (mbPunctuation.isEmpty()) {

        // }
        if (isAssignment()) {
            if (newItem instanceof CoreOperatorAST co && EnumSet.range(CoreOperator.defineImm, CoreOperator.divideMut).contains(co.val)) {
                newStatement();
                return Optional.empty();
            }
        }
        if (newItem instanceof CoreOperatorAST co) {
            if ((co.val == CoreOperator.arrow && (ctx == SyntaxContext.matchh || ctx == SyntaxContext.matchUnboundedd
                                                        || ctx == SyntaxContext.iff || ctx == SyntaxContext.ifUnboundedd))
                || (co.val == CoreOperator.pipe && (ctx == SyntaxContext.sumTypee || ctx == SyntaxContext.sumTypeUnboundedd) )
                || (co.val == CoreOperator.colon && (ctx == SyntaxContext.structt || ctx == SyntaxContext.structUnboundedd))) {
                newStatement();
                return Optional.empty();
            }
        }
        curr.add(newItem);
        ind++;
        return Optional.empty();
    }

    public void newStatement() {
        curr = new ArrayList<>();
        data.add(curr);
    }

    public void newStatement(SyntaxContext statType, ASTList newItem) {
        curr = new ArrayList<>();
        data.add(curr);
        curr.add(newItem);
    }

    /**
     * A list of tokens has ended, and we need to know whether this current AST is saturated.
     * Returns true if saturated, false if not yet saturated, and None if oversaturated (which shouldn't ever happen).
     */
    public Optional<Boolean> listHasEnded() {
        if (isBounded()) {
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
        } else if (isUnbounded()) {
            if ((ctx == SyntaxContext.ifUnboundedd || ctx == SyntaxContext.matchUnboundedd)
                && data.size() > 1 && last(data).get(0) instanceof ReservedLiteral rl && rl.val == ReservedWord.elsee) {
                return Optional.of(true);
            } else {
                newStatement();
                return Optional.of(false);
            }
        } else {
            return Optional.of(true);
        }
    }


    /**
     * Does the next statement conform to the current core syntax form, or not?
     * In case not, it's a syntax error or the end of the current core form.
     */
    public boolean statementFitsCoreForm(ListExpr le) {
        final int len = le.val.size();
        if (ctx == SyntaxContext.iff || ctx == SyntaxContext.ifUnboundedd) {
            // Must be of the form "a .func b -> b"
            if (len < 3) return false;
            if (le.val.get(len - 2) instanceof OperatorToken ot) {
                return (arraysEqual(Syntax.arrow, ot.val));
            } else return false;

        } else if (ctx == SyntaxContext.matchh || ctx == SyntaxContext.matchUnboundedd) {
            // Must be of the form "A -> b" or "Aaa | Bbb | Ccc -> b"
            if (len < 3) return false;
            if (le.val.get(len - 2) instanceof OperatorToken ot) {
                if (arraysEqual(Syntax.arrow, ot.val)) {
                    for (int i = 0; i < len - 2; ++i) {
                        if ((i % 2 == 0 && (le.val.get(i) instanceof WordToken wt) && Character.isUpperCase(wt.val.charAt(0)))
                            || i % 2 == 1 && (le.val.get(i) instanceof OperatorToken ot2) && arraysEqual(Syntax.pipe, ot2.val)) {
                            continue;
                        }
                        return false;
                    }
                } else return false;
            } else return false;
            // check for arrow symbol in penultimate position, plus the preceding must be either a single wordtoken or several split with pipes
        } else if (ctx == SyntaxContext.structt || ctx == SyntaxContext.structUnboundedd) {
            // check for colon in second position, plus the following must be a sequence of type names
        } else if (ctx == SyntaxContext.sumTypee || ctx == SyntaxContext.sumTypeUnboundedd) {
            // TODO write a check for a sum type clause
        }
        return true;
    }

    public void startNewList() {
        curr = new ArrayList<>();
        data.add(curr);
        ++ind;
        ++itemsIngested;
    }

    public boolean isFuncall() {
        return ctx == SyntaxContext.funcall;
    }

    public boolean isAssignment() {
        return EnumSet.range(SyntaxContext.assignImmutable, SyntaxContext.assignMutableDiv).contains(ctx);
    }

    public boolean isCoreForm() {
        return EnumSet.range(SyntaxContext.iff, SyntaxContext.sumTypeUnboundedd).contains(ctx);
    }

    public boolean isUnbounded() {
        return (ctx == SyntaxContext.ifUnboundedd || ctx == SyntaxContext.matchUnboundedd
                || ctx == SyntaxContext.structUnboundedd || ctx == SyntaxContext.sumTypeUnboundedd);
    }

    public boolean isBounded() {
        return EnumSet.range(SyntaxContext.iff, SyntaxContext.typeDeclaration).contains(ctx);
    }

    public boolean isEmpty() {
        return this.data.size() == 1 && curr.size() == 0;
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

/**
 * Reserved words which are not markers for core forms (i.e. do not occur in the initial position)
 */
public final static class ReservedLiteral extends ASTUntypedBase {
    public ReservedWord val;
    public ReservedLiteral(ReservedWord val) {
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
 * Built-in operators that are syntactical markers (arrows, pipes etc).
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
