typedef struct Stack Stack;

typedef struct Foo Foo;

Stack* mkStack(Arena* ar, int length);
bool hasValues(Stack* st);
Foo pop(Stack* st);
void push(Stack* st, Foo newItem);
void clear(Stack* st);
