#ifndef BOXED_STACK_HEADER_H
#define BOXED_STACK_HEADER_H

#include "stdbool.h"
#define DEFINE_BOXED_STACK_HEADER(CONCRETE_TYPE)                                                     \
    typedef struct {                                                                           \
        int capacity;                                                                          \
        int length;                                                                            \
        Arena* arena;                                                                          \
        CONCRETE_TYPE (* content)[];                                                           \
    } BoxedStack ## CONCRETE_TYPE;                                                                  \
    BoxedStack ## CONCRETE_TYPE * mkBoxedStack ## CONCRETE_TYPE (Arena* ar, int initCapacity);           \
    bool hasValuesBoxed ## CONCRETE_TYPE (BoxedStack ## CONCRETE_TYPE * st);                             \
    CONCRETE_TYPE* popBoxed ## CONCRETE_TYPE (BoxedStack ## CONCRETE_TYPE * st);                          \
    void pushBoxed ## CONCRETE_TYPE (BoxedStack ## CONCRETE_TYPE * st, CONCRETE_TYPE* newItem);           \
    void clearBoxed ## CONCRETE_TYPE (BoxedStack ## CONCRETE_TYPE * st);


#endif
