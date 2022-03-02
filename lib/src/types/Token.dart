class Token {}

class ListTokens extends Token {
    List<Token> val = [];
}

class IntToken extends Token {
    int val = 0;
}

class BoolToken extends Token {
    bool val = false;
}
