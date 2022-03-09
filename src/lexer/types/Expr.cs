using System;
using System.Collections.Generic;

namespace O7;

class Expr {
    static bool equal(Expr a, Expr b) {
        if (a is! ListExpr || b is! ListExpr) return a == b;

        var backtrackA = Stack<ListLoc>();
        var backtrackB = Stack<ListExpr>();

        backtrackA.push(Tuple2(a, 0));
        backtrackB.push(b);
        int i = 0;

        while (backtrackA.peek() != null) {
            if (backtrackB.peek() == null) return false;

            var listA = backtrackA.pop();
            var listB = backtrackB.pop();
            i = listA.item2;
            if (listA.item1.pType != listB.pType || listA.item1.val.length != listB.val.length) {
                print(listA.item1.val.length);
                print(listB.val.length);
                if (listA.item1.val.length == 2) {
                    print(listA.item1.val[0]);
                    print(listA.item1.val[1]);
                }

                return false;
            }

            while (i < listA.item1.val.length) {
                var itmA = listA.item1.val[i];
                var itmB = listB.val[i];

                if (itmA is ListExpr && itmB is ListExpr) {
                    backtrackA.push(Tuple2(listA.item1, i + 1));
                    backtrackB.push(listB);
                    listA = Tuple2(itmA, 0);
                    listB = itmB;

                    if (listA.item1.pType != listB.pType || listA.item1.val.length != listB.val.length) {
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
    List<Expr> val = new List<Expr>();
    ExprLexicalType pType;
    public ListExpr(List<Expr> val, ExprLexicalType pType) {
        this.val = val;
        this.pType = pType;
    }

    @override
    bool operator ==(object o) => (o is ListExpr) ? (val == o.val) : false;


    int get hashCode => val.hashCode;

    public override string ToString() {
        if (!this.val.Any()) return "Empty ListExpr";

        var result = new StringBuilder();
        var backtrack = Stack<Tuple<ListExpr, int>>();
        var curr = this;
        var i = 0;

        do {
            while (i < curr.val.Count) {
                if (curr.val[i] is ListExpr) {
                    var listElem = (curr.val[i] as ListExpr);
                    if (listElem.val.isNotEmpty) {
                        backtrack.push((curr, i));
                        curr = listElem;
                        i = 0;
                        if (listElem.pType == ExprLexicalType.curlyBraces) {
                            result.write("{ ");
                        } else if (listElem.pType == ExprLexicalType.dataInitializer) {
                            result.write("[ ");
                        } else if (listElem.pType == ExprLexicalType.parens){
                            result.write("( ");
                        } else {
                            result.write("| ");
                        }

                    } else {
                        result.write("!!empty!!");
                        ++i;
                    }
                } else {
                    result.write(curr.val[i].ToString());
                    result.write(", ");
                    ++i;
                }
            }
            if (backtrack.peek() != null) {
                if (curr.pType == ExprLexicalType.curlyBraces) {
                    result.write(" }, ");
                } else if (curr.pType == ExprLexicalType.dataInitializer) {
                    result.write(" ], ");
                } else if (curr.pType == ExprLexicalType.parens) {
                    result.write(" ), ");
                } else {
                    result.write(" |, ");
                }
                var back = backtrack.pop();
                curr = back.item1;
                i = back.item2 + 1;
            }
        } while (backtrack.peek() != null || i < curr.val.Count);

        return result.toString();
    }
}
