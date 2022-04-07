package tech.sozonov.o7.parser.types;
import java.util.ArrayList;
import java.util.List;
import tech.sozonov.o7.lexer.types.OperatorSymb;
import tech.sozonov.o7.parser.types.CoreOperatorPackage.AssignmentType;
import tech.sozonov.o7.parser.types.CoreOperatorPackage.CoreOperator;
import tech.sozonov.o7.utils.Stack;
import tech.sozonov.o7.utils.Tuple;
import static tech.sozonov.o7.utils.ListUtils.*;


public class ASTUntyped {

public static class ASTUntypedBase {
    @Override
    public final String toString() {
        if (this instanceof ListStatements lsOuter) {

            var result = new StringBuilder();
            var backtrack = new Stack<Tuple<ListStatements, Integer>>();
            ListStatements curr = lsOuter;
            int i = 0;

            do {
                while (i < curr.val.size()) {
                    if (curr.val.get(i) instanceof ListStatements listElem) {
                        if (hasValues(listElem.val)) {
                            backtrack.push(new Tuple<ListStatements, Integer>(curr, i));
                            curr = listElem;
                            i = 0;
                            if (curr.sType == SubexprType.curlyBraces) {
                                result.append("{\n ");
                            } else if (curr.sType == SubexprType.dataInitializer) {
                                result.append("[");
                            } else if (curr.sType == SubexprType.parens){
                                result.append("(");
                            }
                        } else {
                            ++i;
                        }
                    } else {
                        result.append(curr.val.get(i).toString());
                        result.append(", ");
                        ++i;
                    }
                }
                if (backtrack.peek() != null) {
                    if (curr.sType == SubexprType.curlyBraces) {
                        result.append("}\n ");
                    } else if (curr.sType == SubexprType.dataInitializer) {
                        result.append("], ");
                    } else if (curr.sType == SubexprType.parens) {
                        result.append(") ");
                    } else {
                        result.append(";\n ");
                    }
                    var back = backtrack.pop();
                    curr = back.item0;
                    i = back.item1 + 1;
                }
            } while (backtrack.peek() != null || i < curr.val.size());

            return result.toString();
        } else if (this instanceof Ident x) {
            return "id " + x.name;
        } else if (this instanceof If x2) {
            return "If " + x2.val.size();
        } else if (this instanceof Assignment x3) {
            return x3.identifier + " = " + x3.rightSide;
        } else if (this instanceof IntLiteral x4) {
            return Long.toString(x4.val);
        } else if (this instanceof FloatLiteral x5) {
            return Double.toString(x5.val);
        } else if (this instanceof Reserved x6) {
            return x6.val.toString();
        } else if (this instanceof CoreOperatorAST x7) {
            return x7.val.toString();
        } else if (this instanceof BoolLiteral x8) {
            return "boolean " + Boolean.toString(x8.val);
        } else {
            return "Something else";
        }
    }
}

public final static class ASTList extends ASTUntypedBase {
    public ArrayList<ArrayList<ASTUntypedBase>> data;
    public int indList;
    public int ind;
    public ArrayList<ASTUntypedBase> curr;
    public SubexprType sType;

    public ASTList(SubexprType sType) {
        this.sType = sType;
        data = new ArrayList<>();
        curr = new ArrayList<>();

        data.add(curr);
        indList = 0;
        ind = 0;
    }

    public void add(ASTUntypedBase newItem) {
        curr.add(newItem);
        ind++;
    }

    public void addToNextList(ASTUntypedBase newItem) {
        curr = new ArrayList<>();
        curr.add(newItem);
        data.add(curr);
        ++indList;
        ind = 1;
    }

    public void startNewList() {
        curr = new ArrayList<>();
        data.add(curr);
        ++ind;
    }
}

public final static class ListStatements extends ASTUntypedBase {
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

public final static class Reserved extends ASTUntypedBase {
    public CoreFormType val;

    public Reserved(CoreFormType val) {
        this.val = val;
    }
}

public final static class If extends ASTUntypedBase {
    public List<IfClause> val;
    public If(List<IfClause> val) {
        this.val = val;
    }
}

public final static class IfClause extends ASTUntypedBase {
    public ASTUntypedBase testClause;
    public ASTUntypedBase resultClause;
    public IfClause(ASTUntypedBase testClause, ASTUntypedBase resultClause) {
        this.testClause = testClause;
        this.resultClause = resultClause;
    }
}

public final static class Assignment extends ASTUntypedBase {
    public Ident identifier;
    public AssignmentType aType;
    public ASTUntypedBase rightSide;
    public Assignment(Ident identifier, AssignmentType aType, ASTUntypedBase rightSide) {
        this.identifier = identifier;
        this.aType = aType;
        this.rightSide = rightSide;
    }
}



public final static class While extends ASTUntypedBase {
    public ASTUntyped testClause;
    public ListStatements body;
    public While(ASTUntyped testClause, ListStatements body) {
        this.testClause = testClause;
        this.body = body;
    }
}

// public final static class Statement extends ASTUntypedBase {
//     public List<ASTUntypedBase> val;
//     public Statement(List<ASTUntypedBase> val) {
//         this.val = val;
//     }

//     public Statement() {
//         this.val = new ArrayList<ASTUntypedBase>();
//     }
// }

public final static class Ident extends ASTUntypedBase {
    public String name;

    public Ident(String name) {
        this.name = name;
    }
}

public final static class IntLiteral extends ASTUntypedBase {
    public long val;
    public IntLiteral(long val) {
        this.val = val;
    }
}

public final static class FloatLiteral extends ASTUntypedBase {
    public double val;
    public FloatLiteral(double val) {
        this.val = val;
    }
}

public final static class BoolLiteral extends ASTUntypedBase {
    public boolean val;
    public BoolLiteral(boolean val) {
        this.val = val;
    }
}

public final static class StringLiteral extends ASTUntypedBase {
    public String val;
    public StringLiteral(String val) {
        this.val = val;
    }
}

public final static class CoreOperatorAST extends ASTUntypedBase {
    public CoreOperator val;
    public CoreOperatorAST(CoreOperator val) {
        this.val = val;
    }
}

public final static class OperatorAST extends ASTUntypedBase {
    public List<OperatorSymb> val;
    public OperatorAST(List<OperatorSymb> val) {
        this.val = val;
    }
}

}
