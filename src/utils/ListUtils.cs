namespace O7;
using System.Collections.Generic;

public static class ListUtils {
    public static void removeLast<T>(this List<T> lst) {
        lst.RemoveAt(lst.Count - 1);
    }
}
