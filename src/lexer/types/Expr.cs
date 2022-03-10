namespace O7;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

class Expr {
    static bool equal(Expr a, Expr b) {
        if (a is not ListExpr || b is not ListExpr) return a == b;

        var backtrackA = new Stack<Tuple<ListExpr, int>>();
        var backtrackB = new Stack<ListExpr>();

        backtrackA.push(new Tuple<ListExpr, int>((ListExpr)a, 0));
        backtrackB.push((ListExpr)b);
        int i = 0;

        while (backtrackA.peek() != null) {
            if (backtrackB.peek() == null) return false;

            var listA = backtrackA.pop();
            var listB = backtrackB.pop();
            i = listA.Item2;
            if (listA.Item1.pType != listB.pType || listA.Item1.val.Count != listB.val.Count) {
                return false;
            }

            while (i < listA.Item1.val.Count) {
                var itmA = listA.Item1.val[i];
                var itmB = listB.val[i];

                if (itmA is ListExpr && itmB is ListExpr) {
                    backtrackA.push(new Tuple<ListExpr, int>(listA.Item1, i + 1));
                    backtrackB.push(listB);
                    listA = new Tuple<ListExpr, int>((ListExpr)itmA, 0);
                    listB = (ListExpr)itmB;

                    if (listA.Item1.pType != listB.pType || listA.Item1.val.Count != listB.val.Count) {
                        return false;
                    }
                    i = 0;
                } else if (itmA != itmB) {
                    return false;
                } else {
                    ++i;
                }
            }
        }
        return true;
    }
}

/// A list of tokens, which can be a statement, a list of statements,
/// or a data initializer
class ListExpr : Expr {
    public List<Expr> val;
    public ExprLexicalType pType;

    public ListExpr(ExprLexicalType pType) {
        this.val = new List<Expr>();
        this.pType = pType;
    }

    public override bool Equals(object o) => (o is ListExpr) ? (val == ((ListExpr)o).val) : false;
    public override int GetHashCode() => this.val.Count;

    public override string ToString() {
        if (!this.val.Any()) return "Empty ListExpr";

        var result = new StringBuilder();
        var backtrack = new Stack<Tuple<ListExpr, int>>();
        var curr = this;
        var i = 0;

        do {
            while (i < curr.val.Count) {
                if (curr.val[i] is ListExpr) {
                    var listElem = (curr.val[i] as ListExpr);
                    if (listElem.val.Any()) {
                        backtrack.push(new Tuple<ListExpr, int>(curr, i));
                        curr = listElem;
                        i = 0;
                        if (listElem.pType == ExprLexicalType.curlyBraces) {
                            result.Append("{ ");
                        } else if (listElem.pType == ExprLexicalType.dataInitializer) {
                            result.Append("[ ");
                        } else if (listElem.pType == ExprLexicalType.parens){
                            result.Append("( ");
                        } else {
                            result.Append("| ");
                        }

                    } else {
                        result.Append("!!empty!!");
                        ++i;
                    }
                } else {
                    result.Append(curr.val[i].ToString());
                    result.Append(", ");
                    ++i;
                }
            }
            if (backtrack.peek() != null) {
                if (curr.pType == ExprLexicalType.curlyBraces) {
                    result.Append(" }, ");
                } else if (curr.pType == ExprLexicalType.dataInitializer) {
                    result.Append(" ], ");
                } else if (curr.pType == ExprLexicalType.parens) {
                    result.Append(" ), ");
                } else {
                    result.Append(" |, ");
                }
                var back = backtrack.pop();
                curr = back.Item1;
                i = back.Item2 + 1;
            }
        } while (backtrack.peek() != null || i < curr.val.Count);

        return result.ToString();
    }
}


/// A valid int64 token
class IntToken : Expr {
    public int val = 0;
    public IntToken(int val) {
        this.val = val;
    }


    public override bool Equals(Object o) => (o is IntToken) ? (val == ((IntToken)o).val) : false;


    public override int GetHashCode() => this.val;


    public override string ToString() {
        return $"Int {val}";
    }
}


/// A floating-point number token
class FloatToken : Expr {
    public double val = 0;

    public FloatToken(double val) {
        this.val = val;
    }


    public override String ToString() {
        return $"Float {val}";
    }
}


/// Identifier or reserved word
class WordToken : Expr {
    public byte[] val;
    public WordToken(byte[] val) {
        this.val = val;
    }
    // TODO array equality
    public override bool Equals(Object o) => (o is WordToken wt) ? ByteArrayUtils.areEqual(this.val, wt.val) : false;

    public override int GetHashCode() => val.Length;

    public override String ToString() {
        return $"Word {Encoding.ASCII.GetString(this.val)}";
    }
}


class OperatorToken : Expr {
    public List<OperatorSymb> val;
    public OperatorToken(List<OperatorSymb> val) {
        this.val = val;
    }

    public override bool Equals(Object o) {
        if (o is not OperatorToken) return false;
        var oth = (OperatorToken)o;
        if (this.val.Count != oth.val.Count) return false;
        for (int i = 0; i < this.val.Count; ++i) {
            if (this.val[i] != oth.val[i]) return false;
        }
        return true;
    }

    public override int GetHashCode() => val.Count;

    public override string ToString() {
        return $"Operator {val}";
    }
}


class StringToken : Expr {
    public String val;
    public StringToken(String val) {
        this.val = val;
    }

    public override bool Equals(Object o) => (o is StringToken oth) ? (val == oth.val) : false;

    public override int GetHashCode() => val.GetHashCode();

    public string ToString() {
        return $"String {val}";
    }
}


class CommentToken : Expr {
    public String val;

    public CommentToken(String val) {
        this.val = val;
    }

    public override bool Equals(Object o) => (o is CommentToken oth) ? (val == oth.val) : false;

    public override int GetHashCode() => val.GetHashCode();

    public override String ToString() {
        return $"Comment {val}";
    }
}
