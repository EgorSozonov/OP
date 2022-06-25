#include "Stack.h"
#include "arena.h"
#include <stdbool.h>


typedef struct {
    int a;
    double b;
} Foo;

struct Stack {
    int capacity;
    int length;
    Arena* arena;
    Foo* content[];
};

Stack* makeStack(Arena* ar, int length) {
    if (length < 4) return arenaAllocate(ar, 4*sizeof(Foo));
    arenaAllocate(ar, length*sizeof(Foo));
}


bool hasValues(Stack* st) {
    return st->length > 0;
}

Foo pop(Stack* st) {
    Foo result = st->content[st->length - 1];
    --st->length;
    return result;
}

void push(Stack* st, Foo newItem) {
    if (st->length < st->capacity) {
        st->content[st->length] = newItem;
        ++st->length;
    } else {
        Foo newContent[] = arenaAllocate(st->arena, 2*(st->capacity)*sizeof(Foo));

        for (int i = 0; i < st->length; ++i) {
            newContent[i] = st->content[i];
        }
        newContent[st->length] = newItem;
        st->capacity *= 2;
        ++st->length;
        free(st->content);
        st->content = newContent;
    }
}

void clear(Stack* st) {
    st->length = 0;
}
