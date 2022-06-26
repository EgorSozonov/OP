#ifndef STACK_H
#define STACK_H


#define DEFINE_STACK(CONCRETE_TYPE)                                          \
    typedef struct {                                                         \
        int capacity;                                                        \
        int length;                                                          \
        Arena* arena;                                                        \
        CONCRETE_TYPE (* content)[];                                         \
    } Stack ## CONCRETE_TYPE;                                                                  \
    Stack ## CONCRETE_TYPE * mkStack ## CONCRETE_TYPE (Arena* ar, int initCapacity) {          \
        int capacity = initCapacity < 4 ? 4 : initCapacity;                                    \
        Stack ## CONCRETE_TYPE * result = arenaAllocate(ar, sizeof(Stack ## CONCRETE_TYPE));   \
        result->capacity = capacity;                                         \
        result->length = 4;                                                  \
        result->arena = ar;                                                  \
        CONCRETE_TYPE (* arr)[] = arenaAllocate(ar, capacity*sizeof(CONCRETE_TYPE));    \
        result->content = arr;                                                          \
        return result;                                                                  \
    }                                                                                   \
    bool hasValues ## CONCRETE_TYPE (Stack ## CONCRETE_TYPE * st) {                     \
        return st->length > 0;                                                          \
    }                                                                                   \
    CONCRETE_TYPE pop ## CONCRETE_TYPE (Stack ## CONCRETE_TYPE * st) {                  \
        --st->length;                                                                   \
        return *((CONCRETE_TYPE*)st->content + st->length);                             \
    }                                                                                   \
    void push ## CONCRETE_TYPE (Stack ## CONCRETE_TYPE * st, CONCRETE_TYPE newItem) {   \
        if (st->length < st->capacity) {                                                \
            memcpy((CONCRETE_TYPE*)(st->content) + (st->length), &newItem, sizeof(CONCRETE_TYPE));          \
            ++st->length;                                                                                   \
        } else {                                                                                            \
            CONCRETE_TYPE* newContent = arenaAllocate(st->arena, 2*(st->capacity)*sizeof(CONCRETE_TYPE));   \
            memcpy(newContent, (CONCRETE_TYPE*)st->content, st->length*sizeof(CONCRETE_TYPE));              \
            memcpy(newContent + (st->length), &newItem, sizeof(CONCRETE_TYPE)); \
            st->capacity *= 2;                                                  \
            ++st->length;                                                       \
            free(st->content);                                                  \
            st->content = (CONCRETE_TYPE(*)[])newContent;                       \
        }                                                                       \
    }                                                                           \
    void clear ## CONCRETE_TYPE (Stack ## CONCRETE_TYPE * st) {                 \
        st->length = 0;                                                         \
    }                                                                           \


#endif
