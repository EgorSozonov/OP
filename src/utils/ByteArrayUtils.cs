namespace O7;
using System;

public static class ByteArrayUtils {
    public static bool areEqual(byte[] a, byte[] b) {
        if (a.Length != b.Length) return false;
        for (int i = 0; i < a.Length; ++i) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    public static async void l(string a) {
        Console.WriteLine(a);
    }
}
