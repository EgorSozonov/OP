import "dart:typed_data";
import "dart:either_dart";

import "package:o7/src/types/LexError.dart";

import "../types/Token.dart";

class Lexer {
    static List<Token> lexicallyAnalyze(Uint8List inp) {
        if (inp.length < 2) return [];

        int i = 0;
        List<Token> result = [];
        final walkLen = inp.length - 1;
        for (var j = 0; j < walkLen; ++j) {

        }
        return result;
    }

    Either<LexError, Token> lexInt(Uint8List inp, int start, int walkLen) {
    }

}
