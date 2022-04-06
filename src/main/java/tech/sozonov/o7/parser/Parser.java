package tech.sozonov.o7.parser;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tech.sozonov.o7.lexer.types.Expr;
import tech.sozonov.o7.lexer.types.ExprLexicalType;
import tech.sozonov.o7.lexer.types.OperatorSymb;
import tech.sozonov.o7.lexer.types.Expr.*;
import tech.sozonov.o7.parser.types.ReservedType;
import tech.sozonov.o7.parser.types.SubexprType;
import tech.sozonov.o7.parser.types.ASTUntyped.*;
import tech.sozonov.o7.parser.types.CoreOperatorPackage.AssignmentType;
import tech.sozonov.o7.parser.types.CoreOperatorPackage.CoreOperator;
import tech.sozonov.o7.parser.types.ParseError.*;
import tech.sozonov.o7.utils.Tuple;
import tech.sozonov.o7.utils.Stack;
import tech.sozonov.o7.utils.Either;
import lombok.val;


public class Parser {
    public static Tuple<ASTUntypedBase, ParseErrorBase> parse(ExprBase inp) {
        var resultCurr = new ListStatements();
        var result = resultCurr;

        var backtrack = new Stack<Tuple<ListExpr, Integer>>();
        var resultBacktrack = new Stack<Tuple<ListStatements, Integer>>();

        var reservedWords = getReservedMap();
        var coreOperators = getOperatorList();

        if (!(inp instanceof ListExpr le)) {
            return new Tuple<ASTUntypedBase, ParseErrorBase>(parseAtom(inp, reservedWords, coreOperators), null);
        }

        int i = 0;
        int j = 0;
        backtrack.push(new Tuple<ListExpr, Integer>(le, i));
        resultBacktrack.push(new Tuple<ListStatements, Integer>(resultCurr, i));

        var curr = le;
        while (backtrack.peek() != null) {
            val back = backtrack.pop();
            curr = back.item0;
            i = back.item1;

            var backResult = resultBacktrack.pop();
            resultCurr = backResult.item0;
            j = backResult.item1;

            while (i < curr.val.size()) {
                if (!(curr.val.get(i) instanceof ListExpr)) {
                    resultCurr.val.add(parseAtom(curr.val.get(i), reservedWords, coreOperators));
                    ++i;
                    ++j;
                }
                val le2 = (ListExpr)curr.val.get(i);

                backtrack.push(new Tuple<ListExpr, Integer>(curr, i + 1));
                resultBacktrack.push(new Tuple<ListStatements, Integer>(resultCurr, j + 1));

                if (le2.pType == ExprLexicalType.curlyBraces) {
                    ListStatements newList = new ListStatements(SubexprType.curlyBraces);
                    resultCurr.val.add(newList);
                    resultCurr = newList;
                } else if (le2.pType == ExprLexicalType.dataInitializer) {
                    var newElem = new ListStatements(SubexprType.dataInitializer);
                    resultCurr.val.add(newElem);

                    resultCurr = newElem;
                } else if (le2.pType == ExprLexicalType.parens) {
                    ListStatements newStatement = new ListStatements(SubexprType.parens);
                    resultCurr.val.add(newStatement);
                    resultCurr = newStatement;
                } else if (le2.pType == ExprLexicalType.statement) {
                    if (le2.val.size() >= 3 && le2.val.get(1) instanceof OperatorToken ot) {
                        var mbAssType = getAssignmentType(ot);
                        var identParse = parseAtom(le2.val.get(0), reservedWords, coreOperators);
                        if (identParse instanceof Ident ident && mbAssType != null) {
                            ListStatements rightSide = new ListStatements(SubexprType.statement);
                            var assignment = new Assignment(ident, (AssignmentType) mbAssType, rightSide);
                            resultCurr.val.add(assignment);
                            resultBacktrack.push(new Tuple<ListStatements, Integer>(resultCurr, i + 1));
                            resultCurr = rightSide;
                            i = 2;
                        } else {
                            return new Tuple<ASTUntypedBase, ParseErrorBase>(result,
                                new AssignmentError("Erroneous assignment expression, must be: identifier assignmentOper anyExpression, where assignmentOper instanceof one of: = := += -= *= /="));
                        }
                    } else {
                        ListStatements newStatement = new ListStatements(SubexprType.statement);
                        resultCurr.val.add(newStatement);
                        resultCurr = newStatement;
                    }
                }

                curr = le2;
                i = 0;
                j = 0;

            }
        }
        return new Tuple<ASTUntypedBase, ParseErrorBase>(result, null);
    }

    static boolean isReserved(ListExpr expr, Map<String, ReservedType> reserveds) {
        if (!(expr.val.get(0) instanceof WordToken)) return false;
        val word = (WordToken)expr.val.get(0);
        return reserveds.containsKey(word.val);
    }

    static boolean isAssignment(ListExpr expr) {
        if (expr.val.size() >= 3 && expr.val.get(1) instanceof OperatorToken ot) {
            var mbAssType = getAssignmentType(ot);
            return mbAssType != null;
        } else {
            return false;
        }
    }

    static AssignmentType getAssignmentType(OperatorToken ot) {
        if (ot.val.size() == 1 && ot.val.get(0) == OperatorSymb.equals) return AssignmentType.immutableDef;
        if (ot.val.size() == 2 && ot.val.get(1) == OperatorSymb.equals) {
            var f = ot.val.get(0);
            if (f == OperatorSymb.colon) return AssignmentType.mutableAssignment;
            if (f == OperatorSymb.plus) return AssignmentType.mutablePlus;
            if (f == OperatorSymb.minus) return AssignmentType.mutablePlus;
            if (f == OperatorSymb.asterisk) return AssignmentType.mutablePlus;
            if (f == OperatorSymb.slash) return AssignmentType.mutablePlus;
        }
        return null;
    }

    static Map<String, ReservedType> getReservedMap() {
        val result = new HashMap<String, ReservedType>();
        EnumSet.allOf(ReservedType.class).forEach(enumValue -> {
            val nm = enumValue.toString();
            result.put(nm.substring(0, nm.length() - 1), enumValue);
        });

        return result;
    }

    static List<Tuple<List<OperatorSymb>, CoreOperator>> getOperatorList() {
        var result = new ArrayList<Tuple<List<OperatorSymb>, CoreOperator>>();
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.plus),                              CoreOperator.plus));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.minus),                             CoreOperator.minus));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.asterisk),                          CoreOperator.times));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.slash),                             CoreOperator.divideBy));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.ampersand, OperatorSymb.ampersand), CoreOperator.and));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.pipe, OperatorSymb.pipe),           CoreOperator.or));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.exclamation),                       CoreOperator.not));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.ampersand),                         CoreOperator.bitwiseAnd));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.pipe),                              CoreOperator.bitwiseOr));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.tilde, OperatorSymb.ampersand),     CoreOperator.bitwiseNot));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.caret),                             CoreOperator.bitwiseXor));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.equals),                            CoreOperator.defineImm));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.colon, OperatorSymb.equals),        CoreOperator.defineMut));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.lt, OperatorSymb.minus),            CoreOperator.assignmentMut));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.plus, OperatorSymb.equals),         CoreOperator.plusMut));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.minus, OperatorSymb.equals),        CoreOperator.minusMut));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.asterisk, OperatorSymb.equals),     CoreOperator.timesMut));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.slash, OperatorSymb.equals),        CoreOperator.divideMut));
        return result;
    }

    static boolean arraysEqual(List<OperatorSymb> a, List<OperatorSymb> b) {
        if (a.isEmpty() || a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); ++i) {
            if (a.get(i) != b.get(i))
                return false;
        }
        return true;
    }

    static boolean isReservedWord(WordToken wt, Map<String, ReservedType> reservedWords) {
        var str = wt.val;
        if (str == "true" || str == "false")
            return true;
        return (reservedWords.containsKey(str));
    }

    static ASTUntypedBase parseAtom(ExprBase inp,
                                Map<String, ReservedType> reservedWords,
                                List<Tuple<List<OperatorSymb>, CoreOperator>> coreOperators) {
        if (inp instanceof IntToken it) {
            return new IntLiteral(it.val);
        } else if (inp instanceof FloatToken ft) {
            return new FloatLiteral(ft.val);
        } else if (inp instanceof StringToken st) {
            return new StringLiteral(st.val);
        } else if (inp instanceof WordToken wt) {
            var str = wt.val;
            if (str == "true") return new BoolLiteral(true);
            if (str == "false") return new BoolLiteral(false);
            val resWord = reservedWords.getOrDefault(str, null);
            return resWord == null ? new Ident(str) : new Reserved(resWord);
        } else if (inp instanceof OperatorToken ot) {
            for (val oper : coreOperators) {
                if (arraysEqual(ot.val, oper.item0)) {
                    return new CoreOperatorAST(oper.item1);
                }
            }
            return new OperatorAST(ot.val);
        } else {
            // should never happen
            return new IntLiteral(-1);
        }
    }
}
