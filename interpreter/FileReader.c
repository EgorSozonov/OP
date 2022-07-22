#include "FileReader.h"

#include <stdio.h>
#include <string.h>


DEFINE_STACK(Instr)

BytecodeRead readBytecode(char* fName, Arena* ar) {
    StackInstr* stack = mkStackInstr(ar, 100);

    FILE * fp;
    char * line = NULL;
    size_t len = 0;
    ssize_t read;
    fp = fopen(fName, "r");
    printf("%s %p\n", fName, fp);
    if (fp == NULL) exit(EXIT_FAILURE);

    while ((read = getline(&line, &len, fp)) != -1) {
        printf("Retrieved line of length %zu:\n", read);
        printf("%s", line);
    }

    fclose(fp);
    if (line)
        free(line);
    BytecodeRead result = {.opcodes = stack, .errMsg = NULL};
    return result;
}
