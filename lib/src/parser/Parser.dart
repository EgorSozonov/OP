import 'dart:collection';

import 'package:o7/src/lexer/types/OperatorSymb.dart';
import "package:o7/src/parser/types/ASTUntyped.dart";
import 'package:o7/src/parser/types/CoreOperator.dart';
import "package:o7/src/parser/types/ParseError.dart";
import "package:tuple/tuple.dart";
import "../lexer/types/Expr.dart";
import 'types/ReservedType.dart';


typedef ParseResult = Tuple2<ASTUntyped, ParseError?>;

class Parser {
    static ParseResult parse(Expr inp) {
        var reservedWords = getReservedMap();
        var coreOperators = getOperatorList();
        if (inp is ListExpr) {
            return Tuple2(IntLiteral(-1), null);
        } else {
            return Tuple2(parseAtom(inp, reservedWords, coreOperators), null);
        }

    }

    static HashMap<String, ReservedType> getReservedMap() {
        var result = HashMap<String, ReservedType>();
        for (var enumValue in ReservedType.values) {
            result[enumValue.name] = enumValue;
        }
        return result;
    }

    static List<Tuple2<List<OperatorSymb>, CoreOperator>> getOperatorList() {
        List<Tuple2<List<OperatorSymb>, CoreOperator>> result = [];
        result.add(Tuple2([OperatorSymb.plus],                              CoreOperator.plus));
        result.add(Tuple2([OperatorSymb.minus],                             CoreOperator.minus));
        result.add(Tuple2([OperatorSymb.asterisk],                          CoreOperator.times));
        result.add(Tuple2([OperatorSymb.slash],                             CoreOperator.divideBy));
        result.add(Tuple2([OperatorSymb.ampersand, OperatorSymb.ampersand], CoreOperator.and));
        result.add(Tuple2([OperatorSymb.pipe, OperatorSymb.pipe],           CoreOperator.or));
        result.add(Tuple2([OperatorSymb.exclamation],                       CoreOperator.not));
        result.add(Tuple2([OperatorSymb.ampersand],                         CoreOperator.bitwiseAnd));
        result.add(Tuple2([OperatorSymb.pipe],                              CoreOperator.bitwiseOr));
        result.add(Tuple2([OperatorSymb.tilde, OperatorSymb.ampersand],     CoreOperator.bitwiseNot));
        result.add(Tuple2([OperatorSymb.caret],                             CoreOperator.bitwiseXor));
        result.add(Tuple2([OperatorSymb.equals],                            CoreOperator.defineImm));
        result.add(Tuple2([OperatorSymb.colon, OperatorSymb.equals],        CoreOperator.defineMut));
        result.add(Tuple2([OperatorSymb.lt, OperatorSymb.minus],            CoreOperator.assignmentMut));
        result.add(Tuple2([OperatorSymb.plus, OperatorSymb.equals],         CoreOperator.plusMut));
        result.add(Tuple2([OperatorSymb.minus, OperatorSymb.equals],        CoreOperator.minusMut));
        result.add(Tuple2([OperatorSymb.asterisk, OperatorSymb.equals],     CoreOperator.timesMut));
        result.add(Tuple2([OperatorSymb.slash, OperatorSymb.equals],        CoreOperator.divideMut));
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
            return IntLiteral(inp.val);
        } else if (inp is FloatToken) {
            return FloatLiteral(inp.val);
        } else if (inp is StringToken) {
            return StringLiteral(inp.val);
        } else if (inp is WordToken) {
            var str = String.fromCharCodes(inp.val);
            if (str == "true") return BoolLiteral(true);
            if (str == "false") return BoolLiteral(false);
            if (reservedWords.containsKey(str)) {
                return Reserved(reservedWords[str]!);
            } else {
                return Ident(str);
            }
        } else if (inp is OperatorToken) {
            for (var oper in coreOperators) {
                if (arraysEqual(inp.val, oper.item1)) {
                    return CoreOperatorAST(oper.item2);
                }
            }
            return OperatorAST(inp.val);
        } else {
            // should never happen
            return IntLiteral(-1);
        }
    }
}
