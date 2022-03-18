namespace O7;

class ParseError {}

class UnexpectedTokenError : ParseError {
    public string val;

    public UnexpectedTokenError(string val) {
        this.val = val;
    }

    public override string ToString() {
        return val;
    }
}
