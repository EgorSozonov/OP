

namespace O7;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using LanguageExt;
using LexResult = LanguageExt.Either<LexError, System.Tuple<Expr, int>>;

class Lexer {
    static Tuple<Expr, LexError> lexicallyAnalyze(byte[] inp) {
        LexError err = null;
        if (inp.Length < 2) return new Tuple<Expr, LexError>(new ListExpr(ExprLexicalType.curlyBraces), err);

        int i = 0;
        var curr = new ListExpr(ExprLexicalType.curlyBraces);
        var result = curr;
        var backtrack = new Stack<ListExpr>();
        backtrack.push(curr);
        var firstList = new ListExpr(ExprLexicalType.statement);
        curr.val.Add(firstList);
        curr = firstList;

        int walkLen = inp.Length - 1;
        while (i <= walkLen) {
            int cChar = inp[i];
            if (cChar > 127) return new Tuple<Expr, LexError>(result, new NonAsciiError());
            LexResult mbToken;

            if (cChar == (byte)ASCII.space || cChar == (byte)ASCII.emptyCR) {
                ++i;
            } else if (cChar == (byte)ASCII.emptyLF) {
                if (backtrack.peek() != null && backtrack.peek()?.pType == ExprLexicalType.curlyBraces) {
                    // we are in a CurlyBraces context, so a newline means a new statement
                    var back = backtrack.peek();
                    var newStatement = new ListExpr(ExprLexicalType.statement);
                    back?.val.Add(newStatement);
                    curr = newStatement;
                }
                ++i;

            } else if (cChar == (byte)ASCII.colonSemi) {
                if (curr.pType == ExprLexicalType.parens) {
                    return new Tuple<Expr, LexError>(result, new UnexpectedSymbolError(
                        "Semi-colons are not allowed in inline expressions (i.e. directly inside parentheses)"));
                }
                var prevList = backtrack.peek();

                if (prevList != null) {
                    curr = new ListExpr(ExprLexicalType.statement);
                    prevList.val.Add(curr);
                } else {
                    return new Tuple<Expr, LexError>(result, new EmptyStackError());
                }

                ++i;
            } else if (cChar == (byte)ASCII.curlyOpen) {

                var newList = new ListExpr(ExprLexicalType.curlyBraces);
                var newCurr = new ListExpr(ExprLexicalType.statement);
                newList.val.Add(newCurr);
                curr.val.Add(newList);
                backtrack.push(curr);
                backtrack.push(newList);
                curr = newCurr;
                ++i;
            } else if (cChar == (byte)ASCII.curlyClose) {
                if (backtrack.peek() == null || backtrack.peek()?.pType != ExprLexicalType.curlyBraces) {
                    return new Tuple<Expr, LexError>(result, new ExtraClosingCurlyBraceError());
                }
                var _ = backtrack.pop();
                if (backtrack.peek() == null) {
                    return new Tuple<Expr, LexError>(result, new ExtraClosingCurlyBraceError());
                }
                // TODO
                var back = backtrack.pop();
                var last = back.val.Last();
                if (last is ListExpr le) {
                    if (!le.val.Any()) {
                        back.val.removeLast();
                    }
                }
                curr = back;
                ++i;
            } else if (cChar == (byte)ASCII.parenthesisOpen) {
                var newList = new ListExpr(ExprLexicalType.parens);
                curr.val.Add(newList);
                backtrack.push(curr);
                curr = newList;
                ++i;
            } else if (cChar == (byte)ASCII.parenthesisClose) {
                if (backtrack.peek() == null || curr.pType != ExprLexicalType.parens) {
                    return new Tuple<Expr, LexError>(result, new ExtraClosingParenError());
                }
                var back = backtrack.pop();

                // TODO
                var last = back.val.Last();
                if (last is ListExpr) {
                    if (!((ListExpr)last).val.Any()) {
                        back.val.removeLast();
                    }
                }

                curr = back;
                ++i;
            }  else if (cChar == (byte)ASCII.bracketOpen) {
                var newList = new ListExpr(ExprLexicalType.dataInitializer);
                curr.val.Add(newList);
                backtrack.push(curr);
                curr = newList;
                ++i;
            } else if (cChar == (byte)ASCII.bracketClose) {
                if (backtrack.peek() == null || curr.pType != ExprLexicalType.dataInitializer) {
                    return new Tuple<Expr, LexError>(result, new ExtraClosingBracketError());
                }
                var back = backtrack.pop();

                // TODO
                var last = back.val.Last();
                if (last is ListExpr le) {
                    if (!le.val.Any()) {
                        back.val.removeLast();
                    }
                }

                curr = back;
                ++i;
            } else {
                if ((cChar >= (byte)ASCII.digit0 && cChar <= (byte)ASCII.digit9)
                    || (i < walkLen && cChar == (byte)ASCII.underscore
                        && inp[i + 1] >= (byte)ASCII.digit0 && inp[i + 1] <= (byte)ASCII.digit9)) {
                    mbToken = lexNumber(inp, i, walkLen);
                } else if ((cChar >= (byte)ASCII.aLower && cChar <= (byte)ASCII.zLower)
                        || (cChar >= (byte)ASCII.aUpper && cChar <= (byte)ASCII.zUpper)
                        || cChar == (byte)ASCII.underscore) {
                    mbToken = lexWord(inp, i, walkLen);
                } else if (cChar == (byte)ASCII.hashtag) {
                    mbToken = lexComment(inp, i, walkLen);
                } else if (cChar == (byte)ASCII.quotationMarkDouble) {
                    mbToken = lexStringLiteral(inp, i, walkLen);
                } else if (isOperatorSymb(cChar) != OperatorSymb.notASymb) {
                    mbToken = lexOperator(inp, i, walkLen);
                } else {
                    mbToken = new UnexpectedSymbolError(inp[i]);
                }
                if (mbToken.IsLeft) {
                    return new Tuple<Expr, LexError>(result, (LexError)mbToken);
                }
                mbToken.Right(x => {
                    curr.val.Add(x.Item1);
                    i = x.Item2;
                });


            }
        }
        if (backtrack.peek() != null) {
            var back = backtrack.pop();
            var last = back.val.Last();
            if (last is ListExpr le) {
                if (!le.val.Any()) {
                    back.val.removeLast();
                }
            }
        }
        return new Tuple<Expr, LexError>(result, err);
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

        bool isNegative = currByte == (byte)ASCII.underscore;
        if (isNegative) {
            ++ind;
            if (ind <= walkLen) currByte = inp[ind];
        }
        bool isFloating = false;

        while (ind <= walkLen &&
            ((currByte >= (byte)ASCII.digit0 && currByte <= (byte)ASCII.digit9)
            || currByte == (byte)ASCII.underscore)) {
            ++ind;
            if (ind <= walkLen) currByte = inp[ind];
        }

        // In case the next symbol is a dot, this will be a floating-point number
        if (ind < walkLen && inp[ind] == (byte)ASCII.dot) {
            var nextBt = inp[ind + 1];
            if (nextBt >= (byte)ASCII.digit0 && nextBt <= (byte)ASCII.digit9) {
                isFloating = true;
                // Skipping the dot
                ++ind;
                currByte = nextBt;
                while (ind <= walkLen &&
                        ((currByte >= (byte)ASCII.digit0 && currByte <= (byte)ASCII.digit9)
                        || currByte == (byte)ASCII.underscore)) {
                    ++ind;
                    if (ind <= walkLen) currByte = inp[ind];
                }
            }
        }
        int startingDigit = isNegative ? start + 1 : start;
        var mbNumber = isFloating ? lexFloat(inp, startingDigit, ind - 1, isNegative)
                                  : lexInt(inp, startingDigit, ind - 1, isNegative);
        return mbNumber.BiMap(x => new Tuple<Expr, int>(x, ind), x => x);
    }


    static Either<LexError, Expr> lexInt(byte[] inp, int start, int endInclusive, bool isNegative) {
        var digitList = filterBytes(inp, start, endInclusive, (x) => x != (byte)ASCII.underscore);

        if (checkIntRange(digitList, isNegative)) {
            var actualInt = intOfDigits(digitList);
            return new IntToken(isNegative ? (-1)*actualInt : actualInt);
        } else {
            return
                new IntError("Int lexical error: number outside range [-9,223,372,036,854,775,808; 9,223,372,036,854,775,807]");
        }
    }


    static Either<LexError, Expr> lexFloat(byte[] inp, int start, int endInclusive, bool isNegative) {
        var digitList = filterBytes(inp, start, endInclusive, (x) => x != (byte)ASCII.underscore);
        var str = (isNegative ? "-" : "") + Encoding.ASCII.GetString(digitList.ToArray());
        if (double.TryParse(str, out double fl)) {
            return new FloatToken(fl);
        } else {
            return new IntError("Float lexical error: '$str' not parsing as a floating-point number");
        }
    }


    static bool checkIntRange(List<byte> digits, bool isNegative) {
        if (digits.Count != 19) return digits.Count < 19;
        return isNegative ? _isLexicographicallyLE(digits, minInt) : _isLexicographicallyLE(digits, maxInt);
    }


    static bool _isLexicographicallyLE(List<byte> a, byte[] b) {
        for (int i = 0; i < a.Count; ++i) {
            if (a[i] < b[i]) return true;
            if (a[i] > b[i]) return false;
        }
        return true;
    }


    static int intOfDigits(List<byte> digits) {
        int result = 0;
        int powerOfTen = 1;
        for (int ind = digits.Count - 1; ind > -1; --ind) {
            int digitValue = (digits[ind] - (byte)ASCII.digit0)*powerOfTen;
            result += digitValue;
            powerOfTen *= 10;
        }
        return result;
    }


    static LexResult lexWord(byte[] inp, int start, int walkLen) {
        int i = start;
        var currByte = inp[i];
        while (i <= walkLen && (currByte == (byte)ASCII.underscore)) {
            ++i;
            if (i <= walkLen) currByte = inp[i];
        }
        int startOfLetters = i;
        while (i <= walkLen &&
            (  (currByte >= (byte)ASCII.aLower && currByte <= (byte)ASCII.zLower)
            || (currByte >= (byte)ASCII.aUpper && currByte <= (byte)ASCII.zUpper))) {
            ++i;
            if (i <= walkLen) currByte = inp[i];
        }
        if (i == startOfLetters) {
            return new WordError("Word did not contain any letters");
        }
        if (i <= walkLen && inp[i] == (byte)ASCII.underscore) {
            return new WordError("Snake-case identifier ${String.fromCharCodes(byte[].fromList(inp.sublist(start, i).toList()))}_");
        }
        var subList = new byte[i - start];
        Array.Copy(inp, start, subList, 0, i - start);

        return new Tuple<Expr, int>(new WordToken(subList), i);
    }


    /// Operators
    /// Lexes a sequence of 1 to 3 of any of the following symbols:
    /// & + - / * ! ~ $ % ^ | > < ? =
    static LexResult lexOperator(byte[] inp, int start, int walkLen) {
        int i = start;
        var result = new List<OperatorSymb>();
        var currByte = inp[i];
        while (i <= walkLen && i < start + 3) {
            var smb = isOperatorSymb(currByte);
            if (smb == OperatorSymb.notASymb) break;

            result.Add(smb);
            ++i;
            if (i <= walkLen) currByte = inp[i];
        }
        if (i <= walkLen && isOperatorSymb(inp[i]) != OperatorSymb.notASymb) {
            return new OperatorError("Operators longer than 3 symbols are not allowed");
        }
        return new Tuple<Expr, int>(new OperatorToken(result), i);
    }


    static OperatorSymb isOperatorSymb(int symb) {
        if (symb == (int)ASCII.ampersand) return OperatorSymb.ampersand;
        if (symb == (int)ASCII.plus) return OperatorSymb.plus;
        if (symb == (int)ASCII.minus) return OperatorSymb.minus;
        if (symb == (int)ASCII.slashForward) return OperatorSymb.slash;
        if (symb == (int)ASCII.asterisk) return OperatorSymb.asterisk;
        if (symb == (int)ASCII.exclamationMark) return OperatorSymb.exclamation;
        if (symb == (int)ASCII.tilde) return OperatorSymb.tilde;
        if (symb == (int)ASCII.dollar) return OperatorSymb.dollar;
        if (symb == (int)ASCII.percent) return OperatorSymb.percent;
        if (symb == (int)ASCII.caret) return OperatorSymb.caret;
        if (symb == (int)ASCII.verticalBar) return OperatorSymb.pipe;
        if (symb == (int)ASCII.greaterThan) return OperatorSymb.gt;
        if (symb == (int)ASCII.lessThan) return OperatorSymb.lt;
        if (symb == (int)ASCII.questionMark) return OperatorSymb.question;
        if (symb == (int)ASCII.equalTo) return OperatorSymb.equals;
        if (symb == (int)ASCII.colon) return OperatorSymb.colon;
        return OperatorSymb.notASymb;
    }


    /// Reads symbols from '#' until a newline, or until the '.#' combination
    /// Accepts arbitrary UTF-8 text (i.e. does not check that the byte
    /// is within (byte)ASCII range)
    static LexResult lexComment(byte[] inp, int start, int walkLen) {
        int i = start + 1;
        int startContent = start + 1;
        int endContent = -1;
        while (i <= walkLen) {
            if (inp[i] == (byte)ASCII.emptyLF) {
                endContent = i - 1;
                ++i;
                break;
            } else if ((inp[i] == (byte)ASCII.dot
                        && i < walkLen
                        && inp[i + 1] == (byte)ASCII.hashtag)) {
                endContent = i - 1;
                i += 2;
                break;
            }
            ++i;
        }
        if (endContent == -1) endContent = walkLen;
        if (startContent == endContent) return new Tuple<Expr, int>(new CommentToken(""), i);
        var subList = new byte[endContent - startContent + 1];
        Array.Copy(inp, startContent, subList, 0, endContent - startContent + 1);
        return new Tuple<Expr, int>(new CommentToken(Encoding.ASCII.GetString(subList)), i);
    }

    /// Reads symbols from '"' until another '"', while skipping '\"' combination.
    /// Accepts arbitrary UTF-8 text (i.e. does not check that the byte
    /// is within (byte)ASCII range)
    static LexResult lexStringLiteral(byte[] inp, int start, int walkLen) {
        int i = start + 1;
        int startContent = start + 1;
        int endContent = -1;
        while (i <= walkLen) {
            if (inp[i] == (byte)ASCII.quotationMarkDouble) {
                endContent = i - 1;
                ++i;

                break;
            } else if ((inp[i] == (byte)ASCII.slashBackward
                        && i < walkLen
                        && inp[i + 1] == (byte)ASCII.quotationMarkDouble)) {
                i += 2;
            }
            ++i;
        }
        if (endContent == -1) return new EndOfInputError();
        if (startContent == endContent) return new Tuple<Expr, int>(new StringToken(""), i);
        var subList = new byte[endContent - startContent + 1];
        Array.Copy(inp, startContent, subList, 0, endContent - startContent + 1);
        return new Tuple<Expr, int>(new StringToken(Encoding.ASCII.GetString(subList)), i);
    }

    private static List<byte> filterBytes(byte[] inp, int start, int end, Func<byte, bool> predicate) {
        var result = new List<byte>(end - start + 1);
        for (int i = start; i <= end; ++i) {
            if (predicate(inp[i])) {
                result.Add(inp[i]);
            }
        }
        return result;
    }
}
