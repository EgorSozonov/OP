package tech.sozonov.o7.parser.types;

public class SyntaxContexts {

public static enum SyntaxContext {
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
    matchh,
    whilee,
    doo,
    forr,
    foreachh,
    structt,
    sumTypee,
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
    dataInitializer,
    curlyBraces,
    typeDeclaration,
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
    assignmentMut,
    plusMut,
    minusMut,
    timesMut,
    divideMut,

    arrow,
    fatArrow,
    typeDecl,
    colon,
    pipe,
}


}
