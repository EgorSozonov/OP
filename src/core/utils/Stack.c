#include "Stack.h"
#include "arena.h"
#include <stdbool.h>


struct Foo {
    int a;
    double b;
};

struct Stack {
    int capacity;
    int length;
    Arena* arena;
    Foo (* content)[];
};

Stack* mkStack(Arena* ar, int length) {
    if (length < 4) return arenaAllocate(ar, 4*sizeof(Foo));
    arenaAllocate(ar, length*sizeof(Foo));
}

bool hasValues(Stack* st) {
    return st->length > 0;
}

Foo pop(Stack* st) {
    --st->length;
    Foo arr[] = st->content;
    return arr[st->length - 1];
}

void push(Stack* st, Foo newItem) {
    //Foo* ctnt = st->content;
    if (st->length < st->capacity) {
        memcpy((Foo*)(st->content) + (st->length), &newItem, sizeof(Foo));
        ++st->length;
    } else {
        Foo* newContent[] = arenaAllocate(st->arena, 2*(st->capacity)*sizeof(Foo));
        memcpy(newContent, st->content, st->length*sizeof(Foo));
        memcpy(newContent + (st->length), &newItem, sizeof(Foo));

        st->capacity *= 2;
        ++st->length;
        free(st->content);

        st->content = newContent;
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
