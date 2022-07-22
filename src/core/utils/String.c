#include "String.h"
#include "Arena.h"
#include <string.h>


String* allocateString(Arena* ar, int length, char* content) {
    String* result = arenaAllocate(ar, length + 1 + sizeof(String));
    result->length = length;
    memcpy(result->content, content, length + 1);
    return result;
}
