package main.java.tech.sozonov.o7.utils;
import java.util.List;

public class ListUtils {
    public static <T> void removeLast(List<T> a) {
        a.remove(a.size() - 1);
    }

    public static <T> T last(List<T> a) {
        return a.get(a.size() -1);
    }

    public static <T> boolean hasValues(List<T> a) {
        return a.size() > 0;
    }
}
