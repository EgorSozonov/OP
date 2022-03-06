import 'package:o7/src/types/ParenType.dart';
import 'package:tuple/tuple.dart';

import '../utils/Stack.dart';

class Expr {}


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
                        if (listElem.pType == ExprLexicalType.CurlyBraces) {
                            result.write("{ ");
                        } else if (listElem.pType == ExprLexicalType.DataInitializer) {
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
                if (curr.pType == ExprLexicalType.CurlyBraces) {
                    result.write(" }, ");
                } else if (curr.pType == ExprLexicalType.DataInitializer) {
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
