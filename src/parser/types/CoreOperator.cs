namespace O7;

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

public enum AssignmentType {
    immutableDef,
    mutableDef,
    mutableAssignment,
    mutablePlus,
    mutableMinus,
    mutableTimes,
    mutableDiv,
}
