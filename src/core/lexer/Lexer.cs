namespace O7;

public class Lexer : ILexer {
    public List<Token> lexicallyAnalyze(byte[] input, out string errMsg) {
        var result = new List<Token>();
        errMsg = "";        
        if (input == null || input.Length < 2) return result;

        int lineNumber = 1;
        int len = input.Length - 1;

        int i = 0;
        while (i < len) {
            if (input[i] >= 128) {
                errMsg = $"Erroneous byte at {i}, expected to be within [0; 127] range";
                return result;
            }
            var character = (Ascii)input[i];
            if (character == Ascii.QUOTATION_MARK_DOUBLE) {
                // lex until unescaped double quote
                int j = findEndOfStringLiteral(i, input);
                i = j;
            } else if (character == Ascii.MINUS && (Ascii)input[i + 1] == Ascii.MINUS) {
                // lex until newline or ".-"
                // code code -- comment .- blabla = x + -- another inline comment .- (zzz)
                int j = findEndOfComment(i, input);
                i = j;
            } else {
                // lex until space or newline
                int j = findEndOfCodeToken(i, input);
                i = j;
            }
        }


        return result;
    }

    public int findEndOfStringLiteral(int start, byte[] input) {
        int result = start + 1;
        int len = input.Length - 1;
        while (result < len) {
            if (input[result] < 127) {
                var character = (Ascii)input[result];
                if (character == Ascii.SLASH_BACKWARD && input[result + 1] < 127 && (Ascii)input[result + 1] == Ascii.QUOTATION_MARK_DOUBLE) {
                    result += 2;
                    continue;
                } else if (character == Ascii.QUOTATION_MARK_DOUBLE) {
                    return result;
                }
            }
            ++result;
        }
        return result;
    }

    public int findEndOfComment(int start, byte[] input) {
        int result = start + 1;
        return result;
    }

    public int findEndOfCodeToken(int start, byte[] input) {
        int result = start + 1;
        return result;
    }
}