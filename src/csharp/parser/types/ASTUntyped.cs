namespace O7;
using System;
using System.Collections.Generic;
using System.Text;


public class ASTUntyped {
    public sealed override string ToString() {
        if (this is ListStatements lsOuter) {
            var result = new StringBuilder();
            var backtrack = new Stack<Tuple<ListStatements, int>>();
            var curr = lsOuter;
            var i = 0;

            do {
                while (i < curr.val.Count) {
                    if (curr.val[i] is ListStatements listElem) {
                        if (listElem.val.isNotEmpty()) {
                            backtrack.push(new Tuple<ListStatements, int>(curr, i));
                            curr = listElem;
                            i = 0;
                            if (curr.sType == SubexprType.curlyBraces) {
                                result.Append("{ ");
                            } else if (curr.sType == SubexprType.dataInitializer) {
                                result.Append("[ ");
                            } else if (curr.sType == SubexprType.parens){
                                result.Append("( ");
                            }
                        }
                    } else {
                        result.Append(curr.val[i].ToString());
                        result.Append(", ");
                        ++i;
                    }
                }
                if (backtrack.peek() != null) {
                    if (curr.sType == SubexprType.curlyBraces) {
                        result.Append(" }\n ");
                    } else if (curr.sType == SubexprType.dataInitializer) {
                        result.Append(" ], ");
                    } else if (curr.sType == SubexprType.parens) {
                        result.Append(" ) ");
                    } else {
                        result.Append(";\n ");
                    }
                    var back = backtrack.pop();
                    curr = back.Item1;
                    i = back.Item2 + 1;
                }
            } while (backtrack.peek() != null || i < curr.val.Count);

            return result.ToString();
        } else if (this is Ident x) {
            return x.name;
        } else if (this is If x2) {
            return $"If {x2.val.Count}";
        } else if (this is VarDefinition x3) {
            return $"{x3.identifier} = {x3.rightSide}";
        } else if (this is IntLiteral x4) {
            return x4.val.ToString();
        } else if (this is Reserved x5) {
            return x5.val.ToString();
        } else {
            return "Something else";
        }
    }
}

public class ListStatements : ASTUntyped {
    public List<ASTUntyped> val;
    public SubexprType sType;

    public ListStatements() {
        this.val = new List<ASTUntyped>();
        this.sType = SubexprType.list;
    }

    public ListStatements(SubexprType sType) {
        this.val = new List<ASTUntyped>();
        this.sType = sType;
    }
}

public sealed class Statement : ASTUntyped {
    public List<ASTUntyped> val;
    public Statement(List<ASTUntyped> val) {
        this.val = val;
    }

    public Statement() {
        this.val = new List<ASTUntyped>();
    }
}

public sealed class Ident : ASTUntyped {
    public string name;

    public Ident(string name) {
        this.name = name;
    }
}

public sealed class Reserved : ASTUntyped {
    public ReservedType val;

    public Reserved(ReservedType val) {
        this.val = val;
    }
}

public sealed class DataInitializer : ListStatements {
    public DataInitializer() {
        this.val = new List<ASTUntyped>();
        this.sType = SubexprType.dataInitializer;
    }
}

public sealed class If : ASTUntyped {
    public List<IfClause> val;
    public If(List<IfClause> val) {
        this.val = val;
    }
}

public sealed class IfClause : ASTUntyped {
    public ASTUntyped testClause;
    public ASTUntyped resultClause;
    public IfClause(ASTUntyped testClause, ASTUntyped resultClause) {
        this.testClause = testClause;
        this.resultClause = resultClause;
    }
}

public sealed class VarDefinition : ASTUntyped {
    public Ident identifier;
    public ASTUntyped rightSide;
    public VarDefinition(Ident identifier, ASTUntyped rightSide) {
        this.identifier = identifier;
        this.rightSide = rightSide;
    }
}

public sealed class MutableDefinition : ASTUntyped {
    public Ident identifier;
    public Statement rightSide;
    public MutableDefinition(Ident identifier, Statement rightSide) {
        this.identifier = identifier;
        this.rightSide = rightSide;
    }
}

public sealed class MutableAssignment : ASTUntyped {
    public Ident identifier;
    public Statement rightSide;
    public MutableAssignment(Ident identifier, Statement rightSide) {
        this.identifier = identifier;
        this.rightSide = rightSide;
    }
}

public sealed class IntLiteral : ASTUntyped {
    public int val;
    public IntLiteral(int val) {
        this.val = val;
    }
}

public sealed class FloatLiteral : ASTUntyped {
    public double val;
    public FloatLiteral(double val) {
        this.val = val;
    }
}

public sealed class BoolLiteral : ASTUntyped {
    public bool val;
    public BoolLiteral(bool val) {
        this.val = val;
    }
}

public sealed class StringLiteral : ASTUntyped {
    public string val;
    public StringLiteral(string val) {
        this.val = val;
    }
}

public sealed class While : ASTUntyped {
    public Statement testClause;
    public List<Statement> body;
    public While(Statement testClause, List<Statement> body) {
        this.testClause = testClause;
        this.body = body;
    }
}

public sealed class CoreOperatorAST : ASTUntyped {
    public CoreOperator val;
    public CoreOperatorAST(CoreOperator val) {
        this.val = val;
    }
}

public sealed class OperatorAST : ASTUntyped {
    public List<OperatorSymb> val;
    public OperatorAST(List<OperatorSymb> val) {
        this.val = val;
    }
}
