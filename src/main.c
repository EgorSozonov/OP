#include <stdio.h>
#include <stdlib.h>
#include "core/temp.h"
#include "core/utils/arena.h"

typedef struct Foo Foo;
struct Foo {
    int i;
    double f;
    char code[8];
};

int main(int argc, char* argv[]) {
    printf("Hello world\n");
    // int *p1 = malloc(4*sizeof(int));  // allocates enough for an array of 4 int
    // printf("mallocked\n");
    // int *p2 = malloc(sizeof(int[4])); // same, naming the type directly
    // int *p3 = malloc(4*sizeof *p3);   // same, without repeating the type name

    // if(p1) {
    //     for(int n=0; n<4; ++n) // populate the array
    //         p1[n] = n*n;
    //     for(int n=0; n<4; ++n) // print it back out
    //         printf("p1[%d] == %d\n", n, p1[n]);
    // }

    // free(p1);
    // free(p2);
    // free(p3);

    arena *ar = mkArena();
    Foo* firstStruct = allocate(ar, sizeof(Foo));
    firstStruct->i = 500;
    firstStruct->f = 5.4;
    firstStruct->code[0] = 'a';
    firstStruct->code[1] = 's';
    firstStruct->code[2] = 'd';
    firstStruct->code[3] = 'f';
    firstStruct->code[4] = 'f';
    printf("code = %s\n", firstStruct->code);
    delete(ar);

    return 0;
}
