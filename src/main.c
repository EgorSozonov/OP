#include <stdio.h>
#include <stdlib.h>
#include "core/utils/arena.h"
#include "core/utils/Stack.h"



int main(int argc, char* argv[]) {
    printf("Hello world\n");
    Arena *ar = mkArena();
    Stack* st = mkStack(ar, 10);
    Foo foo = {.a = 5, .b = 1.2};
    push(st, foo);

    // Arena *ar = mkArena();
    // Foo* firstStruct = (Foo*) arenaAllocate(ar, sizeof(Foo));

    // firstStruct->i = 500;
    // firstStruct->f = 5.4;
    // firstStruct->code[0] = 'a';
    // firstStruct->code[1] = 's';
    // firstStruct->code[2] = 'd';
    // firstStruct->code[3] = 'f';
    // firstStruct->code[4] = '\0';
    // printf("i = %d, f = %f, code = %s\n", firstStruct->i, firstStruct->f, firstStruct->code);
    // delete(ar);


    return 0;
}
