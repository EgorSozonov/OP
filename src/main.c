#include <stdio.h>
#include <stdlib.h>
#include "core/utils/arena.h"


typedef struct Foo Foo;
struct Foo {
    int i;
    double f;
    char code[8];
};

int main(int argc, char* argv[]) {
    printf("Hello world\n");

    Arena *ar = mkArena();
    Foo* firstStruct = (Foo*) allocate(ar, sizeof(Foo));

    firstStruct->i = 500;
    firstStruct->f = 5.4;
    firstStruct->code[0] = 'a';
    firstStruct->code[1] = 's';
    firstStruct->code[2] = 'd';
    firstStruct->code[3] = 'f';
    firstStruct->code[4] = '\0';
    printf("i = %d, f = %f, code = %s\n", firstStruct->i, firstStruct->f, firstStruct->code);
    delete(ar);

    return 0;
}
