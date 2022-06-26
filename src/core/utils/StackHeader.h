#ifndef STACK_HEADER_H
#define STACK_HEADER_H


#define DEFINE_STACK_HEADER(CONCRETE_TYPE)                                                     \
    typedef struct {                                                                           \
        int capacity;                                                                          \
        int length;                                                                            \
        Arena* arena;                                                                          \
        CONCRETE_TYPE (* content)[];                                                           \
    } Stack ## CONCRETE_TYPE;                                                                  \
    Stack ## CONCRETE_TYPE * mkStack ## CONCRETE_TYPE (Arena* ar, int initCapacity);           \
    bool hasValues ## CONCRETE_TYPE (Stack ## CONCRETE_TYPE * st);                             \
    CONCRETE_TYPE pop ## CONCRETE_TYPE (Stack ## CONCRETE_TYPE * st);                          \
    void push ## CONCRETE_TYPE (Stack ## CONCRETE_TYPE * st, CONCRETE_TYPE newItem);           \
    void clear ## CONCRETE_TYPE (Stack ## CONCRETE_TYPE * st);


#endif
