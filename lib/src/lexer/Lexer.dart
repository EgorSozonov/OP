import "dart:typed_data";
import "package:either_dart/either.dart";
import 'package:o7/src/types/ASCII.dart';

import "package:o7/src/types/LexError.dart";
import 'package:tuple/tuple.dart';

import "../types/Token.dart";

class Lexer {
    static List<Token> lexicallyAnalyze(Uint8List inp) {
        if (inp.length < 2) return [];

        int i = 0;
        List<Token> result = [];
        final walkLen = inp.length - 2;
        for (var j = 0; j < walkLen; ++j) {

        }
        return result;
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

    static Either<LexError, Tuple2<Token, int>> lexInt(Uint8List inp, int start, int walkLen) {
        if (start >= walkLen) return Left(EndOfInputError());

        var ind = start;
        var currByte = inp[start];
        if (currByte != ASCII.MINUS.index && (currByte < ASCII.DIGIT_0.index || currByte > ASCII.DIGIT_9.index)) {
            return Left(IntError("Int lexical error: expected the first symbol to be a digit or '-'"));
        }
        bool isNegative = currByte == ASCII.MINUS.index;
        ++ind;
        if (ind <= walkLen) currByte = inp[ind];
        while (ind <= walkLen &&
            ((currByte >= ASCII.DIGIT_0.index && currByte <= ASCII.DIGIT_9.index)
            || currByte == ASCII.UNDERSCORE.index)) {
            currByte = inp[ind];
            ++ind;
        }

        // Check if we're near the end of the input
        if (ind == (walkLen + 1)) {
            currByte = inp[ind];
            if (currByte >= ASCII.DIGIT_0.index && currByte <= ASCII.DIGIT_9.index) {
                ++ind;
            }
        }

        var digitList = Uint8List.fromList(inp.sublist(isNegative ? start + 1 : start, ind)
                           .where((x) => x != ASCII.UNDERSCORE.index)
                           .toList());

        if (checkIntRange(digitList, isNegative)) {
            var actualInt = intOfDigits(digitList);
            return Right(Tuple2(IntToken(isNegative ? (-1)*actualInt : actualInt), ind));
        } else {
            return Left(
                IntError("Int lexical error: number outside range [-9,223,372,036,854,775,808; 9,223,372,036,854,775,807]"));
        }
    }

    static bool checkIntRange(Uint8List digits, bool isNegative) {
        if (digits.length < 19) return true;
        return false;
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

    static Either<LexError, Tuple2<Token, int>> lexBool(Uint8List inp, int start, int walkLen) {
        int remainingLength = walkLen + 2 - start;
        final Either<LexError, Tuple2<Token, int>> err = Left(BoolError("Boolean lexical error: expected either 'true' or 'false'"));
        if (remainingLength >= 5 && inp[start] == ASCII.F_LOWER.index) {
            var charList = Uint8List.fromList(inp.sublist(start + 1, start + 5)
                           .toList());
            if (charList[0] == ASCII.A_LOWER.index && charList[1] == ASCII.L_LOWER.index
               && charList[2] == ASCII.S_LOWER.index && charList[3] == ASCII.E_LOWER.index) {
                return Right(Tuple2(BoolToken(false), start + 4));
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
