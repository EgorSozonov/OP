
const ExprTag = enum {
    WordToken,
    IntToken,
};

const Expr = union(ExprTag) {
    WordToken: *[]u16,
    IntToken: i64,
};
