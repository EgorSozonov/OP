namespace O7;

interface ILexer {
    List<Token> lexicallyAnalyze(byte[] input);
}