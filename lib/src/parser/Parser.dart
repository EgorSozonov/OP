import "package:o7/src/parser/types/ASTUntyped.dart";
import "package:o7/src/parser/types/ParseError.dart";
import "package:tuple/tuple.dart";
import "../lexer/types/Expr.dart";


typedef ParseResult = Tuple2<ASTUntyped, ParseError?>;

class Parser {
    static ParseResult parse(Expr inp) {

    }
}
