package tech.sozonov.o7.utils;
import java.util.ArrayList;

public final class Stack<T> {
    private ArrayList<T> list;

    public Stack() {
        this.list = new ArrayList<T>();
    }

    public void push(T newItem) {
        list.add(newItem);
    }

    public T pop() {
        var result = list.get(list.size() - 1);
        list.remove(list.size() - 1);
        return result;
    }

    public T peek() {
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }
}
