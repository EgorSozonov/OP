package tech.sozonov.o7;
import tech.sozonov.o7.lexer.Lexer;
import tech.sozonov.o7.parser.Parser;
import java.nio.charset.StandardCharsets;
import lombok.val;
import static tech.sozonov.o7.utils.ArrayUtils.*;


class Program {
public static void main(String[] args) {

    val innp = """
    if x > 5 && (x < 10) -> 0
           x > 1 -> 10
           else  -> 20
    """;

    val res = Lexer.lexicallyAnalyze(innp.getBytes(StandardCharsets.UTF_8));
    if (res.i1 != null) {
        l("Lexer error");
        l(res.i1.toString());
        l(res.i0.toString());
    } else {
        l("Lexer successful");
        var lexed = res.i0;
        //l(lexed.toString());
        l("");

        var parseRes = Parser.parse(lexed);
        if (parseRes.i1 != null) {
            l("Parser error");
            l(parseRes.i1.toString());
            l(parseRes.i0.toString());
        } else {
            l("Parser successful");
            l(parseRes.i0.toString());
        }
    }
}

}
