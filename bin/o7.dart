import 'dart:typed_data';
import "package:o7/o7.dart" as o7;
import "dart:io";
import 'package:o7/src/lexer/Lexer.dart';
import 'package:o7/src/types/ASCII.dart';
import 'package:o7/src/types/Token.dart';

void main(List<String> arguments) async {
    print("Hello world: ${o7.calculate()}!");
    // var file = File.fromUri(Uri.parse("~/Toys/tmp.txt"));
    // var bytes = await file.readAsBytes();
    // var resLex = Lexer.lexicallyAnalyze(bytes);
    var inp = Uint8List.fromList([ASCII.F_LOWER.index, ASCII.A_LOWER.index, ASCII.L_LOWER.index, ASCII.S_LOWER.index, ASCII.E_LOWER.index]);
    var res = Lexer.lexBool(inp, 0, 3);
    if (res.isRight && res.right.item1 is BoolToken) {
        print(res.right.item1.val);
    }
    print(res.right.item1.val);
}
