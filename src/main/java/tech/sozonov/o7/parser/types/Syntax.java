package tech.sozonov.o7.parser.types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import lombok.val;
import tech.sozonov.o7.parser.types.SyntaxContexts.*;
import tech.sozonov.o7.lexer.types.OperatorSymb;
import java.util.List;
import tech.sozonov.o7.utils.Tuple;


public final class Syntax {

public final Map<String, SyntaxContext> contexts;
public final Map<String, ReservedWord> reservedWords;
public final List<Tuple<List<OperatorSymb>, CoreOperator>> coreOperators;
public final List<Tuple<List<OperatorSymb>, CoreOperator>> functionOperators;
public static final List<OperatorSymb> arrow = Arrays.asList(OperatorSymb.minus, OperatorSymb.gt);
public static final List<OperatorSymb> pipe = Arrays.asList(OperatorSymb.pipe);

public Syntax() {
    contexts = getSyntaxContexts();
    reservedWords = getReservedWords();
    coreOperators = getCoreOperatorList();
    functionOperators = getFunctionOperatorList();
}

static Map<String, SyntaxContext> getSyntaxContexts() {
    val result = new HashMap<String, SyntaxContext>();
    val coreContexts = EnumSet.complementOf(EnumSet.range(SyntaxContext.ifUnboundedd, SyntaxContext.sumTypeUnboundedd));
    coreContexts.forEach(enumValue -> {
        val nm = enumValue.toString();
        result.put(nm.substring(0, nm.length() - 1), enumValue);
    });

    return result;
}


static Map<String, ReservedWord> getReservedWords() {
    val result = new HashMap<String, ReservedWord>();
    val reservedWords = EnumSet.allOf(ReservedWord.class);
    reservedWords.forEach(enumValue -> {
        val nm = enumValue.toString();
        result.put(nm.substring(0, nm.length() - 1), enumValue);
    });

    return result;
}


static List<Tuple<List<OperatorSymb>, CoreOperator>> getFunctionOperatorList() {
    var result = new ArrayList<Tuple<List<OperatorSymb>, CoreOperator>>();
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.plus),                              CoreOperator.plus));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.minus),                             CoreOperator.minus));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.asterisk),                          CoreOperator.times));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.slash),                             CoreOperator.divideBy));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.ampersand, OperatorSymb.ampersand), CoreOperator.and));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.pipe, OperatorSymb.pipe),           CoreOperator.or));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.exclamation),                       CoreOperator.not));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.ampersand),                         CoreOperator.bitwiseAnd));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.pipe),                              CoreOperator.bitwiseOr));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.tilde, OperatorSymb.ampersand),     CoreOperator.bitwiseNot));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.caret),                             CoreOperator.bitwiseXor));
    return result;
}


static List<Tuple<List<OperatorSymb>, CoreOperator>> getCoreOperatorList() {
    var result = new ArrayList<Tuple<List<OperatorSymb>, CoreOperator>>();
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.equals),                            CoreOperator.defineImm));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.colon, OperatorSymb.equals),        CoreOperator.assignmentMut));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.plus, OperatorSymb.equals),         CoreOperator.plusMut));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.minus, OperatorSymb.equals),        CoreOperator.minusMut));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.asterisk, OperatorSymb.equals),     CoreOperator.timesMut));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.slash, OperatorSymb.equals),        CoreOperator.divideMut));
    result.add(new Tuple<>(Syntax.arrow,                                                  CoreOperator.arrow));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.equals, OperatorSymb.gt),           CoreOperator.fatArrow));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.colon, OperatorSymb.colon),         CoreOperator.typeDecl));
    result.add(new Tuple<>(Arrays.asList(OperatorSymb.colon),                             CoreOperator.colon));
    result.add(new Tuple<>(pipe,                                                         CoreOperator.pipe));

    return result;
}

}
