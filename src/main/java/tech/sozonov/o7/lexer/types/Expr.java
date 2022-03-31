package main.java.tech.sozonov.o7.lexer.types;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import main.java.tech.sozonov.o7.utils.Stack;
import java.nio.charset.StandardCharsets;
import main.java.tech.sozonov.o7.utils.ByteArrayUtils;
import main.java.tech.sozonov.o7.utils.Tuple;

public class Expr {

public static class ExprBase {}
/** A list of tokens, which can be a statement, a list of statements,
 * or a data initializer
*/
public final class ListExpr extends ExprBase {
    public List<ExprBase> val;
    public ExprLexicalType pType;

    public ListExpr(ExprLexicalType pType) {
        this.val = new ArrayList<ExprBase>();
        this.pType = pType;
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
                        if (listElem.pType == ExprLexicalType.curlyBraces) {
                            result.append("{ ");
                        } else if (listElem.pType == ExprLexicalType.dataInitializer) {
                            result.append("[ ");
                        } else if (listElem.pType == ExprLexicalType.parens){
                            result.append("( ");
                        } else {
                            result.append("| ");
                        }

                    } else {
                        result.append("!!empty " + listElem.pType + "!! ");
                        ++i;
                    }
                } else {
                    result.append(curr.val.get(i).toString());
                    result.append(", ");
                    ++i;
                }
            }
            if (backtrack.peek() != null) {
                if (curr.pType == ExprLexicalType.curlyBraces) {
                    result.append(" }, ");
                } else if (curr.pType == ExprLexicalType.dataInitializer) {
                    result.append(" ], ");
                } else if (curr.pType == ExprLexicalType.parens) {
                    result.append(" ), ");
                } else {
                    result.append(" |, ");
                }
                var back = backtrack.pop();
                curr = back.item0;
                i = back.item1 + 1;
            }
        } while (backtrack.peek() != null || i < curr.val.size());

        return result.toString();
    }
}


/// A valid int64 token
public final class IntToken extends ExprBase {
    public int val = 0;
    public IntToken(int val) {
        this.val = val;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof IntToken) ? (val == ((IntToken)o).val) : false;
    }

    @Override
    public int hashCode() {
        return this.val;
    }


    @Override
    public String toString() {
        return "Int " + val;
    }
}


/// A floating-point number token
public final class FloatToken extends ExprBase {
    public double val = 0;

    public FloatToken(double val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "Float " + val;
    }
}


/// Identifier or reserved word
public final class WordToken extends ExprBase {
    public byte[] val;
    public WordToken(byte[] val) {
        this.val = val;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof WordToken wt) ? ByteArrayUtils.areEqual(this.val, wt.val) : false;
    }

    @Override
    public int hashCode() {
        return val.length;
    }

    @Override
    public String toString() {
        return "Word " + (new String(this.val, StandardCharsets.US_ASCII));
    }
}


public final class OperatorToken extends ExprBase {
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
        return "Operator [" + String.join(" ", val) + "]";
    }
}


public final class StringToken extends ExprBase {
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


public final class CommentToken extends ExprBase {
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
