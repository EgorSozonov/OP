#include "arena.h"


typedef struct {
    int length;
    char content[];
} String;
String* allocateString(Arena* ar, int length, char* content);
