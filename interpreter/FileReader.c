#include "FileReader.h"
#include "../utils/String.h"

DEFINE_STACK(Expr)

typedef struct {
    StackExpr opcodes;
    String* errMsg;
} BytecodeRead;



BytecodeRead readBytecode(char* fName) {
    Arena *ar = mkArena();
    printf("Hello world\n");
    FILE * fp;
    char * line = NULL;
    size_t len = 0;
    ssize_t read;

    fp = fopen("/etc/motd", "r");
    if (fp == NULL)
        exit(EXIT_FAILURE);

    while ((read = getline(&line, &len, fp)) != -1) {
        printf("Retrieved line of length %zu:\n", read);
        printf("%s", line);
    }

    fclose(fp);
    if (line)
        free(line);
    exit(EXIT_SUCCESS);
    arenaDelete(ar);
}
