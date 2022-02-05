namespace O7;

public class Lexer : ILexer {
    public List<Token> lexicallyAnalyze(byte[] input) {
        var result = new List<Token>();
        if (input == null || input.Length < 2) return result;
        int len = input.Length - 1;
        for (int i = 0; i < len; ++i) {
            var character = (Ascii)input[i];
            if (character == Ascii.QUOTATION_MARK_DOUBLE) {
                // lex until unescaped double quote
            } else if (character == Ascii.MINUS && (Ascii)input[i + 1] == Ascii.MINUS) {
                // lex until newline or ".-"
                // code code -- comment .- blabla = x + -- another inline comment .- (zzz)
            } else {
                // lex until space or newline
            }
        }


        return result;
    }
}