import 'dart:typed_data';
import 'package:o7/src/utils/ASCII.dart';
import 'package:o7/src/types/Token.dart';
import "package:test/test.dart";
import 'package:o7/src/lexer/Lexer.dart';


void main() {


    test("lex int 1", () {
        var inp = Uint8List.fromList([ASCII.digit1.index, ASCII.digit2.index, ASCII.digit3.index, ASCII.digit4.index]);
        var res = Lexer.lexNumber(inp, 0, 2);
        expect(res.isRight, true);
        var r = res.right;
        expect(r.item1 is IntToken, true);
        expect((r.item1 as IntToken).val, 1234);
    });


    test("lex int 2", () {
        var inp = Uint8List.fromList([ASCII.minus.index, ASCII.digit1.index]);
        var res = Lexer.lexNumber(inp, 0, 3);
        expect(res.isRight, true);
        var r = res.right;
        expect(r.item1 is IntToken, true);
        expect((r.item1 as IntToken).val, -1);
    });
}
