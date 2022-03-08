import "package:o7/src/parser/types/ReservedType.dart";


class ASTUntyped {}

class Ident extends ASTUntyped {
    String name;
    Ident(this.name);
}

class Reserved extends ASTUntyped {
    ReservedType val;
    Reserved(this.val);
}

class If extends ASTUntyped {
    List<IfClause> val;
    If(this.val);
}

class IfClause extends ASTUntyped {
    ASTUntyped testClause;
    ASTUntyped resultClause;
    IfClause(this.testClause, this.resultClause);
}

class VarDefinition extends ASTUntyped {
    Ident identifier;
    ASTUntyped rightSide;
    VarDefinition(this.identifier, this.rightSide);
}

class MutableDefinition extends ASTUntyped {
    Ident identifier;
    ASTUntyped rightSide;
    MutableDefinition(this.identifier, this.rightSide);
}

class MutableAssignment extends ASTUntyped {
    Ident identifier;
    ASTUntyped rightSide;
    MutableAssignment(this.identifier, this.rightSide);
}

class IntLiteral extends ASTUntyped {
    int val;
    IntLiteral(this.val);
}

class FloatLiteral extends ASTUntyped {
    double val;
    FloatLiteral(this.val);
}

class BoolLiteral extends ASTUntyped {
    bool val;
    BoolLiteral(this.val);
}

class StringLiteral extends ASTUntyped {
    String val;
    StringLiteral(this.val);
}

class While extends ASTUntyped {
    ASTUntyped testClause;
    List<ASTUntyped> body;
    While(this.testClause, this.body);
}
