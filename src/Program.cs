namespace O7;
using System;
using System.Text;
using static ByteArrayUtils;

class Program {
    static void Main(string[] args) {
        var inp = @"
a {
    if

    true

    afterComment #cd comment here.#  += 1

    ([123 _321] + [456 111])
}
d; ";

        var innp = @"
            a b
            #cd.# he  he  he
            {
                12 + y
                x foo [12.34 fa]
            }
        ";

        var res = Lexer.lexicallyAnalyze(Encoding.ASCII.GetBytes(inp));
        // var eqRes = Expr.equal(expected, expected2);
        // print("eqRes = $eqRes");
        // return;


        l("");
        if (res.Item2 != null) {
            l("Lexer error");
            l(res.Item2.ToString());
            l(res.Item1.ToString());
        } else {
            l("Lexer successful");
            var lexed = res.Item1;
            l(lexed.ToString());
            l("");
            var parseRes = PreParser.parse(lexed);
            if (parseRes.Item2 != null) {
                l("Parser error");
                l(parseRes.Item2.ToString());
                l(parseRes.Item1.ToString());
            } else {
                l("Parser successful");
                l(parseRes.Item1.ToString());
            }
            // print("Expected:");
            // print(expected);
        }
    }
}
