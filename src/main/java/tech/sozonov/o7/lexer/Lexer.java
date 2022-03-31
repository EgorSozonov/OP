package main.java.tech.sozonov.o7.lexer;
import main.java.tech.sozonov.o7.lexer.types.LexError.*;
import main.java.tech.sozonov.o7.lexer.types.Expr.*;
import main.java.tech.sozonov.o7.lexer.types.ExprLexicalType;
import main.java.tech.sozonov.o7.lexer.types.OperatorSymb;
import main.java.tech.sozonov.o7.utils.ASCII;
import main.java.tech.sozonov.o7.utils.Tuple;
import main.java.tech.sozonov.o7.utils.Stack;
import static main.java.tech.sozonov.o7.utils.ListUtils.*;

public class Lexer {
    public static Tuple<ExprBase, LexErrorBase> lexicallyAnalyze(byte[] inp) {
        LexErrorBase err = null;
        if (inp.length < 2) return new Tuple<ExprBase, LexErrorBase>(new ListExpr(ExprLexicalType.curlyBraces), err);

        int i = 0;
        var curr = new ListExpr(ExprLexicalType.curlyBraces);
        var result = curr;
        var backtrack = new Stack<ListExpr>();
        backtrack.push(curr);
        var firstList = new ListExpr(ExprLexicalType.statement);
        curr.val.add(firstList);
        curr = firstList;

        int walkLen = inp.length - 1;
        while (i <= walkLen) {
            int cChar = inp[i];
            if (cChar > 127) return new Tuple<ExprBase, LexErrorBase>(result, new NonAsciiError());
            LexResult mbToken;

            if (cChar == ASCII.space || cChar == ASCII.emptyCR) {
                ++i;
            } else if (cChar == ASCII.emptyLF) {
                if (backtrack.peek() != null && backtrack.peek()?.pType == ExprLexicalType.curlyBraces
                    && curr.val.size() > 0) {
                    // we are in a CurlyBraces context, so a newline means a new statement
                    var back = backtrack.peek();
                    var newStatement = new ListExpr(ExprLexicalType.statement);
                    back?.val.add(newStatement);
                    curr = newStatement;
                }
                ++i;

            } else if (cChar == ASCII.colonSemi) {
                if (curr.pType == ExprLexicalType.parens) {
                    return new Tuple<ExprBase, LexErrorBase>(result, new UnexpectedSymbolError(
                        "Semi-colons are not allowed in inline expressions (i.e. directly inside parentheses)"));
                }
                var prevList = backtrack.peek();

                if (prevList != null) {
                    curr = new ListExpr(ExprLexicalType.statement);
                    prevList.val.add(curr);
                } else {
                    return new Tuple<ExprBase, LexErrorBase>(result, new EmptyStackError());
                }

                ++i;
            } else if (cChar == ASCII.curlyOpen) {
                var newList = new ListExpr(ExprLexicalType.curlyBraces);
                var newCurr = new ListExpr(ExprLexicalType.statement);
                newList.val.add(newCurr);
                curr.val.add(newList);
                backtrack.push(curr);
                backtrack.push(newList);
                curr = newCurr;
                ++i;
            } else if (cChar == ASCII.curlyClose) {

                if (backtrack.peek() == null || backtrack.peek()?.pType != ExprLexicalType.curlyBraces) {
                    return new Tuple<ExprBase, LexErrorBase>(result, new ExtraClosingCurlyBraceError());
                }
                var _ = backtrack.pop();
                if (backtrack.peek() == null) {
                    return new Tuple<ExprBase, LexErrorBase>(result, new ExtraClosingCurlyBraceError());
                }
                // TODO
                var back = backtrack.pop();
                var theLast = last(back.val);

                if (theLast instanceof ListExpr le &&  !le.val.isEmpty() && last(le.val) instanceof ListExpr le2 && le2.val.isEmpty()) {
                    removeLast(le.val);
                }
                curr = back;
                ++i;
            } else if (cChar == ASCII.parenthesisOpen) {
                var newList = new ListExpr(ExprLexicalType.parens);
                curr.val.add(newList);
                backtrack.push(curr);
                curr = newList;
                ++i;
            } else if (cChar == ASCII.parenthesisClose) {
                if (backtrack.peek() == null || curr.pType != ExprLexicalType.parens) {
                    return new Tuple<ExprBase, LexErrorBase>(result, new ExtraClosingParenError());
                }
                var back = backtrack.pop();

                // TODO
                var theLast = last(back.val);
                if (theLast instanceof ListExpr le && le.val.isEmpty()) {
                    removeLast(back.val);
                }

                curr = back;
                ++i;
            }  else if (cChar == ASCII.bracketOpen) {
                var newList = new ListExpr(ExprLexicalType.dataInitializer);
                curr.val.add(newList);
                backtrack.push(curr);
                curr = newList;
                ++i;
            } else if (cChar == ASCII.bracketClose) {

                if (backtrack.peek() == null || curr.pType != ExprLexicalType.dataInitializer) {
                    return new Tuple<ExprBase, LexErrorBase>(result, new ExtraClosingBracketError());
                }
                var back = backtrack.pop();

                // TODO
                var theLast = last(back.val);
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
                        || cChar == ASCII.underscore) {
                    mbToken = lexWord(inp, i, walkLen);
                } else if (cChar == ASCII.hashtag) {
                    mbToken = lexComment(inp, i, walkLen);
                } else if (cChar == ASCII.quotationMarkDouble) {
                    mbToken = lexStringLiteral(inp, i, walkLen);
                } else if (isOperatorSymb(cChar) != OperatorSymb.notASymb) {
                    mbToken = lexOperator(inp, i, walkLen);
                } else {
                    mbToken = new UnexpectedSymbolError(inp[i]);
                }
                if (mbToken.IsLeft) {
                    return new Tuple<ExprBase, LexErrorBase>(result, (LexErrorBase)mbToken);
                } else {
                    val rt = (Tuple<ExprBase, Integer>)mbToken;
                    curr.val.add(rt.Item1);
                    i = rt.Item2;
                }
            }
        }
        if (backtrack.peek() != null) {
            var back = backtrack.pop();
            var theLast = last(back.val);
            if (theLast instanceof ListExpr le) {
                if (hasValues(le.val)) {
                    removeLast(back.val);
                }
            }
        }
        return new Tuple<ExprBase, LexErrorBase>(result, err);
    }

    // "int" range from -9,223,372,036,854,775,808
    // to 9,223,372,036,854,775,807
    static byte[] minInt = new byte[] {
            57,
            50, 50, 51,
            51, 55, 50,
            48, 51, 54,
            56, 53, 52,
            55, 55, 53,
            56, 48, 56,
        };
    static byte[] maxInt = new byte[] {
            57,
            50, 50, 51,
            51, 55, 50,
            48, 51, 54,
            56, 53, 52,
            55, 55, 53,
            56, 48, 55,
        };

    static LexResult lexNumber(byte[] inp, int start, int walkLen) {
        if (start > walkLen) return new EndOfInputError();

        var ind = start;
        var currByte = inp[start];

        boolean isNegative = currByte == ASCII.underscore;
        if (isNegative) {
            ++ind;
            if (ind <= walkLen) currByte = inp[ind];
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
            var nextBt = inp[ind + 1];
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
        var mbNumber = isFloating ? lexFloat(inp, startingDigit, ind - 1, isNegative)
                                  : lexInt(inp, startingDigit, ind - 1, isNegative);
        return mbNumber.BiMap(x => new Tuple<ExprBase, Integer>(x, ind), x -> x);
    }


    static Either<LexErrorBase, Expr> lexInt(byte[] inp, int start, int endInclusive, boolean isNegative) {
        var digitList = filterBytes(inp, start, endInclusive, (x) => x != ASCII.underscore);

        if (checkIntRange(digitList, isNegative)) {
            var actualInt = intOfDigits(digitList);
            return new IntToken(isNegative ? (-1)*actualInt : actualInt);
        } else {
            return
                new IntError("Int lexical error: number outside range [-9,223,372,036,854,775,808; 9,223,372,036,854,775,807]");
        }
    }


    static Either<LexErrorBase, ExprBase> lexFloat(byte[] inp, int start, int endInclusive, boolean isNegative) {
        var digitList = filterBytes(inp, start, endInclusive, (x) => x != ASCII.underscore);
        var str = (isNegative ? "-" : "") + (new String(digitList.toArray(), StandardCharsets.US_ASCII));
        if (double.TryParse(str, out double fl)) {
            return new FloatToken(fl);
        } else {
            return new IntError("Float lexical error: '$str' not parsing as a floating-point number");
        }
    }


    static boolean checkIntRange(byte[] digits, boolean isNegative) {
        if (digits.size() != 19) return digits.length < 19;
        return isNegative ? _isLexicographicallyLE(digits, minInt) : _isLexicographicallyLE(digits, maxInt);
    }


    static boolean _isLexicographicallyLE(byte[] a, byte[] b) {
        for (int i = 0; i < a.length; ++i) {
            if (a[i] < b[i]) return true;
            if (a[i] > b[i]) return false;
        }
        return true;
    }


    static int intOfDigits(byte[] digits) {
        int result = 0;
        int powerOfTen = 1;
        for (int ind = digits.length - 1; ind > -1; --ind) {
            int digitValue = (digits[ind] - ASCII.digit0)*powerOfTen;
            result += digitValue;
            powerOfTen *= 10;
        }
        return result;
    }


    static LexResult lexWord(byte[] inp, int start, int walkLen) {
        int i = start;
        var currByte = inp[i];
        while (i <= walkLen && (currByte == ASCII.underscore)) {
            ++i;
            if (i <= walkLen) currByte = inp[i];
        }
        int startOfLetters = i;
        while (i <= walkLen &&
            (  (currByte >= ASCII.aLower && currByte <= ASCII.zLower)
            || (currByte >= ASCII.aUpper && currByte <= ASCII.zUpper))) {
            ++i;
            if (i <= walkLen) currByte = inp[i];
        }
        if (i == startOfLetters) {
            return new WordError("Word did not contain any letters");
        }
        if (i <= walkLen && inp[i] == ASCII.underscore) {
            return new WordError("Snake-case identifier ${String.fromCharCodes(byte[].fromList(inp.sublist(start, i).toList()))}_");
        }
        var subList = new byte[i - start];
        Array.Copy(inp, start, subList, 0, i - start);

        return new Tuple<ExprBase, Integer>(new WordToken(subList), i);
    }


    /// Operators
    /// Lexes a sequence of 1 to 3 of any of the following symbols:
    /// & + - / * ! ~ $ % ^ | > < ? =
    static LexResult lexOperator(byte[] inp, int start, int walkLen) {
        int i = start;
        var result = new ArrayList<OperatorSymb>();
        var currByte = inp[i];
        while (i <= walkLen && i < start + 3) {
            var smb = isOperatorSymb(currByte);
            if (smb == OperatorSymb.notASymb) break;

            result.add(smb);
            ++i;
            if (i <= walkLen) currByte = inp[i];
        }
        if (i <= walkLen && isOperatorSymb(inp[i]) != OperatorSymb.notASymb) {
            return new OperatorError("Operators longer than 3 symbols are not allowed");
        }
        return new Tuple<ExprBase, Integer>(new OperatorToken(result), i);
    }


    static OperatorSymb isOperatorSymb(byte symb) {
        if (symb == ASCII.ampersand) return OperatorSymb.ampersand;
        if (symb == ASCII.plus) return OperatorSymb.plus;
        if (symb == ASCII.minus) return OperatorSymb.minus;
        if (symb == ASCII.slashForward) return OperatorSymb.slash;
        if (symb == ASCII.asterisk) return OperatorSymb.asterisk;
        if (symb == ASCII.exclamationMark) return OperatorSymb.exclamation;
        if (symb == ASCII.tilde) return OperatorSymb.tilde;
        if (symb == ASCII.dollar) return OperatorSymb.dollar;
        if (symb == ASCII.percent) return OperatorSymb.percent;
        if (symb == ASCII.caret) return OperatorSymb.caret;
        if (symb == ASCII.verticalBar) return OperatorSymb.pipe;
        if (symb == ASCII.greaterThan) return OperatorSymb.gt;
        if (symb == ASCII.lessThan) return OperatorSymb.lt;
        if (symb == ASCII.questionMark) return OperatorSymb.question;
        if (symb == ASCII.equalTo) return OperatorSymb.equals;
        if (symb == ASCII.colon) return OperatorSymb.colon;
        return OperatorSymb.notASymb;
    }


    /// Reads symbols from '#' until a newline, or until the '.#' combination
    /// Accepts arbitrary UTF-8 text (i.e. does not check that the byte
    /// instanceof within ASCII range)
    static LexResult lexComment(byte[] inp, int start, int walkLen) {
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
        if (startContent == endContent) return new Tuple<ExprBase, Integer>(new CommentToken(""), i);
        var subList = new byte[endContent - startContent + 1];
        Array.Copy(inp, startContent, subList, 0, endContent - startContent + 1);
        return new Tuple<ExprBase, Integer>(new CommentToken(Encoding.ASCII.GetString(subList)), i);
    }

    /// Reads symbols from '"' until another '"', while skipping '\"' combination.
    /// Accepts arbitrary UTF-8 text (i.e. does not check that the byte
    /// instanceof within ASCII range)
    static LexResult lexStringLiteral(byte[] inp, int start, int walkLen) {
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
        if (endContent == -1) return new EndOfInputError();
        if (startContent == endContent) return new Tuple<ExprBase, Integer>(new StringToken(""), i);
        var subList = new byte[endContent - startContent + 1];
        Array.Copy(inp, startContent, subList, 0, endContent - startContent + 1);
        return new Tuple<ExprBase, Integer>(new StringToken(new String(subList, StandardCharsets.US_ASCII)), i);
    }

    private static List<byte> filterBytes(byte[] inp, int start, int end, Func<byte, bool> predicate) {
        var result = new List<byte>(end - start + 1);
        for (int i = start; i <= end; ++i) {
            if (predicate(inp[i])) {
                result.add(inp[i]);
            }
        }
        return result;
    }
}
