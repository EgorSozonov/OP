namespace O7;

public class ParseError {}

public class UnexpectedTokenError : ParseError {
    public string val;

    public UnexpectedTokenError(string val) {
        this.val = val;
    }

    public override string ToString() {
        return val;
    }
}


public class AssignmentError : ParseError {
    public string val;

    public AssignmentError(string val) {
        this.val = val;
    }

    public override string ToString() {
        return val;
    }
}
