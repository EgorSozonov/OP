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

        while (backtrack.peek() != null) {
            val back = backtrack.pop();
            currInput = back.item0;
            i = back.item1;


            while (i < currInput.val.size()) {
                val сurrToken = currInput.val.get(i);

                if (сurrToken instanceof ListExpr le2) {
                    backtrack.push(new Tuple<ListExpr, Integer>(currInput, i + 1));






                // curly - maybe nest
                // stmt - nest, determine
                // () - maybe nest, determine
                // [] - nest



                    if (le2.pType == ExprLexicalType.curlyBraces) {
                        // Ask curr if it wants to ingest, if not - then create a nesting
                        if (curr.isContextIngesting()) {
                            curr.ingestItem();
                        } else {
                            val newList = new ASTList(le2.pType == ExprLexicalType.curlyBraces ? SyntaxContext.curlyBraces : SyntaxContext.parens);

                            val mbError = curr.add(newList);
                            if (mbError.isPresent()) return new Tuple<>(result, mbError.get());

                            resultBacktrack.push(curr);
                            curr = newList;
                        }
                    } else if (le2.pType == ExprLexicalType.statement || le2.pType == ExprLexicalType.parens){

                        // if {}

                        // if x > 5 -> print x

                        // Determine the type of the statement (assignment | core synt form | func call | list of stuff)
                        // Always nest!

                        val listType = determineListType(le2);
                        if (listType.isLeft()) {
                            return new Tuple<>(result, listType.getLeft());
                        }

                        if (le2.pType == ExprLexicalType.parens && curr.isContextIngesting()) {
                            curr.ingestItem();
                        } else {
                            val newList = new ASTList(listType);

                            val mbError = curr.add(newList);
                            if (mbError.isPresent()) return new Tuple<>(result, mbError.get());

                            resultBacktrack.push(curr);
                            curr = newList;
                        }

                        val newStatement = new ASTList(listType);
                        curr.newStatement(listType, newStatement);
                        resultBacktrack.push(curr);
                        curr = newStatement;
                    } else if (le2.pType == ExprLexicalType.parens) {
                        val listType = determineListType(le2);
                        val newStatement = new ASTList(listType);
                        curr.newStatement(listType, newStatement);
                        resultBacktrack.push(curr);
                        curr = newStatement;
                    } else {
                        val dataInitializer = new ASTList(SyntaxContext.dataInitializer);
                        val mbError = curr.add(dataInitializer);
                        if (mbError.isPresent()) return new Tuple<>(result, mbError.get());
                        resultBacktrack.push(curr);
                        curr = dataInitializer;
                    }

                    currInput = le2;
                    i = 0;
                } else {
                    val atom = parseAtom(сurrToken, reservedWords, coreOperators);
                    val mbError = curr.add(atom);
                    if (mbError.isPresent()) return new Tuple<>(result, mbError.get());
                }

                val mbSaturated = curr.listHasEnded();
                if (mbSaturated.isPresent()) {
                    if (mbSaturated.get()) {
                        curr = resultBacktrack.pop();
                    }
                } else {
                    // should never happen
                    return new Tuple<>(result, new SyntaxError("Strange error: oversaturated syntax form"));
                }


            }
        }
        // TODO check if the stack in resultBacktrack is empty
        return new Tuple<ASTUntypedBase, ParseErrorBase>(result, null);
    }


    /**
     * Pre-condition: the input must be either a Statement or a Parens.
     * Determines the syntactic type of the expression: a function call, an assignment/definition, a core syntactic form, or a type declaration.
     * Returns: either a parse error, or a tuple of SyntaxContext and a boolean of whether to skip the first token (useful for core syntax forms).
     */
    static Either<ParseErrorBase, Tuple<SyntaxContext, Boolean>> determineListType(ListExpr input, Map<String, SyntaxContext> reservedWords) {
        val mbCore = getMbCore(input, reservedWords);
        if (mbCore.isPresent())  return Either.right(new Tuple<>(mbCore.get(), true));
        if (input.val.size() >= 3 && input.val.get(1) instanceof OperatorToken ot) {
            val firstOper = ot.val.get(0);
            if (ot.val.size() == 1 && firstOper == OperatorSymb.equals) {
                return new Tuple<>(SyntaxContext.assignImmutable, false);
            } else if (ot.val.size() == 2 && ot.val.get(1) == OperatorSymb.equals) {
                if (firstOper == OperatorSymb.plus) return new Tuple<>(SyntaxContext.assignMutablePlus, false);
                if (firstOper == OperatorSymb.minus) return new Tuple<>(SyntaxContext.assignMutableMinus, false);
                if (firstOper == OperatorSymb.times) return new Tuple<>(SyntaxContext.assignMutableTimes, false);
                if (firstOper == OperatorSymb.divideBy) return new Tuple<>(SyntaxContext.assignMutableDiv, false);
            }
            if (ot.val.size() == 2 && firstOper == OperatorSymb.colon && ot.val.get(1) == OperatorSymb.colon) return new Tuple<>(SyntaxContext.typeDeclaration, false);
        }
        return new Tuple<>(SyntaxContext.funcall, false);
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
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.colon, OperatorSymb.equals),        CoreOperator.assignmentMut));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.plus, OperatorSymb.equals),         CoreOperator.plusMut));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.minus, OperatorSymb.equals),        CoreOperator.minusMut));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.asterisk, OperatorSymb.equals),     CoreOperator.timesMut));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.slash, OperatorSymb.equals),        CoreOperator.divideMut));

        result.add(new Tuple<>(Arrays.asList(OperatorSymb.minus, OperatorSymb.gt),            CoreOperator.arrow));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.equals, OperatorSymb.gt),           CoreOperator.fatArrow));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.colon, OperatorSymb.colon),         CoreOperator.typeDecl));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.colon),                             CoreOperator.colon));
        result.add(new Tuple<>(Arrays.asList(OperatorSymb.pipe),                             CoreOperator.pipe));

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
