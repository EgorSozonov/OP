import 'dart:typed_data';
import "package:o7/o7.dart" as o7;
import "dart:io";
import 'package:o7/src/lexer/Lexer.dart';
import 'package:o7/src/types/ASCII.dart';
import 'package:o7/src/types/LexError.dart';
import 'package:o7/src/types/Token.dart';

void main(List<String> arguments) async {
    // print("Hello world: ${o7.calculate()}!");
    // var file = File.fromUri(Uri.parse("~/Toys/tmp.txt"));
    // var bytes = await file.readAsBytes();
    // var resLex = Lexer.lexicallyAnalyze(bytes);
    //var inp = Uint8List.fromList([ASCII.F_LOWER.index, ASCII.A_LOWER.index, ASCII.L_LOWER.index, ASCII.S_LOWER.index, ASCII.E_LOWER.index]);
    //var inp = Uint8List.fromList([ASCII.T_LOWER.index, ASCII.R_LOWER.index, ASCII.U_LOWER.index, ASCII.E_LOWER.index]);
    // var inp = Uint8List.fromList([ASCII.DIGIT_1.index, ASCII.DIGIT_2.index, ASCII.DIGIT_3.index, ASCII.DIGIT_4.index]);
    // var res = Lexer.lexInt(inp, 0, 2);
    print("lexed");
    var inp = Uint8List.fromList([ASCII.MINUS.index, ASCII.DIGIT_1.index, ASCII.DIGIT_2.index, ASCII.DIGIT_3.index, ASCII.DIGIT_4.index, ASCII.SPACE.index, ASCII.DIGIT_2.index]);
    var res = Lexer.lexInt(inp, 0, 3);
    if (res.isRight) {
        print("Found a token");
        var token = res.right.item1;
        if (token is BoolToken) {
            print(token.val);
        } else if (token is IntToken) {
            print(token.val);
        }
    } else if (res.isLeft) {
        print("Some kind of error");
        var err = res.left;
        if (err is IntError) {
            print(err.val);
        } else if (err is BoolError) {
            print(err.val);
        } else if (err is EndOfInputError) {
            print("End of input error");
        }
    }
}
