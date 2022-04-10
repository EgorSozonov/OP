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
import tech.sozonov.o7.parser.types.ParsePunctuation;
import tech.sozonov.o7.parser.types.ASTUntyped.*;
import tech.sozonov.o7.parser.types.SyntaxContexts.SyntaxContext;
import tech.sozonov.o7.parser.types.SyntaxContexts.CoreOperator;
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
        var result = new ASTList(SyntaxContext.curlyBraces);
        var reservedWords = getReservedMap();
        var coreOperators = getOperatorList();
        var backtrack = new Stack<Tuple<ListExpr, Integer>>();
        var resultBacktrack = new Stack<ASTList>();

        if (!(inp instanceof ListExpr le)) {
            return new Tuple<ASTUntypedBase, ParseErrorBase>(parseAtom(inp, reservedWords, coreOperators), null);
        }

        int i = 0;
        backtrack.push(new Tuple<ListExpr, Integer>(le, i));
        resultBacktrack.push(result);

        var currInput = le;
        ASTList curr = result;
        boolean ingestionMode = true;

        while (backtrack.peek() != null) {
            val back = backtrack.pop();
            currInput = back.item0;
            i = back.item1;


            while (i < currInput.val.size()) {
                val сurrToken = currInput.val.get(i);

                if (сurrToken instanceof ListExpr le2) {
                    backtrack.push(new Tuple<ListExpr, Integer>(currInput, i + 1));






                    if (le2.pType == ExprLexicalType.curlyBraces || le2.pType == ExprLexicalType.dataInitializer || le2.pType == ExprLexicalType.parens) {
                        // Ask curr if it wants to ingest, if not - then create a nesting
                        if (ingestionMode && isContextIngesting(curr.ctx)) {

                            val newList = new ASTList(le2.pType == ExprLexicalType.curlyBraces ? SyntaxContext.curlyBraces : SyntaxContext.dataInitializer);

                            val mbError = curr.add(newList);
                            if (mbError.isPresent()) return new Tuple<>(result, mbError.get());

                            resultBacktrack.push(curr);
                            curr = newList;
                        } else if (ingestionMode) {
                            ingestionMode = false;
                        } else {
                            ingestionMode = true;
                        }
                    } else {
                        // Determine the type of the statement (assignment | core synt form | func call | list of stuff)
                        // Always nest!
                        if (le2.pType == ExprLexicalType.statement) curr.newStatement();

                        val listType = determineParseContext(le2);

                        val mbCore = getMbCore(le2, reservedWords);
                        if (mbCore.isPresent()) {
                            val coreForm = parseCoreForm(le2, mbCore.get());
                            curr.add(coreForm);

                            // resultBacktrack.push(new Tuple<>(resultCurr, i + 1));
                            // resultCurr = coreForm.;

                        }
                    }

                    currInput = le2;
                    i = 0;
                } else {
                    val atom = parseAtom(сurrToken, reservedWords, coreOperators);
                    val mbError = curr.add(atom);
                    if (mbError.isPresent()) return new Tuple<>(result, mbError.get());
                }

                if (resultBacktrack.peek() != null) {
                    val mbSaturated = curr.listHasEnded();
                    if (mbSaturated.isPresent()) {
                        if (mbSaturated.get()) {
                            curr = resultBacktrack.pop();
                            ingestionMode = false;
                        }
                    } else {
                        // should never happen
                        return new Tuple<>(result, new SyntaxError("Strange error: oversaturated syntax form"));
                    }
                }

            }
        }
        // TODO check if the stack in resultBacktrack is empty
        return new Tuple<ASTUntypedBase, ParseErrorBase>(result, null);
    }


    /**
     * Pre-condition: the input must be either a Statement or a Parens.
     * Determines the syntactic type of the expression: a function call, an assignment/definition, or a core syntactic form.
     */
    static SyntaxContext determineParseContext(ListExpr input) {

    }


    static Optional<SyntaxContext> getMbCore(ListExpr expr, Map<String, SyntaxContext> reserveds) {
        if (!(expr.val.get(0) instanceof WordToken)) return Optional.empty();
        val word = (WordToken)expr.val.get(0);
        return reserveds.containsKey(word.val) ? Optional.of(reserveds.get(word.val)) : Optional.empty();
    }



    static Map<String, SyntaxContext> getReservedMap() {
        val result = new HashMap<String, SyntaxContext>();
        val coreContexts = EnumSet.complementOf(EnumSet.range(SyntaxContext.funcall, SyntaxContext.assignMutableDiv));
        coreContexts.forEach(enumValue -> {
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

    static boolean isReservedWord(WordToken wt, Map<String, SyntaxContext> reservedWords) {
        var str = wt.val;
        if (str == "true" || str == "false")
            return true;
        return (reservedWords.containsKey(str));
    }

    static ASTUntypedBase parseAtom(ExprBase inp,
                                Map<String, SyntaxContext> reservedWords,
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
            return resWord == null ? new Ident(str) : new ASTList(resWord);
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
