namespace O7;

using System;
using System.Collections.Generic;
using ParseResult = Tuple<ASTUntyped, ParseError>;

class PreParser {
    static ParseResult parse(Expr inp) {
        var newStatement = new Statement([]);
        var result = new ListStatements([newStatement]);
        var reservedWords = getReservedMap();
        var coreOperators = getOperatorList();
        var backtrack = new Stack<Tuple<ListExpr, int>>();

        if (inp is ListExpr) {
            int i = 0;
            backtrack.push(Tuple2(inp, i));
            var curr = inp;
            while (backtrack.peek() != null) {
                var back = backtrack.pop();
                curr = back.item1;
                i = back.item2;
                while (i < curr.val.length) {
                    if (curr.val[i] is ListExpr) {

                    } else {
                        newStatement.val.add(parseAtom(curr.val[i], reservedWords, coreOperators));
                    }
                }
            }
        } else {
            return new Tuple(parseAtom(inp, reservedWords, coreOperators), null);
        }
        return new Tuple(result, null);

    }

    static HashMap<String, ReservedType> getReservedMap() {
        var result = new HashMap<String, ReservedType>();
        for (var enumValue in ReservedType.values) {
            result[enumValue.name] = enumValue;
        }
        return result;
    }

    static List<Tuple<List<OperatorSymb>, CoreOperator>> getOperatorList() {
        var result = new List<Tuple<List<OperatorSymb>, CoreOperator>>();
        result.add(new Tuple([OperatorSymb.plus],                              CoreOperator.plus));
        result.add(new Tuple([OperatorSymb.minus],                             CoreOperator.minus));
        result.add(new Tuple([OperatorSymb.asterisk],                          CoreOperator.times));
        result.add(new Tuple([OperatorSymb.slash],                             CoreOperator.divideBy));
        result.add(new Tuple([OperatorSymb.ampersand, OperatorSymb.ampersand], CoreOperator.and));
        result.add(new Tuple([OperatorSymb.pipe, OperatorSymb.pipe],           CoreOperator.or));
        result.add(new Tuple([OperatorSymb.exclamation],                       CoreOperator.not));
        result.add(new Tuple([OperatorSymb.ampersand],                         CoreOperator.bitwiseAnd));
        result.add(new Tuple([OperatorSymb.pipe],                              CoreOperator.bitwiseOr));
        result.add(new Tuple([OperatorSymb.tilde, OperatorSymb.ampersand],     CoreOperator.bitwiseNot));
        result.add(new Tuple([OperatorSymb.caret],                             CoreOperator.bitwiseXor));
        result.add(new Tuple([OperatorSymb.equals],                            CoreOperator.defineImm));
        result.add(new Tuple([OperatorSymb.colon, OperatorSymb.equals],        CoreOperator.defineMut));
        result.add(new Tuple([OperatorSymb.lt, OperatorSymb.minus],            CoreOperator.assignmentMut));
        result.add(new Tuple([OperatorSymb.plus, OperatorSymb.equals],         CoreOperator.plusMut));
        result.add(new Tuple([OperatorSymb.minus, OperatorSymb.equals],        CoreOperator.minusMut));
        result.add(new Tuple([OperatorSymb.asterisk, OperatorSymb.equals],     CoreOperator.timesMut));
        result.add(new Tuple([OperatorSymb.slash, OperatorSymb.equals],        CoreOperator.divideMut));
        return result;
    }


    static bool arraysEqual(List<OperatorSymb> a, List<OperatorSymb> b) {
        if (a.isEmpty && a.length != b.length) return false;
        for (int i = 0; i < a.length; ++i) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }


    static ASTUntyped parseAtom(Expr inp,
                                HashMap<String, ReservedType> reservedWords,
                                List<Tuple2<List<OperatorSymb>, CoreOperator>> coreOperators) {
        if (inp is IntToken) {
            return new IntLiteral(inp.val);
        } else if (inp is FloatToken) {
            return new FloatLiteral(inp.val);
        } else if (inp is StringToken) {
            return new StringLiteral(inp.val);
        } else if (inp is WordToken) {
            var str = String.fromCharCodes(inp.val);
            if (str == "true") return BoolLiteral(true);
            if (str == "false") return BoolLiteral(false);
            if (reservedWords.containsKey(str)) {
                return new Reserved(reservedWords[str]!);
            } else {
                return new Ident(str);
            }
        } else if (inp is OperatorToken) {
            for (var oper in coreOperators) {
                if (arraysEqual(inp.val, oper.item1)) {
                    return new CoreOperatorAST(oper.item2);
                }
            }
            return new OperatorAST(inp.val);
        } else {
            // should never happen
            return new IntLiteral(-1);
        }
    }
}
