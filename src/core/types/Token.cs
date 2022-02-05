namespace O7;

public class Token {}

public class Comment : Token {
    public string content;
}

public class LiteralStr : Token {
    public string content;
}

public class Reserved : Token {
    public ReservedWord content;
}

public class Ident : Token {
    public byte[] content;
}

