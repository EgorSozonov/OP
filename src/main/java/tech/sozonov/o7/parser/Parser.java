package tech.sozonov.o7.parser;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import tech.sozonov.o7.lexer.types.Expr;
import tech.sozonov.o7.lexer.types.ExprLexicalType;
import tech.sozonov.o7.lexer.types.OperatorSymb;
import tech.sozonov.o7.lexer.types.Expr.*;
import tech.sozonov.o7.parser.types.CoreFormType;
import tech.sozonov.o7.parser.types.ParsePunctuation;
import tech.sozonov.o7.parser.types.SubexprType;
import tech.sozonov.o7.parser.types.ASTUntyped.*;
import tech.sozonov.o7.parser.types.ParseContexts.AssignmentType;
import tech.sozonov.o7.parser.types.ParseContexts.CoreOperator;
import tech.sozonov.o7.parser.types.ParseContexts.ParseContext;
import tech.sozonov.o7.parser.types.ParseError.*;
import tech.sozonov.o7.utils.Tuple;
import tech.sozonov.o7.utils.Stack;
import tech.sozonov.o7.utils.Either;
import lombok.val;


public class Parser {
    /**
     * All the structure from the lexer stays, but new structure is added.
     * For example, what was "if x > 5 -> (f)" becomes "if (x > 5) (f)".
     * Full list of new punctuation symbols: -> : $
     */
    public static Tuple<ASTUntypedBase, ParseErrorBase> parse(ExprBase inp) {
        var resultCurr = new ASTList(SubexprType.curlyBraces);
        var result = resultCurr;

        var backtrack = new Stack<Tuple<ListExpr, Integer>>();
        var resultBacktrack = new Stack<ASTList>();

        var reservedWords = getReservedMap();
        var coreOperators = getOperatorList();

        if (!(inp instanceof ListExpr le)) {
            return new Tuple<ASTUntypedBase, ParseErrorBase>(parseAtom(inp, reservedWords, coreOperators), null);
        }

        int i = 0;
        backtrack.push(new Tuple<ListExpr, Integer>(le, i));
        resultBacktrack.push(resultCurr);

        var curr = le;
        while (backtrack.peek() != null) {
            val back = backtrack.pop();
            curr = back.item0;
            i = back.item1;

            resultCurr = resultBacktrack.pop();
            while (i < curr.val.size()) {
                val сurrToken = curr.val.get(i);
                if (сurrToken instanceof ListExpr le2) {
                    backtrack.push(new Tuple<ListExpr, Integer>(curr, i + 1));
                    resultBacktrack.push(resultCurr);
                    val newAST = new ASTList();

                    if (le2.pType == ExprLexicalType.curlyBraces) {
                        // list of stuff
                        val newList = new ASTList(ParseContext.curlyBraces);
                        resultCurr.add(newList);

                        resultCurr = newList;
                    } else if (le2.pType == ExprLexicalType.dataInitializer) {
                        // list of stuff
                        val newElem = new ASTList(ParseContext.dataInitializer);
                        resultCurr.add(newElem);

                        resultCurr = newElem;
                    } else if (le2.pType == ExprLexicalType.parens) {
                        // core synt form | func call | list of stuff
                        val newStatement = new ASTList(ParseContext.parens);
                        resultCurr.add(newStatement);

                        resultCurr = newStatement;
                    } else if (le2.pType == ExprLexicalType.statement) {
                        // assignment | core synt form | func call | list of stuff
                        val mbCore = getMbCore(le2, reservedWords);
                        if (mbCore.isPresent()) {
                            val coreForm = parseCoreForm(le2, mbCore.get());
                            resultCurr.add(coreForm);

                            // resultBacktrack.push(new Tuple<>(resultCurr, i + 1));
                            // resultCurr = coreForm.;

                        } else {


                        }
                    }

                    curr = le2;
                    i = 0;

                } else {
                    // mb a punctuational operator ?
                    // only if not, it is an atom
                    val mbPunctuation = mbParsePunctutation(сurrToken);
                    if (mbPunctuation.isEmpty()) {
                        val atom = parseAtom(сurrToken, reservedWords, coreOperators);
                        // TODO check parsing context
                        resultCurr.add(atom);
                        ++i;
                    } else {

                    }
                }

                if (resultBacktrack.peek() != null) {
                }

            }
        }
        return new Tuple<ASTUntypedBase, ParseErrorBase>(result, null);
    }

    static Optional<ParsePunctuation> mbParsePunctutation(ExprBase token) {
        if (token instanceof OperatorToken op) {
            if (op.val.size() == 2 && op.val.get(0) == OperatorSymb.minus && op.val.get(1) == OperatorSymb.gt) {
                return Optional.of(ParsePunctuation.arrow);
            } else if (op.val.size() == 1 && op.val.get(0) == OperatorSymb.colon) {
                return Optional.of(ParsePunctuation.colon);
            } else if (op.val.size() == 1 && op.val.get(0) == OperatorSymb.dollar) {
                return Optional.of(ParsePunctuation.dollar);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    static ASTUntypedBase parseAssignment(ListExpr expr, AssignmentType assignType) {
        return new Ident("TODO");
    }

    static ASTUntypedBase parseCoreForm(ListExpr expr, CoreFormType coreType) {
        return new Ident("TODO");
    }

    static Optional<CoreFormType> getMbCore(ListExpr expr, Map<String, CoreFormType> reserveds) {
        if (!(expr.val.get(0) instanceof WordToken)) return Optional.empty();
        val word = (WordToken)expr.val.get(0);
        return reserveds.containsKey(word.val) ? Optional.of(reserveds.get(word.val)) : Optional.empty();
    }


    static Optional<AssignmentType> getMbAssignment(ListExpr expr) {
        if (expr.val.size() >= 3 && expr.val.get(1) instanceof OperatorToken ot) {
            return getOperatorAssignmentType(ot);
        } else {
            return Optional.empty();
        }
    }


    static Optional<AssignmentType> getOperatorAssignmentType(OperatorToken ot) {
        if (ot.val.size() == 1 && ot.val.get(0) == OperatorSymb.equals) return Optional.of(AssignmentType.immutableDef);
        if (ot.val.size() == 2 && ot.val.get(1) == OperatorSymb.equals) {
            var f = ot.val.get(0);
            if (f == OperatorSymb.colon) return Optional.of(AssignmentType.mutableAssignment);
            if (f == OperatorSymb.plus) return Optional.of(AssignmentType.mutablePlus);
            if (f == OperatorSymb.minus) return Optional.of(AssignmentType.mutablePlus);
            if (f == OperatorSymb.asterisk) return Optional.of(AssignmentType.mutablePlus);
            if (f == OperatorSymb.slash) return Optional.of(AssignmentType.mutablePlus);
        }
        return Optional.empty();
    }

    static Map<String, CoreFormType> getReservedMap() {
        val result = new HashMap<String, CoreFormType>();
        EnumSet.allOf(CoreFormType.class).forEach(enumValue -> {
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

    static boolean isReservedWord(WordToken wt, Map<String, CoreFormType> reservedWords) {
        var str = wt.val;
        if (str == "true" || str == "false")
            return true;
        return (reservedWords.containsKey(str));
    }

    static ASTUntypedBase parseAtom(ExprBase inp,
                                Map<String, CoreFormType> reservedWords,
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
