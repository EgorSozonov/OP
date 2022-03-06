import 'dart:typed_data';
import 'package:o7/src/utils/ASCII.dart';
import 'package:o7/src/types/Token.dart';
import "package:test/test.dart";
import 'package:o7/src/lexer/Lexer.dart';


void main() {

    test("lex boolean 1", () {
        var inp = Uint8List.fromList("false".codeUnits);
        var res = Lexer.lexBool(inp, 0, 3);
        expect(res.isRight, true);
        var r = res.right;
        expect(r.item1 is BoolToken, true);
        expect((r.item1 as BoolToken).val, false);
    });

    test("lex boolean 2", () {
        var inp = Uint8List.fromList([ASCII.T_LOWER.index, ASCII.R_LOWER.index, ASCII.U_LOWER.index, ASCII.E_LOWER.index]);
        var res = Lexer.lexBool(inp, 0, 2);
        expect(res.isRight, true);
        var r = res.right;
        expect(r.item1 is BoolToken, true);
        expect((r.item1 as BoolToken).val, true);
    });

    test("lex int 1", () {
        var inp = Uint8List.fromList([ASCII.DIGIT_1.index, ASCII.DIGIT_2.index, ASCII.DIGIT_3.index, ASCII.DIGIT_4.index]);
        var res = Lexer.lexInt(inp, 0, 2);
        expect(res.isRight, true);
        var r = res.right;
        expect(r.item1 is IntToken, true);
        expect((r.item1 as IntToken).val, 1234);
    });


    test("lex int 2", () {
        var inp = Uint8List.fromList([ASCII.MINUS.index, ASCII.DIGIT_1.index]);
        var res = Lexer.lexInt(inp, 0, 3);
        expect(res.isRight, true);
        var r = res.right;
        expect(r.item1 is IntToken, true);
        expect((r.item1 as IntToken).val, -1);
    });
}
