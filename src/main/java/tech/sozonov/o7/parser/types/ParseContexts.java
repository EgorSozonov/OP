package tech.sozonov.o7.parser.types;

public class ParseContexts {

public static enum ParseContext {
    funcall,

    // Assignments and definitions
    assignImmutable,
    assignMutable,
    assignMutablePlus,
    assignMutableMinus,
    assignMutableTimes,
    assignMutableDiv,

    // Core syntax forms
    iff,
    elsee,
    whilee,
    doo,
    forr,
    foreachh,
    asz,
    varr,
    mutt,
    matchh,
    privatee,
    exportt,
    importt,
    hidingg,
    modulee,
    typee,
    aliass,
    interfacee,
    impll,
    returnn,
    breakk,
    continuee,
    gotoo,
    asyncc,
    awaitt,
    yieldd,
    assertt,
    tryy,
    catchh,
    finallyy,
    nodestructt,
    macroo,

    // Lists of stuff
    statement,
    dataInitializer,
    curlyBraces,
    parens,
}

public enum CoreOperator {
    plus,
    minus,
    times,
    divideBy,
    and,
    or,
    not,
    bitwiseAnd,
    bitwiseOr,
    bitwiseNot,
    bitwiseXor,
    defineImm,
    defineMut,
    assignmentMut,
    plusMut,
    minusMut,
    timesMut,
    divideMut,
}


}
