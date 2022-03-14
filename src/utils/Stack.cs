namespace O7;
using System.Collections.Generic;
using System.Linq;

public sealed class Stack<T> where T : class {
    private List<T> list;

    public Stack() {
        this.list = new List<T>();
    }

    public void push(T newItem) {
        list.Add(newItem);
    }

    public T pop() {
        var result = list[list.Count - 1];
        list.removeLast();
        return result;
    }

    public T peek() {
        return list.Any() ? list[list.Count - 1] : null;
    }
}
