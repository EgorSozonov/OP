package tech.sozonov.o7.lexer.types;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import tech.sozonov.o7.utils.Stack;
import tech.sozonov.o7.utils.Tuple;
import lombok.val;


public class Expr {

public static class ExprBase {
    public static boolean equal(ExprBase a, ExprBase b) {
        if (!(a instanceof ListExpr) || !(b instanceof ListExpr)) return a == b;

        var backtrackA = new Stack<Tuple<ListExpr, Integer>>();
        var backtrackB = new Stack<ListExpr>();

        backtrackA.push(new Tuple<ListExpr, Integer>((ListExpr)a, 0));
        backtrackB.push((ListExpr)b);
        int i = 0;

        while (backtrackA.peek() != null) {
            if (backtrackB.peek() == null) return false;

            var listA = backtrackA.pop();
            var listB = backtrackB.pop();
            i = listA.i1;
            if (listA.i0.lType != listB.lType || listA.i0.val.size() != listB.val.size()) {
                return false;
            }

            while (i < listA.i0.val.size()) {
                var itmA = listA.i0.val.get(i);
                var itmB = listB.val.get(i);

                if (itmA instanceof ListExpr le1 && itmB instanceof ListExpr le2) {
                    listA = new Tuple<>(le1, 0);
                    listB = le2;

                    if (listA.i0.lType != listB.lType || listA.i0.val.size() != listB.val.size()) {
                        return false;
                    }
                    backtrackA.push(new Tuple<>(listA.i0, i + 1));
                    backtrackB.push(listB);
                    i = 0;
                } else if (!itmA.equals(itmB)) {
                    return false;
                } else {
                    ++i;
                }
            }
        }
        return true;
    }

    /**
     * Wraps one token the way it would be wrapped when lexing a real text input.
     */
    public static ExprBase wrapOneToken(ExprBase token) {
        val result = new ListExpr(LexicalContext.curlyBraces);
        val stmt = new ListExpr(LexicalContext.statement);
        stmt.val.add(token);
        result.val.add(stmt);
        return result;
    }

    /**
     * Wraps a list of tokens into a statement the way it would be wrapped when lexing a real text input.
     */
    public static ExprBase wrapListTokens(List<ExprBase> tokens) {
        val result = new ListExpr(LexicalContext.curlyBraces);
        val stmt = new ListExpr(LexicalContext.statement);
        stmt.val = tokens;
        result.val.add(stmt);
        return result;
    }

    /**
     * Simplifies checking of equality between results of lexing and expected values.
     */
    public static boolean checkEquality(ExprBase a, Tuple<ExprBase, LexError> b) {
        return b.i1 == null && ListExpr.equal(a, b.i0);
    }
}

/** A list of tokens, which can be a statement, a list of statements,
 * or a data initializer
*/
public final static class ListExpr extends ExprBase {
    public List<ExprBase> val;
    public LexicalContext lType;

    public ListExpr(LexicalContext pType) {
        this.val = new ArrayList<ExprBase>();
        this.lType = pType;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ListExpr le) ? (val == le.val) : false;
    }

    @Override
    public int hashCode() {
        return this.val.size();
    }

    @Override
    public String toString() {
        if (this.val.isEmpty()) return "Empty ListExpr";

        var result = new StringBuilder();
        var backtrack = new Stack<Tuple<Expr.ListExpr, Integer>>();
        var curr = this;
        var i = 0;

        do {
            while (i < curr.val.size()) {
                if (curr.val.get(i) instanceof Expr.ListExpr listElem) {
                    if (!listElem.val.isEmpty()) {
                        backtrack.push(new Tuple<ListExpr, Integer>(curr, i));
                        curr = listElem;
                        i = 0;
                        if (listElem.lType == LexicalContext.curlyBraces) {
                            result.append("{ ");
                        } else if (listElem.lType == LexicalContext.dataInitializer) {
                            result.append("[ ");
                        } else if (listElem.lType == LexicalContext.parens){
                            result.append("( ");
                        } else {
                            result.append("| ");
                        }

                    } else {
                        result.append("!!empty " + listElem.lType + "!! ");
                        ++i;
                    }
                } else {
                    result.append(curr.val.get(i).toString());
                    result.append(", ");
                    ++i;
                }
            }
            if (backtrack.peek() != null) {
                if (curr.lType == LexicalContext.curlyBraces) {
                    result.append(" }, ");
                } else if (curr.lType == LexicalContext.dataInitializer) {
                    result.append(" ], ");
                } else if (curr.lType == LexicalContext.parens) {
                    result.append(" ), ");
                } else {
                    result.append(" |, ");
                }
                val back = backtrack.pop();
                curr = back.i0;
                i = back.i1 + 1;
            }
        } while (backtrack.peek() != null || i < curr.val.size());

        return result.toString();
    }
}


public final static class IntToken extends ExprBase {
    public long val = 0;
    public IntToken(long val) {
        this.val = val;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof IntToken) ? (val == ((IntToken)o).val) : false;
    }

    @Override
    public int hashCode() {
        return (int)this.val;
    }


    @Override
    public String toString() {
        return "Int " + val;
    }
}


public final static class FloatToken extends ExprBase {
    public double val = 0;

    public FloatToken(double val) {
        this.val = val;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof FloatToken ft) ? this.val == ft.val : false;
    }

    @Override
    public int hashCode() {
        return (int)val;
    }

    @Override
    public String toString() {
        return "Float " + val;
    }
}


/**
 * Identifier or a reserved word.
 * May be capitalized, may contain an underscore in initial position but not otherwise.
 * May contain dots, but not in the initial position.
 */
public final static class WordToken extends ExprBase {
    public String val;
    public String capitalizedPrefix;


    public WordToken(String txt) {
        this.val = txt;
        capitalizedPrefix = "";
    }

    public WordToken(String capPref, String txt) {
        this.capitalizedPrefix = capPref;
        this.val = txt;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof WordToken wt) ? this.val.equals(wt.val) && this.capitalizedPrefix.equals(wt.capitalizedPrefix) : false;
    }

    @Override
    public int hashCode() {
        return val.length();
    }

    @Override
    public String toString() {
        return "Word " + (capitalizedPrefix == "" ? this.val : capitalizedPrefix + "." + val);
    }
}

/**
 * A dot followed by an identifier. Used for function names in calls.
 * May be capitalized, may contain underscores initially but not otherwise.
 * May contain dots, but not in the initial position.
 */
public final static class DotWordToken extends ExprBase {
    public String val;
    public String capitalizedPrefix;


    public DotWordToken(WordToken wt) {
        this.val = wt.val;
        this.capitalizedPrefix = wt.capitalizedPrefix;
    }

    public DotWordToken(String txt) {
        this.val = txt;
        this.capitalizedPrefix = "";
    }

    public DotWordToken(String capPrefix, String txt) {
        this.val = txt;
        this.capitalizedPrefix = capPrefix;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof DotWordToken wt) ? val.equals(wt.val) && capitalizedPrefix.equals(wt.capitalizedPrefix) : false;
    }

    @Override
    public int hashCode() {
        return val.length();
    }

    @Override
    public String toString() {
        return "DotWord " + (capitalizedPrefix == "" ? this.val : capitalizedPrefix + "." + val);
    }
}


public final static class OperatorToken extends ExprBase {
    public List<OperatorSymb> val;
    public OperatorToken(List<OperatorSymb> val) {
        this.val = val;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OperatorToken)) return false;
        var oth = (OperatorToken)o;
        if (this.val.size() != oth.val.size()) return false;
        for (int i = 0; i < this.val.size(); ++i) {
            if (this.val.get(i) != oth.val.get(i)) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return val.size();
    }

    @Override
    public String toString() {
        return "Operator [" + String.join(" ", val.stream().map(OperatorSymb::toString).collect(Collectors.toList())) + "]";
    }
}


public final static class StringToken extends ExprBase {
    public String val;
    public StringToken(String val) {
        this.val = val;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof StringToken oth) ? (val == oth.val) : false;
    }

    @Override
    public int hashCode() {
        return val.hashCode();
    }

    @Override
    public String toString() {
        return "String " + val;
    }
}


public final static class CommentToken extends ExprBase {
    public String val;

    public CommentToken(String val) {
        this.val = val;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CommentToken oth) ? (val == oth.val) : false;
    }

    @Override
    public int hashCode() {
        return val.hashCode();
    }

    @Override
    public String toString() {
        return "Comment " + val;
    }
}

}
