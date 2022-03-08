class ParseError {}


class UnexpectedTokenError extends ParseError {
    String val;
    UnexpectedTokenError(this.val);

    @override
    String toString() {
        return val;
    }
}
