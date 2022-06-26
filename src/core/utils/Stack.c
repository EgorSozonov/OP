#include "Stack.h"
#include "arena.h"
#include <string.h>
#include <stdio.h>


struct Stack {
    int capacity;
    int length;
    Arena* arena;
    Foo (* content)[];
};

Stack* mkStack(Arena* ar, int initCapacity) {
    int capacity = initCapacity < 4 ? 4 : initCapacity;
    Stack* result = arenaAllocate(ar, sizeof(Stack));
    result->capacity = capacity;
    result->length = 4;
    result->arena = ar;
    Foo (* arr)[] = arenaAllocate(ar, capacity*sizeof(Foo));
    result->content = arr;
    return result;
}

bool hasValues(Stack* st) {
    return st->length > 0;
}

Foo pop(Stack* st) {
    --st->length;
    return *((Foo*)st->content + st->length);
}

void push(Stack* st, Foo newItem) {
    if (st->length < st->capacity) {
        memcpy((Foo*)(st->content) + (st->length), &newItem, sizeof(Foo));
        ++st->length;
    } else {
        Foo* newContent = arenaAllocate(st->arena, 2*(st->capacity)*sizeof(Foo));
        memcpy(newContent, (Foo*)st->content, st->length*sizeof(Foo));
        memcpy(newContent + (st->length), &newItem, sizeof(Foo));

        st->capacity *= 2;
        ++st->length;
        free(st->content);

        st->content = (Foo(*)[])newContent;
    }
}

void clear(Stack* st) {
    st->length = 0;
}


// #define DEFINE_LL_NODE(CONCRETE_TYPE) \
//   struct node_of_ ## CONCRETE_TYPE \
//     { \
//       CONCRETE_TYPE data; \
//       struct node_of_ ## CONCRETE_TYPE *next; \
//     };

// #define DECLARE_LL_NODE(CONCRETE_TYPE,VARIABLE_NAME) \
//   struct node_of_ ## CONCRETE_TYPE VARIABLE_NAME;
