class Stack<T> {
    final List<T> _list = [];

    void push(T newItem) {
        _list.add(newItem);
    }

    T pop() {
        var result = _list.last;
        _list.removeLast();
        return result;
    }

    T? peek() {
        return _list.isNotEmpty ? _list.last : null;
    }
}
