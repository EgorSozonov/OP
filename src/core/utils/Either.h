#ifndef EITHER_H
#define EITHER_H


#define DEFINE_EITHER(TYPELEFT, TYPERIGHT)                     \
    TYPELEFT* getLeft(Either ## TYPELEFT ## TYPERIGHT * e) {   \
        return (TYPELEFT*)e->content.left;                     \
    }                                                          \
    TYPERIGHT getRight(Either ## TYPELEFT ## TYPERIGHT* e) {   \
        return (TYPERIGHT)e->content.right;                    \
    }

#endif
