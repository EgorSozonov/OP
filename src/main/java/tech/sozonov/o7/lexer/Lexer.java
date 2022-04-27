package tech.sozonov.o7.lexer;
import tech.sozonov.o7.lexer.types.LexError;
import tech.sozonov.o7.lexer.types.Expr.*;
import tech.sozonov.o7.lexer.types.LexicalContext;
import tech.sozonov.o7.lexer.types.OperatorSymb;
import tech.sozonov.o7.utils.ASCII;
import tech.sozonov.o7.utils.ByteList;
import tech.sozonov.o7.utils.Tuple;
import tech.sozonov.o7.utils.Stack;
import tech.sozonov.o7.utils.Either;
import tech.sozonov.o7.utils.MutableBoolean;
import static tech.sozonov.o7.utils.ListUtils.*;
import static tech.sozonov.o7.utils.ArrayUtils.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import lombok.val;


public class Lexer {
public static Tuple<ExprBase, LexError> lexicallyAnalyze(final byte[] inp) {
    LexError err = null;
    if (inp.length < 1) return new Tuple<ExprBase, LexError>(new ListExpr(LexicalContext.curlyBraces), err);

    int i = 0;
    ListExpr curr = new ListExpr(LexicalContext.curlyBraces);
    val result = curr;
    val backtrack = new Stack<ListExpr>();
    backtrack.push(curr);
    val firstList = new ListExpr(LexicalContext.statement);
    curr.val.add(firstList);
    curr = firstList;

    int walkLen = inp.length - 1;
    while (i <= walkLen) {
        byte cChar = inp[i];
        if (cChar > 127) return new Tuple<ExprBase, LexError>(result, new LexError("Non-ASCII symbol in code " + cChar));
        Either<LexError, Tuple<ExprBase, Integer>> mbToken;

        if (cChar == ASCII.space || cChar == ASCII.emptyCR) {
            ++i;
        } else if (cChar == ASCII.emptyLF) {
            if (backtrack.peek() != null
              && backtrack.peek().lType == LexicalContext.curlyBraces
              && curr.val.size() > 0) {
                // we are in a CurlyBraces context, so a newline means a new statement
                val back = backtrack.peek();
                val newStatement = new ListExpr(LexicalContext.statement);
                back.val.add(newStatement);
                curr = newStatement;
            }
            ++i;

        } else if (cChar == ASCII.colonSemi) {
            if (curr.lType == LexicalContext.parens) {
                return new Tuple<ExprBase, LexError>(result, new LexError(
                    "Semi-colons are not allowed in inline expressions (i.e. directly inside parentheses)"));
            }
            val prevList = backtrack.peek();

            if (prevList != null) {
                curr = new ListExpr(LexicalContext.statement);
                prevList.val.add(curr);
            } else {
                return new Tuple<ExprBase, LexError>(result, new LexError("Lexer error: empty stack"));
            }

            ++i;
        } else if (cChar == ASCII.curlyOpen) {
            if (curr.lType == LexicalContext.statement && curr.val.isEmpty()) {
                curr.lType = LexicalContext.curlyBraces;
                val newCurr = new ListExpr(LexicalContext.statement);
                curr.val.add(newCurr);
                backtrack.push(curr);
                curr = newCurr;

            } else {
                val newList = new ListExpr(LexicalContext.curlyBraces);
                val newCurr = new ListExpr(LexicalContext.statement);
                newList.val.add(newCurr);
                curr.val.add(newList);
                backtrack.push(curr);
                backtrack.push(newList);
                curr = newCurr;
            }

            ++i;
        } else if (cChar == ASCII.curlyClose) {
            if (backtrack.peek() == null || backtrack.peek().lType != LexicalContext.curlyBraces) {
                return new Tuple<ExprBase, LexError>(result, new LexError("Extra '}'"));
            }
            backtrack.pop();
            if (backtrack.peek() == null) {
                return new Tuple<ExprBase, LexError>(result, new LexError("Missing parent for '{}' context"));
            }

            val back = backtrack.pop();
            val theLast = last(back.val);
            if (theLast instanceof ListExpr le && !le.val.isEmpty() && last(le.val) instanceof ListExpr le2 && le2.val.isEmpty()) {
                removeLast(le.val);
            }

            curr = back;
            ++i;
        } else if (cChar == ASCII.parenthesisOpen) {
            val newList = new ListExpr(LexicalContext.parens);
            curr.val.add(newList);
            backtrack.push(curr);
            curr = newList;
            ++i;
        } else if (cChar == ASCII.parenthesisClose) {
            if (backtrack.peek() == null || curr.lType != LexicalContext.parens) {
                return new Tuple<ExprBase, LexError>(result, new LexError("Extra ')'"));
            }
            val back = backtrack.pop();

            val theLast = last(back.val);
            if (theLast instanceof ListExpr le && le.val.isEmpty()) {
                removeLast(back.val);
            }

            curr = back;
            ++i;
        }  else if (cChar == ASCII.bracketOpen) {
            val newList = new ListExpr(LexicalContext.dataInitializer);
            curr.val.add(newList);
            backtrack.push(curr);
            curr = newList;
            ++i;
        } else if (cChar == ASCII.bracketClose) {

            if (backtrack.peek() == null || curr.lType != LexicalContext.dataInitializer) {
                return new Tuple<ExprBase, LexError>(result, new LexError("Extra ']'"));
            }
            val back = backtrack.pop();

            val theLast = last(back.val);
            if (theLast instanceof ListExpr le && le.val.isEmpty()) {
                removeLast(back.val);
            }

            curr = back;
            ++i;
        } else {
            if ((cChar >= ASCII.digit0 && cChar <= ASCII.digit9)
                || (i < walkLen && cChar == ASCII.underscore
                    && inp[i + 1] >= ASCII.digit0 && inp[i + 1] <= ASCII.digit9)) {
                mbToken = lexNumber(inp, i, walkLen);
            } else if ((cChar >= ASCII.aLower && cChar <= ASCII.zLower)
                    || (cChar >= ASCII.aUpper && cChar <= ASCII.zUpper)
                    || (cChar == ASCII.underscore
                        && i < walkLen
                        && ((inp[i + 1] >= ASCII.aLower && inp[i + 1] <= ASCII.zLower)
                            || (inp[i + 1] >= ASCII.aUpper && inp[i + 1] <= ASCII.zUpper)))) {
                mbToken = lexWord(inp, i, walkLen);
            } else if (i < walkLen && cChar == ASCII.dot
                    && ((inp[i + 1] >= ASCII.aLower && inp[i + 1] <= ASCII.zLower)
                        || (inp[i + 1] >= ASCII.aUpper && inp[i + 1] <= ASCII.zUpper)
                        || inp[i + 1] == ASCII.underscore)) {
                mbToken = lexDotWord(inp, i, walkLen);
            } else if (cChar == ASCII.hashtag) {
                mbToken = lexComment(inp, i, walkLen);
            } else if (cChar == ASCII.quotationMarkDouble) {
                mbToken = lexStringLiteral(inp, i, walkLen);
            } else if (isOperatorSymb(cChar) != OperatorSymb.notASymb) {
                mbToken = lexOperator(inp, i, walkLen);
            } else {
                mbToken = Either.left(new LexError("Unexpected symbol " + inp[i]));
            }
            if (mbToken.isLeft()) {
                return new Tuple<ExprBase, LexError>(result, mbToken.getLeft());
            } else {
                val rt = mbToken.get();
                curr.val.add(rt.i0);
                i = rt.i1;
            }
        }
    }
    if (backtrack.peek() != null) {
        val back = backtrack.pop();
        val theLast = last(back.val);
        if (theLast instanceof ListExpr le) {
            if (le.val.isEmpty()) {
                removeLast(back.val);
            }
        }
    }
    return new Tuple<ExprBase, LexError>(result, err);
}

/**
 * The ASCII notation for the lowest 64-bit integer, -9_223_372_036_854_775_808
 */
static byte[] minInt = new byte[] {
        57,
        50, 50, 51,
        51, 55, 50,
        48, 51, 54,
        56, 53, 52,
        55, 55, 53,
        56, 48, 56,
    };

/**
 * The ASCII notation for the highest 64-bit integer, 9_223_372_036_854_775_807
 */
static byte[] maxInt = new byte[] {
        57,
        50, 50, 51,
        51, 55, 50,
        48, 51, 54,
        56, 53, 52,
        55, 55, 53,
        56, 48, 55,
    };

static Either<LexError, Tuple<ExprBase, Integer>> lexNumber(byte[] inp, int start, int walkLen) {
    if (start > walkLen) return Either.left(new LexError("Unexpected end of input"));

    int ind = start;
    byte currByte = inp[start];

    boolean isNegative = currByte == ASCII.underscore;
    if (isNegative) {
        ++ind;
        currByte = inp[ind];
        if (currByte < ASCII.digit0 || currByte > ASCII.digit9) {
            return Either.left(new LexError("Unexpected symbol error: Expected a digit but got char " + currByte));
        }
    }
    boolean isFloating = false;

    while (ind <= walkLen &&
        ((currByte >= ASCII.digit0 && currByte <= ASCII.digit9)
        || currByte == ASCII.underscore)) {
        ++ind;
        if (ind <= walkLen) currByte = inp[ind];
    }

    // In case the next symbol instanceof a dot, this will be a floating-point number
    if (ind < walkLen && inp[ind] == ASCII.dot) {
        val nextBt = inp[ind + 1];
        if (nextBt >= ASCII.digit0 && nextBt <= ASCII.digit9) {
            isFloating = true;
            // Skipping the dot
            ++ind;
            currByte = nextBt;
            while (ind <= walkLen &&
                    ((currByte >= ASCII.digit0 && currByte <= ASCII.digit9)
                    || currByte == ASCII.underscore)) {
                ++ind;
                if (ind <= walkLen) currByte = inp[ind];
            }
        }
    }
    int startingDigit = isNegative ? start + 1 : start;
    val mbNumber = isFloating ? lexFloat(inp, startingDigit, ind - 1, isNegative)
                              : lexInt(inp, startingDigit, ind - 1, isNegative);
    final int finalInd = ind;
    return mbNumber.bimap(x -> x, x -> new Tuple<ExprBase, Integer>(x, finalInd));
}


static Either<LexError, ExprBase> lexInt(byte[] inp, int start, int endInclusive, boolean isNegative) {
    val digitList = filterBytes(inp, start, endInclusive, x -> x != ASCII.underscore);

    if (checkIntRange(digitList, isNegative)) {
        long actualInt = intOfDigits(digitList);
        return Either.right(new IntToken(isNegative ? (-1)*actualInt : actualInt));
    } else {
        return
            Either.left(new LexError("Int lexical error: number outside range [-9,223,372,036,854,775,808; 9,223,372,036,854,775,807]"));
    }
}


static Either<LexError, ExprBase> lexFloat(byte[] inp, int start, int endInclusive, boolean isNegative) {
    ByteList digitList = filterBytes(inp, start, endInclusive, (x) -> x != ASCII.underscore);
    val str = (isNegative ? "-" : "") + (digitList.toAsciiString());
    double result = tryParseDouble(str);
    return Double.isNaN(result)
        ? Either.left(new LexError("Float lexical error: '" + str + "' not parsing as a floating-point number"))
        : Either.right(new FloatToken(result));
}


static boolean checkIntRange(ByteList digits, boolean isNegative) {
    if (digits.length != 19) return digits.length < 19;
    return isNegative ? _isLexicographicallyLE(digits, minInt) : _isLexicographicallyLE(digits, maxInt);
}


static boolean _isLexicographicallyLE(ByteList a, byte[] b) {
    for (int i = 0; i < a.length; ++i) {
        if (a.get(i) < b[i]) return true;
        if (a.get(i) > b[i]) return false;
    }
    return true;
}


static long intOfDigits(ByteList digits) {
    long result = 0;
    long powerOfTen = 1;
    for (int ind = digits.length - 1; ind > -1; --ind) {
        long digitValue = (digits.get(ind) - ASCII.digit0)*powerOfTen;
        result += digitValue;
        powerOfTen *= 10;
    }
    return result;
}

/**
 * Lexically analyze a single word, i.e. a possibly dot-separated number of possibly underscore-initiated sections.
 * Examples: "foo", "foo.x", "Module.Submodule.foo", "_x", "_x1._y2"
 */
static Either<LexError, Tuple<ExprBase, Integer>> lexWord(byte[] inp, int start, int walkLen) {
    int startCapitalized = -1;
    int endCapitalized = -2;
    int startUncapitalized = -1;
    int endUncapitalized = -2;
    val prevCapitalized = new MutableBoolean(true);
    val nextCapitalized = new MutableBoolean(true);
    int prev = start;
    int next = -1;

    do {
        next = lexWordSection(inp, prev, walkLen, nextCapitalized);

        if (!MutableBoolean.eq(prevCapitalized, nextCapitalized)) {
            if (nextCapitalized.v == true) {
                return Either.left(new LexError("A word may not contain capitalized sections after uncapitalized ones"));
            }
            prevCapitalized.v = nextCapitalized.v;
            if (prev > start) {
                startCapitalized = start;
                endCapitalized = prev - 2;
            }
            startUncapitalized = prev;
        }
        if (next <= walkLen && inp[next] == ASCII.underscore) {
            return Either.left(new LexError("Snake case is forbidden in all identifiers. Underscores are only allowed in initial position"));
        }
        if ((next <= walkLen && inp[next] != ASCII.dot) || next > walkLen) break;
        if (next == walkLen && inp[next] == ASCII.dot) {
            return Either.left(new LexError("Premature end of input after dot in word"));
        }

        byte afterDot = inp[next + 1];
        if (afterDot == ASCII.underscore) {
            if (next == walkLen - 1) return Either.left(new LexError("Premature end of input after underscore in word"));
            afterDot = inp[next + 2];
        }
        if (afterDot < ASCII.aUpper || afterDot > ASCII.zLower && (afterDot > ASCII.zUpper && afterDot < ASCII.aLower)) {
            return Either.left(new LexError("Unexpected character in word, expected a lowercase or uppercase letter, instead got value " + afterDot));
        }
        prev = next + 1;
    } while (next > start);
    if (startUncapitalized > -1) {
        endUncapitalized = next - 1;
    } else {
        // we've never encountered an uncapitalized section - i.e. they've all been capitalized
        startCapitalized = start;
        endCapitalized = next - 1;
    }
    if (endCapitalized >= startCapitalized && endUncapitalized >= startUncapitalized) {
        val wordToken = new WordToken(stringOfASCII(inp, startCapitalized, endCapitalized), stringOfASCII(inp, startUncapitalized, endUncapitalized));
        return Either.right(new Tuple<>(wordToken, next));
    } else if (endCapitalized >= startCapitalized) {
        val wordToken = new WordToken(stringOfASCII(inp, startCapitalized, endCapitalized));
        return Either.right(new Tuple<>(wordToken, next));
    } else if (endUncapitalized >= startUncapitalized) {
        val wordToken = new WordToken(stringOfASCII(inp, startUncapitalized, endUncapitalized));
        return Either.right(new Tuple<>(wordToken, next));
    } else {
        return Either.left(new LexError("Word did not contain any letters"));
    }
}

static String stringOfASCII(byte[] inp, int start, int end) {
    val subList = new byte[end - start + 1];
    for (int j = start; j <= end; ++j) {
        subList[j - start] = inp[j];
    }
    return new String(subList, StandardCharsets.US_ASCII);
}

/**
 * Lexically analyze a single word section, i.e. the part of the word between the dots
 * Returns next position and also sets a boolean flag if this section started with a capital letter.
 * Does NOT check that the word starts with a letter instead of a digit, because that's validated in the caller.
 */
static int lexWordSection(byte[] inp, int start, int walkLen, MutableBoolean wasCapitalized) {
    int i = start;
    byte currByte = inp[i];
    if (currByte == ASCII.underscore) {
        ++i;
        if (i <= walkLen) currByte = inp[i];
    }
    wasCapitalized.v = currByte >= ASCII.aUpper && (currByte <= ASCII.zUpper);

    while (i <= walkLen &&
        (  (currByte >= ASCII.aLower && currByte <= ASCII.zLower)
        || (currByte >= ASCII.aUpper && currByte <= ASCII.zUpper)
        || (currByte >= ASCII.digit0 && currByte <= ASCII.digit9))) {
        ++i;
        if (i <= walkLen) currByte = inp[i];
    }
    return i;
}


/**
 * Lexically analyze a dot-word, i.e. a word preceded by a dot (used for function names in function calls).
 * Examples: ".foo", ".foo.x._y", ".Module.Submodule.foo.x.y"
 */
static Either<LexError, Tuple<ExprBase, Integer>> lexDotWord(byte[] inp, int start, int walkLen) {
    Either<LexError, Tuple<ExprBase, Integer>> wrd = lexWord(inp, start + 1, walkLen);
    if (wrd.isRight()) {
        return Either.right(new Tuple<>(new DotWordToken((WordToken)wrd.get().i0), wrd.get().i1));
    } else {
        return wrd;
    }
}

/**
 * Operators
 * Lexes a sequence of 1 to 3 of any of the following symbols:
 * & + - / * ! ~ $ % ^ | > < ? =
 */
static Either<LexError, Tuple<ExprBase, Integer>> lexOperator(byte[] inp, int start, int walkLen) {
    int i = start;
    val result = new ArrayList<OperatorSymb>();
    byte currByte = inp[i];
    while (i <= walkLen && i < start + 3) {
        val smb = isOperatorSymb(currByte);
        if (smb == OperatorSymb.notASymb) break;

        result.add(smb);
        ++i;
        if (i <= walkLen) currByte = inp[i];
    }
    if (i <= walkLen && isOperatorSymb(inp[i]) != OperatorSymb.notASymb) {
        return Either.left(new LexError("Operators longer than 3 symbols are not allowed"));
    }
    return Either.right(new Tuple<ExprBase, Integer>(new OperatorToken(result), i));
}


static OperatorSymb isOperatorSymb(byte symb) {
    switch (symb) {
        case ASCII.ampersand: return OperatorSymb.ampersand;
        case ASCII.plus: return OperatorSymb.plus;
        case ASCII.minus: return OperatorSymb.minus;
        case ASCII.slashForward: return OperatorSymb.slash;
        case ASCII.asterisk: return OperatorSymb.asterisk;
        case ASCII.exclamationMark: return OperatorSymb.exclamation;
        case ASCII.tilde: return OperatorSymb.tilde;
        case ASCII.dollar: return OperatorSymb.dollar;
        case ASCII.percent: return OperatorSymb.percent;
        case ASCII.caret: return OperatorSymb.caret;
        case ASCII.verticalBar: return OperatorSymb.pipe;
        case ASCII.greaterThan: return OperatorSymb.gt;
        case ASCII.lessThan: return OperatorSymb.lt;
        case ASCII.questionMark: return OperatorSymb.question;
        case ASCII.equalTo: return OperatorSymb.equals;
        case ASCII.colon: return OperatorSymb.colon;
        default: return OperatorSymb.notASymb;
    }
}

/**
 * Reads symbols from '#' until a newline, or until the '.#' combination
 * Accepts arbitrary UTF-8 text (i.e. does not check that the byte is within ASCII range)
 */
static Either<LexError, Tuple<ExprBase, Integer>> lexComment(byte[] inp, int start, int walkLen) {
    int i = start + 1;
    int startContent = start + 1;
    int endContent = -1;
    while (i <= walkLen) {
        if (inp[i] == ASCII.emptyLF) {
            endContent = i - 1;
            ++i;
            break;
        } else if ((inp[i] == ASCII.dot
                    && i < walkLen
                    && inp[i + 1] == ASCII.hashtag)) {
            endContent = i - 1;
            i += 2;
            break;
        }
        ++i;
    }
    if (endContent == -1) endContent = walkLen;
    if (startContent == endContent) return Either.right(new Tuple<ExprBase, Integer>(new CommentToken(""), i));
    val subList = Arrays.copyOfRange(inp, startContent, endContent);
    return Either.right(new Tuple<ExprBase, Integer>(new CommentToken(new String(subList, StandardCharsets.US_ASCII)), i));
}

/**
 * Reads symbols from '"' until another '"', while skipping '\"' combination.
 * Accepts arbitrary UTF-8 text (i.e. does not check that the byte is within ASCII range)
 */
static Either<LexError, Tuple<ExprBase, Integer>> lexStringLiteral(byte[] inp, int start, int walkLen) {
    // TODO replace the escaped double quote characters in the string literals
    int i = start + 1;
    int startContent = start + 1;
    int endContent = -1;
    while (i <= walkLen) {
        if (inp[i] == ASCII.quotationMarkDouble) {
            endContent = i - 1;
            ++i;

            break;
        } else if ((inp[i] == ASCII.slashBackward
                    && i < walkLen
                    && inp[i + 1] == ASCII.quotationMarkDouble)) {
            i += 2;
        }
        ++i;
    }
    if (endContent == -1) return Either.left(new LexError("End of input error"));
    if (startContent == endContent) return Either.right(new Tuple<ExprBase, Integer>(new StringToken(""), i));
    val subList = new byte[endContent - startContent + 1];
    for (int j = startContent; j <= endContent; ++j) {
        subList[j - startContent] = inp[j];
    }
    return Either.right(new Tuple<ExprBase, Integer>(new StringToken(new String(subList, StandardCharsets.US_ASCII)), i));
}

private static ByteList filterBytes(byte[] inp, int start, int end, Function<Byte, Boolean> predicate) {
    val result = new ByteList(end - start + 1);
    for (int i = start; i <= end; ++i) {
        if (predicate.apply(inp[i])) {
            result.add(inp[i]);
        }
    }
    return result;
}

}
