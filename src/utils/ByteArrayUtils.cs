namespace O7;

public static class ByteArrayUtils {
    public static bool areEqual(byte[] a, byte[] b) {
        if (a.Length != b.Length) return false;
        for (int i = 0; i < a.Length; ++i) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }
}
