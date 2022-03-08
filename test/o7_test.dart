import 'dart:typed_data';
import 'package:o7/src/types/OperatorSymb.dart';
import 'package:o7/src/types/ParenType.dart';
import 'package:o7/src/utils/ASCII.dart';
import 'package:o7/src/types/Token.dart';
import "package:test/test.dart";
import 'package:o7/src/lexer/Lexer.dart';


void main() {


    test("lex int 1", () {
        var inp = Uint8List.fromList([ASCII.digit1.index, ASCII.digit2.index, ASCII.digit3.index, ASCII.digit4.index]);
        var res = Lexer.lexNumber(inp, 0, 3);
        expect(res.isRight, true);
        var r = res.right;
        expect(r.item1 is IntToken, true);
        expect((r.item1 as IntToken).val, 1234);
    });



    test("Full lexer test 1", () {
        var inpStr = """
a = b + c
""";

        var inp = Uint8List.fromList(inpStr.codeUnits);
        var res = Lexer.lexicallyAnalyze(inp);

        var expected = ListExpr([ListExpr([WordToken(Uint8List.fromList([ASCII.aLower.index])), OperatorToken([OperatorSymb.equals]),
                                           WordToken(Uint8List.fromList([ASCII.bLower.index])), OperatorToken([OperatorSymb.plus]),
                                           WordToken(Uint8List.fromList([ASCII.cLower.index]))],
                                            ExprLexicalType.statement)], ExprLexicalType.curlyBraces);
        expect(res.item2, null);
        expect(Expr.equal(res.item1, expected), true);
    });
}
