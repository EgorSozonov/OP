namespace O7;
using System.Collections.Generic;

class ASTUntyped {}

class ListStatements : ASTUntyped {
    public List<Statement> val;
    public ListStatements(List<Statement> val) {
        this.val = val;
    }
}

class Statement : ASTUntyped {
    public List<ASTUntyped> val;
    public Statement(List<ASTUntyped> val) {
        this.val = val;
    }
}

class Ident : ASTUntyped {
    public string name;

    public Ident(string name) {
        this.name = name;
    }
}

class Reserved : ASTUntyped {
    public ReservedType val;

    public Reserved(ReservedType val) {
        this.val = val;
    }
}

class If : ASTUntyped {
    List<IfClause> val;
    public If(List<IfClause> val) {
        this.val = val;
    }
}

class IfClause : ASTUntyped {
    public ASTUntyped testClause;
    public ASTUntyped resultClause;
    public IfClause(ASTUntyped testClause, ASTUntyped resultClause) {
        this.testClause = testClause;
        this.resultClause = resultClause;
    }
}

class VarDefinition : ASTUntyped {
    public Ident identifier;
    public ASTUntyped rightSide;
    public VarDefinition(Ident identifier, ASTUntyped rightSide) {
        this.identifier = identifier;
        this.rightSide = rightSide;
    }
}

class MutableDefinition : ASTUntyped {
    public Ident identifier;
    public Statement rightSide;
    public MutableDefinition(Ident identifier, Statement rightSide) {
        this.identifier = identifier;
        this.rightSide = rightSide;
    }
}

class MutableAssignment : ASTUntyped {
    public Ident identifier;
    public Statement rightSide;
    public MutableAssignment(Ident identifier, Statement rightSide) {
        this.identifier = identifier;
        this.rightSide = rightSide;
    }
}

class IntLiteral : ASTUntyped {
    public int val;
    public IntLiteral(int val) {
        this.val = val;
    }
}

class FloatLiteral : ASTUntyped {
    public double val;
    public FloatLiteral(double val) {
        this.val = val;
    }
}

class BoolLiteral : ASTUntyped {
    public bool val;
    public BoolLiteral(bool val) {
        this.val = val;
    }
}

class StringLiteral : ASTUntyped {
    public string val;
    public StringLiteral(string val) {
        this.val = val;
    }
}

class While : ASTUntyped {
    public Statement testClause;
    public List<Statement> body;
    public While(Statement testClause, List<Statement> body) {
        this.testClause = testClause;
        this.body = body;
    }
}

class CoreOperatorAST : ASTUntyped {
    public CoreOperator val;
    public CoreOperatorAST(CoreOperator val) {
        this.val = val;
    }
}

class OperatorAST : ASTUntyped {
    public List<OperatorSymb> val;
    public OperatorAST(List<OperatorSymb> val) {
        this.val = val;
    }
}
