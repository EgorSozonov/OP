package tech.sozonov.o7.parser.types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import tech.sozonov.o7.lexer.types.ExprLexicalType;
import tech.sozonov.o7.lexer.types.OperatorSymb;
import tech.sozonov.o7.parser.types.ParseContexts.CoreOperator;
import tech.sozonov.o7.parser.types.ParseContexts.ParseContext;
import tech.sozonov.o7.parser.types.ParseError.ParseErrorBase;
import tech.sozonov.o7.utils.Stack;
import tech.sozonov.o7.utils.Tuple;
import static tech.sozonov.o7.utils.ListUtils.*;


public class ASTUntyped {

public static class ASTUntypedBase {
    @Override
    public final String toString() {
        if (this instanceof ASTList lsOuter) {
            return "";

            // var result = new StringBuilder();
            // var backtrack = new Stack<Tuple<ListStatements, Integer>>();
            // ASTList curr = lsOuter;
            // int i = 0;

            // do {
            //     while (i < curr.val.size()) {
            //         if (curr.val.get(i) instanceof ListStatements listElem) {
            //             if (hasValues(listElem.val)) {
            //                 backtrack.push(new Tuple<ListStatements, Integer>(curr, i));
            //                 curr = listElem;
            //                 i = 0;
            //                 if (curr.ctx == ParseContext.curlyBraces) {
            //                     result.append("{\n ");
            //                 } else if (curr.sType == SubexprType.dataInitializer) {
            //                     result.append("[");
            //                 } else if (curr.sType == SubexprType.parens){
            //                     result.append("(");
            //                 }
            //             } else {
            //                 ++i;
            //             }
            //         } else {
            //             result.append(curr.val.get(i).toString());
            //             result.append(", ");
            //             ++i;
            //         }
            //     }
            //     if (backtrack.peek() != null) {
            //         if (curr.sType == SubexprType.curlyBraces) {
            //             result.append("}\n ");
            //         } else if (curr.sType == SubexprType.dataInitializer) {
            //             result.append("], ");
            //         } else if (curr.sType == SubexprType.parens) {
            //             result.append(") ");
            //         } else {
            //             result.append(";\n ");
            //         }
            //         var back = backtrack.pop();
            //         curr = back.item0;
            //         i = back.item1 + 1;
            //     }
            // } while (backtrack.peek() != null || i < curr.val.size());

            // return result.toString();
        } else if (this instanceof Ident x) {
            return "id " + x.name;
        } else if (this instanceof ASTList x3) {
            return "AST List";
        } else if (this instanceof IntLiteral x4) {
            return Long.toString(x4.val);
        } else if (this instanceof FloatLiteral x5) {
            return Double.toString(x5.val);
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
    public ParseContext ctx;

    public ASTList(ParseContext ctx) {
        this.ctx = ctx;
        data = new ArrayList<>();
        curr = new ArrayList<>();

        data.add(curr);
        indList = 0;
        ind = 0;
    }

    // public ASTList(ExprLexicalType listType) {
    //     if (listType == ExprLexicalType.curlyBraces) {
    //         this.ctx = ParseContext.curlyBraces;
    //     } else if (listType == ExprLexicalType.dataInitializer) {
    //         this.ctx = ParseContext.dataInitializer;
    //     } else if (listType == ExprLexicalType.parens) {
    //         this.ctx = ParseContext.parens;
    //     } else {
    //         this.ctx = ParseContext.statement;
    //     }
    // }

    /**
     * Try to add a new item to the current AST node. Returns a syntax error if unsuccessful.
     */
    public Optional<ParseErrorBase> add(ASTUntypedBase newItem) {
        // TODO adding of stuff in accordance to parse context
        // val mbPunctuation = mbParsePunctutation(сurrToken);
        // if (mbPunctuation.isEmpty()) {

        // }
        curr.add(newItem);
        ind++;
        return Optional.empty();
    }

    /**
     * A list of tokens has ended, and we need to know whether this current AST is saturated.
     * Returns true if saturated, false if not yet saturated, and None if oversaturated (which shouldn't ever happen).
     *
     * Saturation is determined by the parsing context and number of ingested lists:
     * if/1
     * while/2
     * for/4
     * foreach/2
     * match/2
     */
    public Optional<Boolean> listHasEnded() {
        int saturationLength = 0;
        if (ctx == ParseContext.iff) {
            saturationLength = 1;
        } else if (ctx == ParseContext.whilee || ctx == ParseContext.foreachh || ctx == ParseContext.matchh) {
            saturationLength = 2;
        } else if (ctx == ParseContext.forr) {
            saturationLength = 4;
        } else {
            saturationLength = 1;
        }
        if (data.size() < saturationLength) return Optional.of(false);
        if (data.size() == saturationLength) return Optional.of(true);
        return Optional.empty();
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
