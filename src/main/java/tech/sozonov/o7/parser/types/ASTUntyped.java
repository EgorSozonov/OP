package tech.sozonov.o7.types;

import static tech.sozonov.o7.utils.ByteArrayUtils.*;
import java.util.List;

public class ASTUntyped {

static class ASTUntypedBase {
    @Override
    public final String toString() {
        if (this instanceof ListStatements lsOuter) {

            var result = new StringBuilder();
            var backtrack = new Stack<Tuple<ListStatements, Integer>>();
            ListStatements curr = lsOuter;
            var i = 0;

            do {
                while (i < curr.val.Count) {
                    if (curr.val[i] instanceof ListStatements listElem) {
                        if (listElem.val.isNotEmpty()) {
                            backtrack.push(new Tuple<ListStatements, Integer>(curr, i));
                            curr = listElem;
                            i = 0;
                            if (curr.sType == SubexprType.curlyBraces) {
                                result.Append("{\n ");
                            } else if (curr.sType == SubexprType.dataInitializer) {
                                result.Append("[");
                            } else if (curr.sType == SubexprType.parens){
                                result.Append("(");
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
                        result.Append("}\n ");
                    } else if (curr.sType == SubexprType.dataInitializer) {
                        result.Append("], ");
                    } else if (curr.sType == SubexprType.parens) {
                        result.Append(") ");
                    } else {
                        result.Append(";\n ");
                    }
                    var back = backtrack.pop();
                    curr = back.Item1;
                    i = back.Item2 + 1;
                }
            } while (backtrack.peek() != null || i < curr.val.Count);

            return result.ToString();
        } else if (this instanceof Ident x) {
            return "id " + x.name;
        } else if (this instanceof If x2) {
            return $"If {x2.val.Count}";
        } else if (this instanceof Assignment x3) {
            return $"{x3.identifier} = {x3.rightSide}";
        } else if (this instanceof IntLiteral x4) {
            return x4.val.ToString();
        } else if (this instanceof FloatLiteral x5) {
            return x5.val.ToString();
        } else if (this instanceof Reserved x6) {
            return x6.val.ToString();
        } else if (this instanceof CoreOperatorAST x7) {
            return x7.val.ToString();
        } else if (this instanceof BoolLiteral x8) {
            return "boolean " + x8.val.toString();
        } else {
            return "Something else";
        }
    }
}

public class ListStatements extends ASTUntypedBase {
    public List<ASTUntypedBase> val;
    public SubexprType sType;

    public ListStatements() {
        this.val = new ArrayList<ASTUntypedBase>();
        this.sType = SubexprType.statement;
    }

    public ListStatements(SubexprType sType) {
        this.val = new ArrayList<ASTUntypedBase>();
        this.sType = sType;
    }
}

public final class Statement extends ASTUntypedBase {
    public List<ASTUntypedBase> val;
    public Statement(List<ASTUntypedBase> val) {
        this.val = val;
    }

    public Statement() {
        this.val = new ArrayList<ASTUntypedBase>();
    }
}

public final class Ident extends ASTUntypedBase {
    public string name;

    public Ident(string name) {
        this.name = name;
    }
}

public final class Reserved extends ASTUntypedBase {
    public ReservedType val;

    public Reserved(ReservedType val) {
        this.val = val;
    }
}

public final class DataInitializer extends ListStatements {
    public DataInitializer() {
        this.val = new ArrayList<ASTUntypedBase>();
        this.sType = SubexprType.dataInitializer;
    }
}

public final class If extends ASTUntypedBase {
    public List<IfClause> val;
    public If(List<IfClause> val) {
        this.val = val;
    }
}

public final class IfClause extends ASTUntypedBase {
    public ASTUntypedBase testClause;
    public ASTUntypedBase resultClause;
    public IfClause(ASTUntypedBase testClause, ASTUntypedBase resultClause) {
        this.testClause = testClause;
        this.resultClause = resultClause;
    }
}

public final class Assignment extends ASTUntypedBase {
    public Ident identifier;
    public AssignmentType aType;
    public ASTUntypedBase rightSide;
    public Assignment(Ident identifier, AssignmentType aType, ASTUntypedBase rightSide) {
        this.identifier = identifier;
        this.aType = aType;
        this.rightSide = rightSide;
    }
}

public final class IntLiteral extends ASTUntypedBase {
    public int val;
    public IntLiteral(int val) {
        this.val = val;
    }
}

public final class FloatLiteral extends ASTUntypedBase {
    public double val;
    public FloatLiteral(double val) {
        this.val = val;
    }
}

public final class BoolLiteral extends ASTUntypedBase {
    public boolean val;
    public BoolLiteral(boolean val) {
        this.val = val;
    }
}

public final class StringLiteral extends ASTUntypedBase {
    public String val;
    public StringLiteral(String val) {
        this.val = val;
    }
}

public final class While extends ASTUntypedBase {
    public Statement testClause;
    public List<Statement> body;
    public While(Statement testClause, List<Statement> body) {
        this.testClause = testClause;
        this.body = body;
    }
}

public final class CoreOperatorAST extends ASTUntypedBase {
    public CoreOperator val;
    public CoreOperatorAST(CoreOperator val) {
        this.val = val;
    }
}

public final class OperatorAST extends ASTUntypedBase {
    public List<OperatorSymb> val;
    public OperatorAST(List<OperatorSymb> val) {
        this.val = val;
    }
}

}
