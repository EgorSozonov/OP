class Token {}


class ListTokens extends Token {
    List<Token> val = [];
    ListTokens(this.val);
}

class IntToken extends Token {
    int val = 0;
    IntToken(this.val);
}

class BoolToken extends Token {
    bool val = false;
    BoolToken(this.val);
}
