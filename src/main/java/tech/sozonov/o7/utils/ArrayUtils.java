package tech.sozonov.o7.utils;
import java.util.Arrays;
import java.util.List;

import tech.sozonov.o7.lexer.types.OperatorSymb;


public class ArrayUtils {

public static boolean areEqual(byte[] a, byte[] b) {
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; ++i) {
        if (a[i] != b[i]) return false;
    }
    return true;
}


public static void l(String a) {
    System.out.println(a);
}


public static double tryParseDouble(String inp) {
    try {
        return Double.parseDouble(inp);
    } catch (Exception e) {
        return Double.NaN;
    }
}


public static byte[] subArray(byte[] arr, int start, int end) {
    return Arrays.copyOfRange(arr, start, end + 1);
}


public static boolean arraysEqual(List<OperatorSymb> a, List<OperatorSymb> b) {
    if (a.isEmpty() || a.size() != b.size())
        return false;
    for (int i = 0; i < a.size(); ++i) {
        if (a.get(i) != b.get(i))
            return false;
    }
    return true;
}

}
