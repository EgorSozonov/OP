#ifndef EITHER_HEADER_H
#define EITHER_HEADER_H


#define DEFINE_EITHER_HEADER(TYPELEFT, TYPERIGHT)              \
    typedef struct {                                           \
        bool isRight;                                          \
        union {                                                \
            TYPELEFT* left;                                    \
            TYPERIGHT right;                                   \
        } content;                                             \
    } Either ## TYPELEFT ## TYPERIGHT;                         \
    TYPELEFT* getLeft(Either ## TYPELEFT ## TYPERIGHT * e);    \
    TYPERIGHT getRight(Either ## TYPELEFT ## TYPERIGHT * e);

#endif
