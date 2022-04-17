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

    // The name of a type, or a type constructor, or a type constructor application
    typecall,

    // Lists of stuff
    curlyBraces,
    dataInitializer,


    // Assignments and definitions
    assignImmutable,
    assignMutable,
    assignMutablePlus,
    assignMutableMinus,
    assignMutableTimes,
    assignMutableDiv,

    // Bounded core syntax forms (i.e. ones that span a fixed number of items - curlyBraces, parens and/or statements)
    whilee, // 2 items, except as part of "do-while" when it has 1
    doo, // 1 item
    forr, // 4 items
    foreachh, // 2 items
    tryy, // 1 item
    catchh, // 1 item
    finallyy, // 1 item
    typeDeclaration, // 1 item after 1 identifier
    //macroo, // for the future

    // Unbounded core syntax forms (i.e. ones that span an unbouned number of statements following them)
    iff,
    matchh,
    structt,
    sumTypee,
}

/**
 * The reserved words except the ones which are used to mark syntax contexts (like "if", "while" etc).
 */
public static enum ReservedWord {
    elsee,
    exportt,
    importt,
    hidingg,
    asz,
    modulee,
    typee,
    aliass,
    interfacee,
    impll,
    returnn,
    breakk,
    continuee,
    awaitt,
    yieldd,
    assertt,
    nodestructt,
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

    greaterThan,
    greaterThanOrEq,
    lessThan,
    lessThanOrEq,

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
