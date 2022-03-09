namespace O7;


using System.Collections.Generic;
using System.Linq;

public sealed class Stack<T> where T : class {
    private List<T> list;
    public Stack() {
        this.list = new List<T>();
    }

    void push(T newItem) {
        list.Add(newItem);
    }

    T pop() {
        var result = list[list.Count - 1];
        list.RemoveAt(list.Count - 1);
        return result;
    }

    T peek() {
        return list.Any() ? null : list[list.Count - 1];
    }
}
