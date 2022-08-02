#ifndef BOXED_STACK_H
#define BOXED_STACK_H


#define DEFINE_BOXED_STACK(CONCRETE_TYPE)                                          \
    BoxedStack ## CONCRETE_TYPE * mkBoxedStack ## CONCRETE_TYPE (Arena* ar, int initCapacity) {          \
        int capacity = initCapacity < 4 ? 4 : initCapacity;                                    \
        BoxedStack ## CONCRETE_TYPE * result = arenaAllocate(ar, sizeof(BoxedStack ## CONCRETE_TYPE));   \
        result->capacity = capacity;                                         \
        result->length = 4;                                                  \
        result->arena = ar;                                                  \
        CONCRETE_TYPE** arr = arenaAllocate(ar, capacity*sizeof(CONCRETE_TYPE*));    \
        result->content = arr;                                                          \
        return result;                                                                  \
    }                                                                                   \
    bool hasValuesBoxed ## CONCRETE_TYPE (BoxedStack ## CONCRETE_TYPE * st) {                     \
        return st->length > 0;                                                          \
    }                                                                                   \
    CONCRETE_TYPE* popBoxed ## CONCRETE_TYPE (BoxedStack ## CONCRETE_TYPE * st) {                  \
        --st->length;                                                                   \
        return (CONCRETE_TYPE*)(st->content[st->length]);                             \
    }                                                                                   \
    void pushBoxed ## CONCRETE_TYPE (BoxedStack ## CONCRETE_TYPE * st, CONCRETE_TYPE* newItem) {   \
        if (st->length < st->capacity) {                                                \
            *(st->content + st->length) = newItem;          \
            ++st->length;                                                                                   \
        } else {                                                                                            \
            CONCRETE_TYPE** newContent = arenaAllocate(st->arena, 2*(st->capacity)*sizeof(CONCRETE_TYPE*));   \
            memcpy(newContent, (CONCRETE_TYPE*)st->content, st->length*sizeof(CONCRETE_TYPE*));              \
            memcpy(newContent + (st->length), &newItem, sizeof(CONCRETE_TYPE)); \
            st->capacity *= 2;                                                  \
            ++st->length;                                                       \
            free(st->content);                                                  \
            st->content = (CONCRETE_TYPE(*)[])newContent;                       \
        }                                                                       \
    }                                                                           \
    void clearBoxed ## CONCRETE_TYPE (BoxedStack ## CONCRETE_TYPE * st) {                 \
        st->length = 0;                                                         \
    }                                                                           \


#endif
