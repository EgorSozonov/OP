import "dart:typed_data";
import "package:o7/src/types/OperatorSymb.dart";
import "package:o7/src/types/ParenType.dart";
import "package:tuple/tuple.dart";
import "../utils/Stack.dart";

typedef ListLoc = Tuple2<ListExpr, int>;
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
class ListExpr extends Expr {
    List<Expr> val = [];
    ExprLexicalType pType;
    ListExpr(this.val, this.pType);

    @override
    bool operator ==(Object o) => (o is ListExpr) ? (val == o.val) : false;

    @override
    int get hashCode => val.hashCode;

    @override
    String toString() {
        if (this.val.isEmpty) return "Empty ListExpr";

        var result = StringBuffer();
        var backtrack = Stack<Tuple2<ListExpr, int>>();
        var curr = this;
        var i = 0;

        do {
            while (i < curr.val.length) {
                if (curr.val[i] is ListExpr) {
                    var listElem = (curr.val[i] as ListExpr);
                    if (listElem.val.isNotEmpty) {
                        backtrack.push(Tuple2(curr, i));
                        curr = listElem;
                        i = 0;
                        if (listElem.pType == ExprLexicalType.curlyBraces) {
                            result.write("{ ");
                        } else if (listElem.pType == ExprLexicalType.dataInitializer) {
                            result.write("[ ");
                        } else {
                            result.write("( ");
                        }

                    } else {
                        ++i;
                    }
                } else {
                    result.write(curr.val[i].toString());
                    result.write(", ");
                    ++i;
                }
            }
            if (backtrack.peek() != null) {
                if (curr.pType == ExprLexicalType.curlyBraces) {
                    result.write(" }, ");
                } else if (curr.pType == ExprLexicalType.dataInitializer) {
                    result.write(" ], ");
                } else {
                    result.write(" ), ");
                }
                var back = backtrack.pop();
                curr = back.item1;
                i = back.item2 + 1;
            }
        } while (backtrack.peek() != null || i < curr.val.length);

        return result.toString();
    }
}

/// A valid int64 token
class IntToken extends Expr {
    int val = 0;
    IntToken(this.val);

    @override
    bool operator ==(Object o) => (o is IntToken) ? (val == o.val) : false;

    @override
    int get hashCode => val.hashCode;

    @override
    String toString() {
        return "Int $val";
    }
}


/// A floating-point number token
class FloatToken extends Expr {
    double val = 0;
    FloatToken(this.val);

    @override
    String toString() {
        return "Float $val";
    }
}


/// Identifier or reserved word
class WordToken extends Expr {
    Uint8List val;
    WordToken(this.val);

    @override
    bool operator ==(Object o) => (o is WordToken) ? (String.fromCharCodes(this.val) == String.fromCharCodes(o.val)) : false;

    @override
    int get hashCode => val.hashCode;

    @override
    String toString() {
        return "Word ${String.fromCharCodes(this.val)}";
    }
}


class BoolToken extends Expr {
    bool val = false;
    BoolToken(this.val);

    @override
    bool operator ==(Object o) => (o is BoolToken) ? (val == o.val) : false;

    @override
    int get hashCode => val.hashCode;

    @override
    String toString() {
        return "Bool $val";
    }
}


class OperatorToken extends Expr {
    List<OperatorSymb> val;
    OperatorToken(this.val);

    @override
    bool operator ==(Object o) => (o is WordToken) ? (val == o.val) : false;

    @override
    int get hashCode => val.hashCode;

    @override
    String toString() {
        return "Operator $val";
    }
}


class StringToken extends Expr {
    String val;
    StringToken(this.val);

    @override
    bool operator ==(Object o) => (o is StringToken) ? (val == o.val) : false;

    @override
    int get hashCode => val.hashCode;

    @override
    String toString() {
        return "String $val";
    }
}


class CommentToken extends Expr {
    String val;
    CommentToken(this.val);

    @override
    bool operator ==(Object o) => (o is CommentToken) ? (val == o.val) : false;

    @override
    int get hashCode => val.hashCode;

    @override
    String toString() {
        return "Comment $val";
    }
}
