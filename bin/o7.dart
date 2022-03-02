import "package:o7/o7.dart" as o7;
import "dart:io";

import 'package:o7/src/lexer/Lexer.dart';

void main(List<String> arguments) async {
    print("Hello world: ${o7.calculate()}!");
    var file = File.fromUri(Uri.parse("~/Toys/tmp.txt"));
    var bytes = await file.readAsBytes();
    var resLex = Lexer.lexicallyAnalyze(bytes);
    print(resLex);
}
