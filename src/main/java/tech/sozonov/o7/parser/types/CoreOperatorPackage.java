package tech.sozonov.o7.parser.types;

public class CoreOperatorPackage {

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

public static enum AssignmentType {
    immutableDef,
    mutableAssignment,
    mutablePlus,
    mutableMinus,
    mutableTimes,
    mutableDiv,
}

}
