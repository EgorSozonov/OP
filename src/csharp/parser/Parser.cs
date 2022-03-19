namespace O7;

using System;
using System.Collections.Generic;
using System.Text;
using static ByteArrayUtils;
using ParseResult = System.Tuple<ASTUntyped, ParseError>;

public class PreParser {
    public static ParseResult parse(Expr inp) {
        var resultCurr = new ListStatements();
        var result = resultCurr;
        var resultBacktrack = new Stack<Tuple<ListStatements, int>>();
        var reservedWords = getReservedMap();
        var coreOperators = getOperatorList();
        var backtrack = new Stack<Tuple<ListExpr, int>>();

        if (inp is ListExpr le) {
            int i = 0;
            int j = 0;
            backtrack.push(new Tuple<ListExpr, int>(le, i));
            resultBacktrack.push(new Tuple<ListStatements, int>(resultCurr, i));
            var curr = le;
            while (backtrack.peek() != null) {
                var back = backtrack.pop();
                curr = back.Item1;
                i = back.Item2;

                var backResult = resultBacktrack.pop();
                resultCurr = backResult.Item1;
                j = backResult.Item2;

                while (i < curr.val.Count) {
                    l(i.ToString());
                    if (curr.val[i] is ListExpr le2) {
                        backtrack.push(new Tuple<ListExpr, int>(curr, i + 1));
                        resultBacktrack.push(new Tuple<ListStatements, int>(resultCurr, j + 1));

                        if (le2.pType == ExprLexicalType.curlyBraces) {
                            ListStatements newList = new ListStatements(SubexprType.list);
                            resultCurr.val.Add(newList);
                            newList.sType = SubexprType.list;

                            resultCurr = newList;
                        } else if (le2.pType == ExprLexicalType.dataInitializer) {
                            var newElem = new DataInitializer();
                            resultCurr.val.Add(newElem);

                            resultCurr = newElem;
                        } else { // if (le2.pType == ExprLexicalType.parens || le2.pType == ExprLexicalType.statement) {
                            ListStatements newStatement = new ListStatements(SubexprType.parens);
                            resultCurr.val.Add(newStatement);
                            result = newStatement;
                        }
                        curr = le2;
                        i = 0;
                        j = 0;
                    } else {
                        resultCurr.val.Add(parseAtom(curr.val[i], reservedWords, coreOperators));
                        ++i;
                        ++j;
                    }
                }

            }
        } else {
            return new Tuple<ASTUntyped, ParseError>(parseAtom(inp, reservedWords, coreOperators), null);
        }
        return new Tuple<ASTUntyped, ParseError>(result, null);

    }

    static Dictionary<String, ReservedType> getReservedMap() {
        var result = new Dictionary<String, ReservedType>();
        foreach (ReservedType enumValue in Enum.GetValues(typeof(ReservedType))) {
            result[enumValue.ToString()] = enumValue;
        }
        return result;
    }

    static List<Tuple<List<OperatorSymb>, CoreOperator>> getOperatorList() {
        var result = new List<Tuple<List<OperatorSymb>, CoreOperator>>();
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.plus },                             CoreOperator.plus));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.minus},                             CoreOperator.minus));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.asterisk},                          CoreOperator.times));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.slash},                             CoreOperator.divideBy));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.ampersand, OperatorSymb.ampersand}, CoreOperator.and));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.pipe, OperatorSymb.pipe},           CoreOperator.or));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.exclamation},                       CoreOperator.not));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.ampersand},                         CoreOperator.bitwiseAnd));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.pipe},                              CoreOperator.bitwiseOr));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.tilde, OperatorSymb.ampersand},     CoreOperator.bitwiseNot));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.caret},                             CoreOperator.bitwiseXor));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.equals},                            CoreOperator.defineImm));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.colon, OperatorSymb.equals},        CoreOperator.defineMut));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.lt, OperatorSymb.minus},            CoreOperator.assignmentMut));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.plus, OperatorSymb.equals},         CoreOperator.plusMut));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.minus, OperatorSymb.equals},        CoreOperator.minusMut));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.asterisk, OperatorSymb.equals},     CoreOperator.timesMut));
        result.Add(new Tuple<List<OperatorSymb>, CoreOperator>(new List<OperatorSymb>() { OperatorSymb.slash, OperatorSymb.equals},        CoreOperator.divideMut));
        return result;
    }


    static bool arraysEqual(List<OperatorSymb> a, List<OperatorSymb> b) {
        if (a.isEmpty() && a.Count != b.Count) return false;
        for (int i = 0; i < a.Count; ++i) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }


    static ASTUntyped parseAtom(Expr inp,
                                Dictionary<String, ReservedType> reservedWords,
                                List<Tuple<List<OperatorSymb>, CoreOperator>> coreOperators) {
        if (inp is IntToken it) {
            return new IntLiteral(it.val);
        } else if (inp is FloatToken ft) {
            return new FloatLiteral(ft.val);
        } else if (inp is StringToken st) {
            return new StringLiteral(st.val);
        } else if (inp is WordToken wt) {
            var str = Encoding.ASCII.GetString(wt.val);
            if (str == "true") return new BoolLiteral(true);
            if (str == "false") return new BoolLiteral(false);
            if (reservedWords.TryGetValue(str, out ReservedType resWord)) {
                return new Reserved(resWord);
            } else {
                return new Ident(str);
            }
        } else if (inp is OperatorToken ot) {
            foreach (var oper in coreOperators) {
                if (arraysEqual(ot.val, oper.Item1)) {
                    return new CoreOperatorAST(oper.Item2);
                }
            }
            return new OperatorAST(ot.val);
        } else {
            // should never happen
            return new IntLiteral(-1);
        }
    }
}
