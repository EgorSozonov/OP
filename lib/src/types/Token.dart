import 'package:tuple/tuple.dart';

import '../utils/Stack.dart';

class Expr {}


class ListExpr extends Expr {
    List<Expr> val = [];
    ListExpr(this.val);

    @override
    bool operator ==(Object o) => (o is ListExpr) ? (val == o.val) : false;

    @override
    int get hashCode => val.hashCode;

    @override
    String toString() {
        if (this.val.isEmpty) return "Empty ListExpr";

        var result = StringBuffer();
        var backtrack = Stack<Tuple2<List<Expr>, int>>();
        var curr = this.val;
        var i = 0;

        do {
            while (i < curr.length) {
                if (curr[i] is ListExpr) {
                    backtrack.push(Tuple2(curr, i));
                    curr = (curr[i] as ListExpr).val;
                    i = 0;
                    result.write("{ ");
                } else {
                    result.write(curr[i].toString());
                    result.write(", ");
                    ++i;
                }
            }
            if (backtrack.peek() != null) {
                var back = backtrack.pop();

                curr = back.item1;
                i = back.item2 + 1;
                result.write(" }");
            }
        } while (backtrack.peek() != null || i < curr.length);

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
