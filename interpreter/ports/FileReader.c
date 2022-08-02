#include "FileReader.h"
#include <stdio.h>
#include <string.h>
#include "../utils/Stack.h"
#include "../utils/Arena.h"
#include "../types/Types.h"


DEFINE_STACK(Instr)

String* splitString(char* inp, Arena* localAr) {
    int prev = 0;
    int curr = 0;

    while (inp[curr] != '\0') {
        if (inp[curr] == ' ') {
            String* substr = allocateFromSubstring(localAr, inp, prev, curr - prev);
            printf("String: %s", substr->content);
            prev = curr + 1;
        }
        ++curr;
    }
    return NULL;
}

/**
 * Sets the errFlag to true if error
 */
bool readOpCode(char* line, StackInstr* stack, Arena* localAr) {
    String* upTo4Tokens = splitString(line, localAr);

    return false;
    // while (line[ind] != '\0') {
    //     if (line[ind] == ' ') {
    //         if (ind - prev >= 20) return false;
    //         strcpy(wordBuf, line, prev, ind);
    //         prev = ind + 1;
    //     }
    //     ++ind;
    // }
}

BytecodeRead readBytecode(char* fName, Arena* ar) {
    StackInstr* stack = mkStackInstr(ar, 100);

    Arena* localAr = mkArena();

    FILE * fp;
    char * line = NULL;
    size_t len = 0;
    size_t read;
    fp = fopen(fName, "r");
    if (fp == NULL) exit(EXIT_FAILURE);

    bool opCodeError = true;
    while ((read = getline(&line, &len, fp)) != -1 && opCodeError) {
        opCodeError = readOpCode(line, stack, localAr);
        //printf("%s", line);
    }

    fclose(fp);
    if (line)
        free(line);
    BytecodeRead result = {.opcodes = stack, .errMsg = NULL};
    return result;
}
