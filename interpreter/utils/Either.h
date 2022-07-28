#ifndef EITHER_H
#define EITHER_H


#define DEFINE_EITHER(TYPELEFT, TYPERIGHT)                     \
    Either ## TYPELEFT ## TYPERIGHT left ## TYPELEFT ## TYPERIGHT(TYPELEFT * newValue) {    \
        return (Either ## TYPELEFT ## TYPERIGHT){ .isRight= false, .content = { .left = newValue } };   \
    }                                                                                       \
    Either ## TYPELEFT ## TYPERIGHT right ## TYPELEFT ## TYPERIGHT(TYPERIGHT newValue) {    \
        return (Either ## TYPELEFT ## TYPERIGHT){ .isRight= true, .content = { .right = newValue } };    \
    }                                                                                       \
    TYPELEFT* getLeft(Either ## TYPELEFT ## TYPERIGHT * e) {   \
        return (TYPELEFT*)e->content.left;                     \
    }                                                          \
    TYPERIGHT getRight(Either ## TYPELEFT ## TYPERIGHT* e) {   \
        return (TYPERIGHT)e->content.right;                    \
    }


#endif
