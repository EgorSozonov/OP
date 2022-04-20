package tech.sozonov.o7.utils;


public final class MutableBoolean {
    public boolean v;

    public MutableBoolean(boolean _v) {
        v = _v;
    }

    public static boolean eq(MutableBoolean a, MutableBoolean b) {
        return a.v == b.v;
    }
}
