namespace O7;
using System;
using System.Text;

class Program {
    static void Main(string[] args) {
        Console.WriteLine("Hw");
        var inpStr = @"
a = b + c
";

        var inp = Encoding.ASCII.GetBytes("a { b\nc\n (123 + 456 111); } d; ");

        var innp = @"
            a b
            #cd.# he  he  he
            {
                12 + y
                x foo [12.34 fa]
            }
        ";

        var res = Lexer.lexicallyAnalyze(Encoding.ASCII.GetBytes(innp));
        // var eqRes = Expr.equal(expected, expected2);
        // print("eqRes = $eqRes");
        // return;



        if (res.Item2 != null) {
            Console.WriteLine("Lexer error");
            Console.WriteLine(res.Item2.ToString());
            Console.WriteLine(res.Item1);
        } else {
            Console.WriteLine("Lexer successful");
            Console.WriteLine(res.Item1);
            // print("Expected:");
            // print(expected);
        }
    }
}
