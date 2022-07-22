#include "FileReader.h"

#include <stdio.h>
#include <string.h>


DEFINE_STACK(Instr)

BytecodeRead readBytecode(char* fName, Arena* ar) {
    StackInstr* stack = mkStackInstr(ar, 100);

    Arena* localAr = mkArena();

    FILE * fp;
    char * line = NULL;
    size_t len = 0;
    ssize_t read;
    fp = fopen(fName, "r");
    printf("%s %p\n", fName, fp);
    if (fp == NULL) exit(EXIT_FAILURE);
    bool opCodeError = true;

    while ((read = getline(&line, &len, fp)) != -1) {
        opCodeError = readOpCode(line, stack);
        printf("%s", line);
    }

    fclose(fp);
    if (line)
        free(line);
    BytecodeRead result = {.opcodes = stack, .errMsg = NULL};
    return result;
}

/**
 * Sets the errFlag to true if error
 */
bool readOpCode(char* line, StackInstr* stack, Arena* localAr) {
    char wordBuf[20];
    int prev = 0;
    int ind = 0;
    String upTo4Tokens[] = splitString(line, localAr);

    // while (line[ind] != '\0') {
    //     if (line[ind] == ' ') {
    //         if (ind - prev >= 20) return false;
    //         strcpy(wordBuf, line, prev, ind);
    //         prev = ind + 1;
    //     }
    //     ++ind;
    // }
}

String* splitString(char* inp, Arena* localAr) {
}
