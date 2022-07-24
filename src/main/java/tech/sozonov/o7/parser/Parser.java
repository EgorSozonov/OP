package tech.sozonov.o7.parser;
import java.util.Map;
import java.util.Optional;
import tech.sozonov.o7.lexer.types.LexicalContext;
import tech.sozonov.o7.lexer.types.OperatorSymb;
import tech.sozonov.o7.lexer.types.Expr.*;
import tech.sozonov.o7.parser.types.Syntax;
import tech.sozonov.o7.parser.types.AST.*;
import static tech.sozonov.o7.parser.types.SyntaxContexts.SyntaxContext.*;
import tech.sozonov.o7.parser.types.SyntaxContexts.SyntaxContext;
import tech.sozonov.o7.parser.types.SyntaxError;
import tech.sozonov.o7.utils.Tuple;
import tech.sozonov.o7.utils.Either;
import tech.sozonov.o7.utils.Stack;
import static tech.sozonov.o7.utils.ArrayUtils.*;
import static tech.sozonov.o7.utils.ListUtils.*;
import lombok.val;


public class Parser {

/**
 * All the structure from the lexer stays, but new structure is added.
 * For example, what was "if x > 5 -> (f)" becomes "if (x > 5) (f)".
 * Full list of new punctuation symbols: -> : $
 */
public static Tuple<ASTBase, SyntaxError> parse(final ExprBase inp) {
    var result = new ASTList(curlyBraces);
    val syntax = new Syntax();
    var backtrack = new Stack<Tuple<ListExpr, Integer>>();
    var resultBacktrack = new Stack<ASTList>();

    if (!(inp instanceof ListExpr le)) {
        return new Tuple<ASTBase, SyntaxError>(parseAtom(inp, syntax), null);
    }

    int i = 0;
    backtrack.push(new Tuple<ListExpr, Integer>(le, i));
    resultBacktrack.push(result);

    var currInput = le;
    ASTList curr = result;

    while (backtrack.peek() != null) {
        val back = backtrack.pop();
        currInput = back.i0;
        i = back.i1;

        while (i < currInput.val.size()) {
            val сurrToken = currInput.val.get(i);
            if (сurrToken instanceof ListExpr le2) {
                backtrack.push(new Tuple<ListExpr, Integer>(currInput, i + 1));
                boolean skipFirstToken = false;

                // curlyBrace -> curlyBrace or nothing (if directly inside a bounded core form)
                // stmt -> funcall | assignment | core form
                // paren -> funcall | core form
                // dataInit -> dataInit

                if (le2.lType == LexicalContext.curlyBraces) {
                    if (curr.isUnbounded()) {
                        if (curr.isEmpty()) return new Tuple<>(result, new SyntaxError("Curly braces are not allowed in an unbounded core syntax form like " + curr.ctx));
                        curr = cleanPop(resultBacktrack, curr);
                    } else {
                        if (curr.isBounded()) curr.newStatement();
                        val newList = new ASTList(curlyBraces);
                        curr.add(newList);
                        resultBacktrack.push(curr);
                        curr = newList;
                    }
                } else if (le2.lType == LexicalContext.statement) {
                    if (curr.ctx != curlyBraces && !curr.isCoreForm()) {
                        return new Tuple<>(result, new SyntaxError("Statements are only allowed in curly braces or core syntax forms, not inside " + curr.ctx));
                    }
                    if (curr.isUnbounded()) {
                        if (curr.statementFitsCoreForm(le2)) {
                            curr.newStatement();
                        } else {
                            curr = cleanPop(resultBacktrack, curr);
                        }
                    }
                    if (curr.isBounded()) curr.newStatement();
                    if (!curr.isClauseBased()) {

                        val listType = determineListType(le2, syntax.contexts);
                        if (listType.isLeft()) return new Tuple<>(result, listType.getLeft());
                        skipFirstToken = listType.get().i1;

                        val newList = new ASTList(listType.get().i0);
                        val mbError = curr.add(newList);
                        if (mbError.isPresent()) return new Tuple<>(result, mbError.get());

                        resultBacktrack.push(curr);
                        curr = newList;
                    }
                } else if (le2.lType == LexicalContext.parens) {
                    if (curr.isUnbounded()) {
                        if (curr.isEmpty()) new Tuple<>(result, new SyntaxError("Parentheses are not allowed in an unbounded core syntax form like " + curr.ctx));
                    }
                    if (curr.isBounded()) curr.newStatement();
                    if (curr.ctx == curlyBraces) return new Tuple<>(result, new SyntaxError("Parentheses are not allowed directly in curly braces"));

                    val listType = determineListType(le2, syntax.contexts);
                    if (listType.isLeft()) return new Tuple<>(result, listType.getLeft());

                    skipFirstToken = listType.get().i1;

                    val newList = new ASTList(listType.get().i0);
                    val mbError = curr.add(newList);
                    if (mbError.isPresent()) return new Tuple<>(result, mbError.get());

                    resultBacktrack.push(curr);
                    curr = newList;
                } else {
                    if (curr.isBounded()) {
                        return new Tuple<>(result, new SyntaxError("Data initializers are not allowed in a bounded core syntax form like " + curr.ctx));
                    }

                    val dataInit = new ASTList(dataInitializer);
                    val mbError = curr.add(dataInit);
                    if (mbError.isPresent()) return new Tuple<>(result, mbError.get());

                    resultBacktrack.push(curr);
                    curr = dataInit;
                }

                currInput = le2;
                i = skipFirstToken ? 1 : 0;
            } else {
                val atom = parseAtom(сurrToken, syntax);
                val mbError = curr.addAtom(atom);
                if (mbError.isPresent()) return new Tuple<>(result, mbError.get());
                ++i;
            }
        }

        if (i == currInput.val.size()) {
            val mbSaturated = curr.gotSaturated();
            if (mbSaturated.isPresent()) {
                if (mbSaturated.get()) {
                    curr = cleanPop(resultBacktrack, curr);
                }
            } else {
                // should never happen
                return new Tuple<>(result, new SyntaxError("Strange error: oversaturated syntax form"));
            }
        }
    }
    if (resultBacktrack.peek() != null) {
        return new Tuple<>(result, new SyntaxError("Strange error: non-empty result stack at the end"));
    }
    return new Tuple<ASTBase, SyntaxError>(result, null);
}


static ASTList cleanPop(final Stack<ASTList> backtrack, final ASTList curr) {
    if (last(curr.data).isEmpty()) {
        removeLast(curr.data);
    }
    return backtrack.pop();
}

/**
 * Pre-condition: the input must be either a Statement or a Parens.
 * Determines the syntactic type of the expression: a function call, an assignment/definition, a core syntactic form, or a type declaration.
 * Returns: either a parse error,
 * or a tuple of SyntaxContext and a boolean of whether to skip the first token (necessary for the case of a core syntax form).
 */
static Tuple<SyntaxContext, Boolean> determineListTypeNoErrCheck(ListExpr input, Map<String, SyntaxContext> syntaxContexts) {
    val mbCore = checkListIsCore(input, syntaxContexts);
    if (mbCore.isPresent())  return new Tuple<>(mbCore.get(), true);

    if (input.val.size() >= 3 && input.val.get(1) instanceof OperatorToken ot) {
        val firstOper = ot.val.get(0);
        if (ot.val.size() == 1 && firstOper == OperatorSymb.equals) {
            return new Tuple<>(assignImmutable, false);
        } else if (ot.val.size() == 2 && ot.val.get(1) == OperatorSymb.equals) {
            if (firstOper == OperatorSymb.plus) return new Tuple<>(assignMutablePlus, false);
            if (firstOper == OperatorSymb.minus) return new Tuple<>(assignMutableMinus, false);
            if (firstOper == OperatorSymb.asterisk) return new Tuple<>(assignMutableTimes, false);
            if (firstOper == OperatorSymb.slash) return new Tuple<>(assignMutableDiv, false);
        }
        if (ot.val.size() == 2 && firstOper == OperatorSymb.colon && ot.val.get(1) == OperatorSymb.colon) return new Tuple<>(typeDeclaration, false);
    }
    if (input.val.size() == 1 && input.val.get(0) instanceof ListExpr le && le.lType == LexicalContext.curlyBraces) {
        return new Tuple<>(curlyBraces, false);
    }
    return new Tuple<>(funcall, false);
}

/**
 * Pre-condition: the input must be either a Statement or a Parens.
 * Determines the syntactic type of the expression: a function call, an assignment/definition, a core syntactic form, or a type declaration.
 * Returns: either a parse error, or a tuple of SyntaxContext and a boolean of whether to skip the first token (useful for core syntax forms).
 */
static Either<SyntaxError, Tuple<SyntaxContext, Boolean>> determineListType(ListExpr input, Map<String, SyntaxContext> syntaxContexts) {
    val tuple = determineListTypeNoErrCheck(input, syntaxContexts);
    if (input.lType == LexicalContext.parens && ASTBase.isAssignment(tuple.i0)) {
        return Either.left(new SyntaxError("Assignments are not allowed in parentheses, only on the statement level (i.e. inside curly braces)"));
    } else {
        return Either.right(tuple);
    }
}


/**
 * Determines core syntactical forms, but only judging on the first token (or first 2 tokens for unbounded-able forms).
 */
static Optional<SyntaxContext> checkListIsCore(ListExpr expr, Map<String, SyntaxContext> reservedWords) {
    if (!(expr.val.get(0) instanceof WordToken)) return Optional.empty();
    val word = (WordToken)expr.val.get(0);
    if (!reservedWords.containsKey(word.val)) return Optional.empty();

    val coreSyntaxContext = reservedWords.get(word.val);
    return Optional.of(coreSyntaxContext);
}


static ASTBase parseAtom(ExprBase inp, Syntax syntax) {
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

        val resWord = syntax.reservedWords.getOrDefault(str, null);
        if (resWord != null) return new ReservedLiteral(resWord);

        val coreForm = syntax.contexts.getOrDefault(str, null);
        if (coreForm != null) return new ASTList(coreForm);

        return new Ident(str);
    } else if (inp instanceof OperatorToken ot) {
        for (val oper : syntax.coreOperators) {
            if (arraysEqual(ot.val, oper.i0)) {
                return new CoreOperatorAST(oper.i1);
            }
        }
        for (val oper : syntax.functionOperators) {
            if (arraysEqual(ot.val, oper.i0)) {
                return new FunctionOperatorAST(oper.i1);
            }
        }
        return new OperatorAST(ot.val);
    } else if (inp instanceof CommentToken ct) {
        return new CommentAST(ct.val);
    } else {
        // should never happen
        return new IntLiteral(-1);
    }
}

}
