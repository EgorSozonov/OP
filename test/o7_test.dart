import 'dart:typed_data';
import 'package:either_dart/either.dart';
import "package:o7/o7.dart";
import 'package:o7/src/types/ASCII.dart';
import 'package:o7/src/types/Token.dart';
import "package:test/test.dart";
import 'package:o7/src/lexer/Lexer.dart';
import 'package:tuple/tuple.dart';


void main() {
    test("calculate", () {
        expect(calculate(), 42);
    });

    test("lex boolean 1", () {
        var inp = Uint8List.fromList([ASCII.F_LOWER.index, ASCII.A_LOWER.index, ASCII.L_LOWER.index, ASCII.S_LOWER.index, ASCII.E_LOWER.index]);
        expect(Lexer.lexBool(inp, 0, 3), Right(Tuple2(BoolToken(false), 5)));
    });

    test("lex boolean 2", () {
        var inp = Uint8List.fromList([ASCII.T_LOWER.index, ASCII.R_LOWER.index, ASCII.U_LOWER.index, ASCII.E_LOWER.index]);
        expect(Lexer.lexBool(inp, 0, 2), Right(Tuple2(BoolToken(true), 4)));
    });

    test("lex int 1", () {
        var inp = Uint8List.fromList([ASCII.DIGIT_1.index, ASCII.DIGIT_2.index, ASCII.DIGIT_3.index, ASCII.DIGIT_4.index]);
        expect(Lexer.lexInt(inp, 0, 2), Right(Tuple2(IntToken(1234), 4)));
    });


    test("lex int 2", () {
        var inp = Uint8List.fromList([ASCII.MINUS.index, ASCII.DIGIT_1.index, ASCII.DIGIT_2.index, ASCII.DIGIT_3.index, ASCII.DIGIT_4.index]);
        print (Lexer.lexInt(inp, 0, 3));
        expect(Lexer.lexInt(inp, 0, 3), Right(Tuple2(IntToken(-1234), 5)));
    });
}
