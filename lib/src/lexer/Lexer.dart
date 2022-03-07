import "dart:typed_data";
import "package:either_dart/either.dart";
import "package:o7/src/types/ParenType.dart";
import "package:o7/src/utils/ASCII.dart";
import "package:o7/src/types/LexError.dart";
import "package:tuple/tuple.dart";
import "../types/OperatorSymb.dart";
import "../types/Token.dart";
import "../utils/Stack.dart";


class Lexer {
    static Tuple2<List<Expr>, LexError?> lexicallyAnalyze(Uint8List inp) {
        LexError? err;
        if (inp.length < 2) return Tuple2([], err);

        int i = 0;
        var curr = ListExpr([], ExprLexicalType.CurlyBraces);
        var result = [curr];
        Stack<ListExpr> backtrack = Stack();
        backtrack.push(curr);
        var firstList = ListExpr([], ExprLexicalType.Statement);
        curr.val.add(firstList);
        curr = firstList;

        final walkLen = inp.length - 1;
        while (i <= walkLen) {
            int cChar = inp[i];
            if (cChar > 127) return Tuple2(result, NonAsciiError());
            Either<LexError, Tuple2<Expr, int>> mbToken;

            if (cChar == ASCII.SPACE.index || cChar == ASCII.EMPTY_CR.index) {
                ++i;
            } else if (cChar == ASCII.EMPTY_LF.index) {
                if (backtrack.peek() != null && backtrack.peek()?.pType == ExprLexicalType.CurlyBraces) {
                    // we are in a CurlyBraces context, so a newline means a new statement
                    var back = backtrack.peek();
                    var newStatement = ListExpr([], ExprLexicalType.Statement);
                    back?.val.add(newStatement);
                    curr = newStatement;
                }
                ++i;

            } else if (cChar == ASCII.COLON_SEMI.index) {
                // TODO check that we're neither in a statement nor in a datainit
                var prevList = backtrack.peek();

                if (prevList != null) {
                    curr = ListExpr([], ExprLexicalType.Statement);
                    prevList.val.add(curr);
                } else {
                    return Tuple2(result, EmptyStackError());
                }

                ++i;
            } else if (cChar == ASCII.CURLY_OPEN.index) {

                var newList = ListExpr([], ExprLexicalType.CurlyBraces);
                var newCurr = ListExpr([], ExprLexicalType.Statement);
                newList.val.add(newCurr);
                curr.val.add(newList);
                backtrack.push(curr);
                backtrack.push(newList);
                curr = newCurr;
                ++i;
            } else if (cChar == ASCII.CURLY_CLOSE.index) {
                if (backtrack.peek() == null || backtrack.peek()?.pType != ExprLexicalType.CurlyBraces) {
                    if (backtrack.peek() == null) {
                        print("backtrack is null") ;
                    } else {
                        print("pType is ${backtrack.peek()?.pType}, len is ${backtrack.peek()?.val.length}");
                    }

                    return Tuple2(result, ExtraClosingCurlyBraceError());
                }
                var backCurlyBrace = backtrack.pop();
                if (backtrack.peek() == null) {
                    return Tuple2(result, ExtraClosingCurlyBraceError());
                }
                curr = backtrack.pop();
                ++i;
            } else if (cChar == ASCII.PARENTHESES_OPEN.index) {
                var newList = ListExpr([], ExprLexicalType.Statement);
                curr.val.add(newList);
                backtrack.push(curr);
                curr = newList;
                ++i;
            } else if (cChar == ASCII.PARENTHESES_CLOSE.index) {
                if (backtrack.peek() == null || curr.pType != ExprLexicalType.Statement) {
                    return Tuple2(result, ExtraClosingParenError());
                }
                var back = backtrack.pop();
                curr = back;
                ++i;
            }  else if (cChar == ASCII.BRACKET_OPEN.index) {
                var newList = ListExpr([], ExprLexicalType.DataInitializer);
                curr.val.add(newList);
                backtrack.push(curr);
                curr = newList;
                ++i;
            } else if (cChar == ASCII.BRACKET_CLOSE.index) {
                if (backtrack.peek() == null || curr.pType != ExprLexicalType.DataInitializer) {
                    return Tuple2(result, ExtraClosingBracketError());
                }
                var back = backtrack.pop();
                curr = back;
                ++i;
            } else {
                if ((cChar >= ASCII.DIGIT_0.index && cChar <= ASCII.DIGIT_9.index)
                    || (i < walkLen && cChar == ASCII.UNDERSCORE.index
                        && inp[i + 1] >= ASCII.DIGIT_0.index && inp[i + 1] <= ASCII.DIGIT_9.index)) {
                    mbToken = lexNumber(inp, i, walkLen);
                } else if ((cChar >= ASCII.A_LOWER.index && cChar <= ASCII.Z_LOWER.index)
                        || (cChar >= ASCII.A_UPPER.index && cChar <= ASCII.Z_UPPER.index)
                        || cChar == ASCII.UNDERSCORE.index) {
                    mbToken = lexWord(inp, i, walkLen);
                } else if (isOperatorSymb(cChar) != OperatorSymb.notASymb) {
                    mbToken = lexOperator(inp, i, walkLen);
                } else {
                    mbToken = Left(UnexpectedSymbolError(String.fromCharCode(inp[i])));
                }
                if (mbToken.isRight) {
                    curr.val.add(mbToken.right.item1);
                    i = mbToken.right.item2;
                } else {
                    return Tuple2(result, mbToken.left);
                }
            }
        }
        return Tuple2(result, err);
    }

    // "int" range from -9,223,372,036,854,775,808
    // to 9,223,372,036,854,775,807
    static Uint8List minInt = Uint8List.fromList(const [
            57,
            50, 50, 51,
            51, 55, 50,
            48, 51, 54,
            56, 53, 52,
            55, 55, 53,
            56, 48, 56
        ]);
    static Uint8List maxInt = Uint8List.fromList(const [
            57,
            50, 50, 51,
            51, 55, 50,
            48, 51, 54,
            56, 53, 52,
            55, 55, 53,
            56, 48, 55
        ]);

    static Either<LexError, Tuple2<Expr, int>> lexNumber(Uint8List inp, int start, int walkLen) {
        if (start > walkLen) return Left(EndOfInputError());

        var ind = start;
        var currByte = inp[start];

        bool isNegative = currByte == ASCII.UNDERSCORE.index;
        if (isNegative) {
            ++ind;
            if (ind <= walkLen) currByte = inp[ind];
        }
        bool isFloating = false;

        while (ind <= walkLen &&
            ((currByte >= ASCII.DIGIT_0.index && currByte <= ASCII.DIGIT_9.index)
            || currByte == ASCII.UNDERSCORE.index)) {
            ++ind;
            if (ind <= walkLen) currByte = inp[ind];
        }

        // In case the next symbol is a dot, this will be a floating-point number
        if (ind < walkLen && inp[ind] == ASCII.DOT.index) {
            var nextBt = inp[ind + 1];
            if (nextBt >= ASCII.DIGIT_0.index && nextBt <= ASCII.DIGIT_9.index) {
                isFloating = true;
                // Skipping the dot
                ++ind;
                currByte = nextBt;
                while (ind <= walkLen &&
                        ((currByte >= ASCII.DIGIT_0.index && currByte <= ASCII.DIGIT_9.index)
                        || currByte == ASCII.UNDERSCORE.index)) {
                    ++ind;
                    if (ind <= walkLen) currByte = inp[ind];
                }
            }
        }
        int startingDigit = isNegative ? start + 1 : start;
        var mbNumber = isFloating ? lexFloat(inp, startingDigit, ind - 1, isNegative)
                                  : lexInt(inp, startingDigit, ind - 1, isNegative);
        if (mbNumber.isLeft) {
            return Left(mbNumber.left);
        } else {
            return Right(Tuple2(mbNumber.right, ind));
        }
        // var digitList = Uint8List.fromList(inp.sublist(isNegative ? start + 1 : start, ind)
        //                    .where((x) => x != ASCII.UNDERSCORE.index)
        //                    .toList());

        // if (checkIntRange(digitList, isNegative)) {
        //     var actualInt = intOfDigits(digitList);
        //     return Right(Tuple2(IntToken(isNegative ? (-1)*actualInt : actualInt), ind));
        // } else {
        //     return Left(
        //         IntError("Int lexical error: number outside range [-9,223,372,036,854,775,808; 9,223,372,036,854,775,807]"));
        // }
    }


    static Either<LexError, Expr> lexInt(Uint8List inp, int start, int endInclusive, bool isNegative) {
        var digitList = Uint8List.fromList(inp.sublist(start, endInclusive + 1)
                           .where((x) => x != ASCII.UNDERSCORE.index)
                           .toList());

        if (checkIntRange(digitList, isNegative)) {
            var actualInt = intOfDigits(digitList);
            return Right(IntToken(isNegative ? (-1)*actualInt : actualInt));
        } else {
            return Left(
                IntError("Int lexical error: number outside range [-9,223,372,036,854,775,808; 9,223,372,036,854,775,807]"));
        }
    }

    static Either<LexError, Expr> lexFloat(Uint8List inp, int start, int endInclusive, bool isNegative) {
        var digitList = Uint8List.fromList(inp.sublist(start, endInclusive + 1)
                           .where((x) => x != ASCII.UNDERSCORE.index)
                           .toList());
        var str = (isNegative ? "-" : "") + String.fromCharCodes(digitList);
        var mbFloat = double.tryParse(str);

        if (mbFloat != null) {
            return Right(FloatToken(mbFloat));
        } else {
            return Left(
                IntError("Float lexical error: '$str' not parsing as a floating-point number"));
        }
    }


    static bool checkIntRange(Uint8List digits, bool isNegative) {
        if (digits.length < 19) return true;
        if (digits.length > 19) return false;
        return isNegative ? _isLexicographicallyLE(digits, minInt) : _isLexicographicallyLE(digits, maxInt);
    }


    static bool _isLexicographicallyLE(Uint8List a, Uint8List b) {
        for (int i = 0; i < a.length; ++i) {
            if (a[i] < b[i]) return true;
            if (a[i] > b[i]) return false;
        }
        return true;
    }


    static int intOfDigits(Uint8List digits) {
        int result = 0;
        int powerOfTen = 1;
        for (int ind = digits.length - 1; ind > -1; --ind) {
            int digitValue = (digits[ind] - ASCII.DIGIT_0.index)*powerOfTen;
            result += digitValue;
            powerOfTen *= 10;
        }
        return result;
    }


    static Either<LexError, Tuple2<Expr, int>> lexWord(Uint8List inp, int start, int walkLen) {
        int i = start;
        var currByte = inp[i];
        while (i <= walkLen && (currByte == ASCII.UNDERSCORE.index)) {
            ++i;
            if (i <= walkLen) currByte = inp[i];
        }
        int startOfLetters = i;
        while (i <= walkLen &&
            (  (currByte >= ASCII.A_LOWER.index && currByte <= ASCII.Z_LOWER.index)
            || (currByte >= ASCII.A_UPPER.index && currByte <= ASCII.Z_UPPER.index))) {
            ++i;
            if (i <= walkLen) currByte = inp[i];
        }
        if (i == startOfLetters) {
            return Left(WordError("Word did not contain any letters"));
        }
        if (i <= walkLen && inp[i] == ASCII.UNDERSCORE.index) {
            return Left(WordError("Snake-case identifier ${String.fromCharCodes(Uint8List.fromList(inp.sublist(start, i).toList()))}_"));
        }
        var wrd = Uint8List.fromList(inp.sublist(start, i).toList());
        return Right(Tuple2(WordToken(wrd), i));
    }


    /// Operators
    /// Lexes a sequence of 1 to 3 of any of the following symbols:
    /// & + - / * ! ~ $ % ^ | > < ? =
    static Either<LexError, Tuple2<Expr, int>> lexOperator(Uint8List inp, int start, int walkLen) {
        int i = start;
        List<OperatorSymb> result = [];
        var currByte = inp[i];
        while (i <= walkLen && i < start + 3) {
            var smb = isOperatorSymb(currByte);
            if (smb == OperatorSymb.notASymb) break;

            result.add(smb);
            ++i;
            if (i <= walkLen) currByte = inp[i];
        }
        if (i <= walkLen && isOperatorSymb(inp[i]) != OperatorSymb.notASymb) {
            return Left(OperatorError("Operators longer than 3 symbols are not allowed"));
        }
        return Right(Tuple2(OperatorToken(result), i));
    }


    static OperatorSymb isOperatorSymb(int symb) {
        if (symb == ASCII.AMPERSAND.index) return OperatorSymb.ampersand;
        if (symb == ASCII.PLUS.index) return OperatorSymb.plus;
        if (symb == ASCII.MINUS.index) return OperatorSymb.minus;
        if (symb == ASCII.SLASH_FORWARD.index) return OperatorSymb.slash;
        if (symb == ASCII.ASTERISK.index) return OperatorSymb.asterisk;
        if (symb == ASCII.EXCLAMATION_MARK.index) return OperatorSymb.exclamation;
        if (symb == ASCII.TILDE.index) return OperatorSymb.tilde;
        if (symb == ASCII.DOLLAR.index) return OperatorSymb.dollar;
        if (symb == ASCII.PERCENT.index) return OperatorSymb.percent;
        if (symb == ASCII.CARET.index) return OperatorSymb.caret;
        if (symb == ASCII.VERTICAL_BAR.index) return OperatorSymb.pipe;
        if (symb == ASCII.GREATER_THAN.index) return OperatorSymb.gt;
        if (symb == ASCII.LESS_THAN.index) return OperatorSymb.lt;
        if (symb == ASCII.QUESTION_MARK.index) return OperatorSymb.question;
        if (symb == ASCII.EQUAL_TO.index) return OperatorSymb.equals;
        return OperatorSymb.notASymb;
    }


    @Deprecated("Use 'lexWord' instead")
    static Either<LexError, Tuple2<Expr, int>> lexBool(Uint8List inp, int start, int walkLen) {
        int remainingLength = walkLen + 1 - start;
        final Either<LexError, Tuple2<Expr, int>> err = Left(BoolError("Boolean lexical error: expected either 'true' or 'false'"));
        if (remainingLength >= 5 && inp[start] == ASCII.F_LOWER.index) {
            var charList = Uint8List.fromList(inp.sublist(start + 1, start + 5)
                           .toList());
            if (charList[0] == ASCII.A_LOWER.index && charList[1] == ASCII.L_LOWER.index
               && charList[2] == ASCII.S_LOWER.index && charList[3] == ASCII.E_LOWER.index) {
                return Right(Tuple2(BoolToken(false), start + 5));
            } else {
                return err;
            }
        } else if (remainingLength >= 4 && inp[start] == ASCII.T_LOWER.index) {
            var charList = Uint8List.fromList(inp.sublist(start + 1, start + 4)
                           .toList());
            if (charList[0] == ASCII.R_LOWER.index && charList[1] == ASCII.U_LOWER.index
               && charList[2] == ASCII.E_LOWER.index) {
                return Right(Tuple2(BoolToken(true), start + 4));
            } else {
                return err;
            }
        } else {
            return err;
        }
    }
}
