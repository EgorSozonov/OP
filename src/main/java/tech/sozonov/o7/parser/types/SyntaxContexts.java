package tech.sozonov.o7.parser.types;

public class SyntaxContexts {

/**
 * Core forms are divided into bounded and unbounded ones in terms of their ingestion of statements.
 * The entries in this enum should not be re-ordered, for everything after "assignMutableDiv" is considered
 * a keyword for a core syntax form.
 * Bounded ones ingest a fixed number of statements/parens:
 *
 * while/2
 * for/4
 * foreach/2
 * do/1 while/1
 * try/1 catch/1 finally/1
 * struct
 *
 * Unbounded ones have an option to ingest a single curlyBraces/parens, but otherwise ingest statements
 * while they satisfy a condition (contain an arrow etc):
 *
 * if
 * match
 * sumType
 */
public static enum SyntaxContext {
    funcall,

    // Lists of stuff
    dataInitializer,
    curlyBraces,
    typeDeclaration,

    // unbounded syntax forms (i.e. ones that span an unbouned number of statements following them)
    ifUnboundedd,
    matchUnboundedd,
    structUnboundedd,
    sumTypeUnboundedd,

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
}

public static enum CoreOperator {
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
