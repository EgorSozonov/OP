namespace O7;
using System;
using System.Text;
using static ByteArrayUtils;

class Program {
    static void Main(string[] args) {

        var inpStr = @"
a = b + c
";

        var inp = @"a {
    b
    c
    (123 + 456 111)
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
            l(res.Item1.ToString());
            // print("Expected:");
            // print(expected);
        }
    }
}
