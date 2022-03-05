class Token {}


class ListTokens extends Token {
    List<Token> val = [];
    ListTokens(this.val);

    @override
    bool operator ==(Object o) => (o is ListTokens) ? (val == o.val) : false;

    @override
    int get hashCode => val.hashCode;
}

class IntToken extends Token {
    int val = 0;
    IntToken(this.val);

    @override
    bool operator ==(Object o) => (o is IntToken) ? (val == o.val) : false;

    @override
    int get hashCode => val.hashCode;

    @override
    String toString() {
        return "Int $val";
    }
}

class BoolToken extends Token {
    bool val = false;
    BoolToken(this.val);

    @override
    bool operator ==(Object o) => (o is BoolToken) ? (val == o.val) : false;

    @override
    int get hashCode => val.hashCode;

    @override
    String toString() {
        return "Bool $val";
    }
}
