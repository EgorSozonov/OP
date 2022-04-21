package tech.sozonov.o7.parser.types;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import lombok.val;
import tech.sozonov.o7.lexer.types.OperatorSymb;
import tech.sozonov.o7.lexer.types.Expr.ExprBase;
import tech.sozonov.o7.lexer.types.Expr.ListExpr;
import tech.sozonov.o7.lexer.types.Expr.OperatorToken;
import tech.sozonov.o7.lexer.types.Expr.WordToken;
import tech.sozonov.o7.parser.types.SyntaxContexts.CoreOperator;
import tech.sozonov.o7.parser.types.SyntaxContexts.ReservedWord;
import tech.sozonov.o7.utils.ArrayUtils;
import tech.sozonov.o7.utils.Stack;
import tech.sozonov.o7.utils.Triple;
import static tech.sozonov.o7.utils.ArrayUtils.*;
import tech.sozonov.o7.parser.types.SyntaxContexts.SyntaxContext;
import static tech.sozonov.o7.parser.types.SyntaxContexts.SyntaxContext.*;


public class ASTUntyped {

public static class ASTUntypedBase {
    public static boolean isContextUnbounded(SyntaxContext ctx) {
        return (ctx == iff || ctx == matchh
                || ctx == structt || ctx == sumTypee);
    }

    public static boolean isAssignment(SyntaxContext ctx) {
        return EnumSet.range(assignImmutable, assignMutableDiv).contains(ctx);
    }

    public static boolean equal(ASTUntypedBase a, ASTUntypedBase b) {
        if (a.getClass() != b.getClass()) return false;

        if (a instanceof ASTList l1) {
            val l2 = (ASTList)b;
            val backtrackA = new Stack<Triple<ASTList, Integer, Integer>>();
            val backtrackB = new Stack<ASTList>();
            backtrackA.push(new Triple<>(l1, 0, 0));
            backtrackB.push(l2);

            int i = 0;
            int j = 0;
            while(backtrackA.peek() != null) {
                if (backtrackB.peek() == null) {
                     return false;
                }
                var listA = backtrackA.pop();
                var listB = backtrackB.pop();
                i = listA.i1;
                j = listA.i2;
                if (listA.i0.ctx != listB.ctx || listA.i0.data.size() != listB.data.size()) {
                    return false;
                }
                outerList:
                while (i < listB.data.size()) {
                    int lenSubList = listB.data.get(i).size();
                    if (listA.i0.data.get(i).size() != lenSubList) {
                        return false;
                    }

                    while (j < lenSubList) {
                        val itmA = listA.i0.data.get(i).get(j);
                        val itmB = listB.data.get(i).get(j);

                        if (itmA instanceof ASTList le1) {
                            if (!(itmB instanceof ASTList)) {
                                return false;
                            }

                            if (j < (lenSubList - 1)) {
                                backtrackA.push(new Triple<>(listA.i0, i, j + 1));
                            } else {
                                backtrackA.push(new Triple<>(listA.i0, i + 1, 0));
                            }
                            backtrackB.push(listB);
                            listA = new Triple<>(le1, 0, 0);
                            listB = (ASTList)itmB;
                            i = 0;
                            j = 0;
                            if (le1.ctx != listB.ctx || le1.data.size() != listB.data.size()) {
                                return false;
                            }

                            continue outerList;
                        } else if (!equal(itmA, itmB)) {
                            return false;
                        } else {
                            ++j;
                        }
                    }
                    ++i;
                    j = 0;
                }
            }
        } else if (a instanceof Ident x1){
            val y1 = (Ident)b;
            return x1.name.equals(y1.name);
        } else if (a instanceof IntLiteral x2){
            val y2 = (IntLiteral)b;
            return x2.val == y2.val;
        } else if (a instanceof FloatLiteral x3){
            val y3 = (FloatLiteral)b;
            return x3.val == y3.val;
        } else if (a instanceof BoolLiteral x4){
            val y4 = (BoolLiteral)b;
            return x4.val == y4.val;
        } else if (a instanceof ReservedLiteral x5){
            val y5 = (ReservedLiteral)b;
            return x5.val == y5.val;
        } else if (a instanceof StringLiteral x6){
            val y6 = (StringLiteral)b;
            return x6.val.equals(y6.val);
        } else if (a instanceof FunctionOperatorAST x7){
            val y7 = (FunctionOperatorAST)b;
            return x7.val == y7.val;
        } else if (a instanceof CoreOperatorAST x8){
            val y8 = (CoreOperatorAST)b;
            return x8.val == y8.val;
        } else if (a instanceof OperatorAST x9){
            val y9 = (OperatorAST)b;
            return ArrayUtils.arraysEqual(x9.val, y9.val);
        }
        return true;
    }


    @Override
    public final String toString() {
        if (this instanceof ASTList lsOuter) {
            val result = new StringBuffer();
            val backtrack = new Stack<Triple<ASTList, Integer, Integer>>();
            ASTList curr = lsOuter;
            int i = 0;
            int j = 0;
            do {
                outerLoop:
                while (i < curr.data.size()) {
                    int lenSublist = curr.data.get(i).size();
                    while (j < lenSublist) {
                        val itm = curr.data.get(i).get(j);
                        if (itm instanceof ASTList lst) {
                            if (lst.ctx == curlyBraces) {
                                result.append("{\n");
                            } else if (lst.ctx == iff) {
                                result.append("if ");
                            }
                            if (j < (lenSublist - 1)) {
                                backtrack.push(new Triple<> (curr, i, j + 1));
                            } else {
                                backtrack.push(new Triple<> (curr, i + 1, 0));
                            }
                            curr = lst;
                            i = 0;
                            j = 0;
                            continue outerLoop;
                        } else {
                            result.append(itm.toString());
                            result.append(" ");
                        }

                        ++j;
                    }
                    ++i;
                    j = 0;
                }
                if (backtrack.peek() != null) {
                    val back = backtrack.pop();
                    if (back.i0.ctx == curlyBraces) {
                        result.append("\n}\n");
                    }
                    curr = back.i0;
                    i = back.i1;
                    j = back.i2;
                }
            } while (backtrack.peek() != null || i < curr.data.size());
            return result.toString();
        } else if (this instanceof Ident x) {
            return "id " + x.name;
        } else if (this instanceof IntLiteral x4) {
            return Long.toString(x4.val);
        } else if (this instanceof FloatLiteral x5) {
            return Double.toString(x5.val);
        } else if (this instanceof BoolLiteral x8) {
            return Boolean.toString(x8.val);
        } else if (this instanceof ReservedLiteral x) {
            val str = x.val.toString();
            return str.substring(0, str.length() - 1);
        } else if (this instanceof StringLiteral x7) {
            return "Str: x7.val";
        } else if (this instanceof FunctionOperatorAST x7) {
            return x7.val.toString();
        } else if (this instanceof CoreOperatorAST x7) {
            return x7.val.toString();
        } else if (this instanceof OperatorAST x7) {
            return x7.val.toString();
        } else if (this instanceof CommentAST) {
            return "# comment .#";
        } else {
            return "The thing that should not be";
        }
    }
}

public final static class ASTList extends ASTUntypedBase {
    public ArrayList<ArrayList<ASTUntypedBase>> data;
    public ArrayList<ASTUntypedBase> curr;
    public SyntaxContext ctx;
    private int itemsIngested;

    public ASTList(SyntaxContext ctx) {
        this.ctx = ctx;
        data = new ArrayList<>();
        curr = new ArrayList<>();
        data.add(curr);
        itemsIngested = 0;
    }

    public ASTList(SyntaxContext ctx, List<ArrayList<ASTUntypedBase>> _data) {
        this.ctx = ctx;
        data = new ArrayList<ArrayList<ASTUntypedBase>>(_data);
        curr = data.get(0);
        itemsIngested = 0;
    }



    /**
     * Try to add a new item to the current AST node. Returns a syntax error if unsuccessful.
     */
    public Optional<SyntaxError> add(ASTUntypedBase newItem) {
        // TODO adding of stuff in accordance to parse context
        if (isAssignment()) {
            if (newItem instanceof CoreOperatorAST co && EnumSet.range(CoreOperator.defineImm, CoreOperator.divideMut).contains(co.val)) {
                newStatement();
                return Optional.empty();
            }
        } else if (isClauseBased()) {
            if (newItem instanceof CoreOperatorAST co) {
                if ((co.val == CoreOperator.arrow && (ctx == matchh || ctx == iff))
                    || (co.val == CoreOperator.pipe && (ctx == sumTypee) )
                    || (co.val == CoreOperator.colon && (ctx == structt))) {
                    newStatement();
                    return Optional.empty();
                }
            }
        }
        curr.add(newItem);
        // ind++;
        return Optional.empty();
    }

    /**
     * Try to add a new item to the current AST node. Returns a syntax error if unsuccessful.
     */
    public Optional<SyntaxError> addAtom(ASTUntypedBase newItem) {
        if (newItem instanceof ASTList lst && lst.isCoreForm()) {
            val ctxName = lst.ctx.toString();
            return Optional.of(new SyntaxError("Core form markers like " + ctxName.substring(0, ctxName.length() - 1)
                                                + " are only allowed in initial position"));
        }
        if (isAssignment()) {
            if (newItem instanceof CoreOperatorAST co && EnumSet.range(CoreOperator.defineImm, CoreOperator.divideMut).contains(co.val)) {
                newStatement();
                return Optional.empty();
            }
        } else if (isClauseBased()) {
            if (newItem instanceof CoreOperatorAST co) {
                if ((co.val == CoreOperator.arrow && (ctx == matchh || ctx == iff))
                    || (co.val == CoreOperator.pipe && (ctx == sumTypee) )
                    || (co.val == CoreOperator.colon && (ctx == structt))) {
                    newStatement();
                    return Optional.empty();
                }
            }
        }
        curr.add(newItem);
        // ind++;
        return Optional.empty();
    }

    public void newStatement() {
        if (!curr.isEmpty()) {
            curr = new ArrayList<>();
            data.add(curr);
        }
    }

    public void newStatement(ASTList newItem) {
        newStatement();
        curr.add(newItem);
    }

    /**
     * A list of tokens has ended, and we need to know whether this current AST is saturated.
     * Returns true if saturated, false if not yet saturated, and None if oversaturated (which shouldn't ever happen).
     */
    public Optional<Boolean> gotSaturated() {
        if (isBounded()) {
            int saturationLength = 0;
            if (ctx == iff) {
                saturationLength = 1;
            } else if (ctx == whilee || ctx == foreachh || ctx == matchh) {
                saturationLength = 2;
            } else if (ctx == forr) {
                saturationLength = 4;
            } else {
                saturationLength = 0;
            }
            if (itemsIngested < saturationLength) return Optional.of(false);
            if (itemsIngested == saturationLength) return Optional.of(true);
            return Optional.empty();
        } else if (isUnbounded()) {
            if ((ctx == iff || ctx == matchh)
                && data.size() >= 4 && data.get(data.size() - 2).get(0) instanceof ReservedLiteral rl && rl.val == ReservedWord.elsee) {
                return Optional.of(true);
            } else {
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

        if (ctx == iff) {
            // Must be of the form "a .func b -> b"
            if (len < 3) return false;
            if (le.val.get(len - 2) instanceof OperatorToken ot) {
                return (arraysEqual(Syntax.arrow, ot.val));
            } else return false;

        } else if (ctx == matchh) {
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
        } else if (ctx == structt) {
            // check for colon in second position, plus the following must be a sequence of type names
        } else if (ctx == sumTypee) {
            // TODO write a check for a sum type clause
        }
        return true;
    }

    public void newItemForBounded() {
        if (!isBounded()) return;
        newStatement();
        ++itemsIngested;
    }

    public boolean isFuncall() {
        return ctx == funcall;
    }

    public boolean isAssignment() {
        return EnumSet.range(assignImmutable, assignMutableDiv).contains(ctx);
    }

    public boolean isCoreForm() {
        return EnumSet.range(whilee, sumTypee).contains(ctx);
    }

    public boolean isUnbounded() {
        return EnumSet.range(iff, sumTypee).contains(ctx);
    }

    public boolean isBounded() {
        return EnumSet.range(whilee, typeDeclaration).contains(ctx);
    }

    public boolean isClauseBased() {
        return isBounded() || isUnbounded();
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
        if (ot.val.size() == 1 && ot.val.get(0) == OperatorSymb.equals) return Optional.of(assignImmutable);
        if (ot.val.size() == 2 && ot.val.get(1) == OperatorSymb.equals) {
            var f = ot.val.get(0);
            if (f == OperatorSymb.colon) return Optional.of(assignMutable);
            if (f == OperatorSymb.plus) return Optional.of(assignMutablePlus);
            if (f == OperatorSymb.minus) return Optional.of(assignMutableMinus);
            if (f == OperatorSymb.asterisk) return Optional.of(assignMutableTimes);
            if (f == OperatorSymb.slash) return Optional.of(assignMutableDiv);
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

public final static class CommentAST extends ASTUntypedBase {
    public String val;
    public CommentAST(String val) {
        this.val = val;
    }
}

}
