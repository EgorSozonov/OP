#include "String.h"
#include "Arena.h"
#include <string.h>
#include <math.h>


#define MIN(X, Y) (((X) < (Y)) ? (X) : (Y))

///
/// \brief allocateString allocates a C string literal into an arena. The length of the literal has to be provided in an arg.
/// It should NOT include +1 for the \0 character, as that will be added automatically.
/// \param ar
/// \param length
/// \param content
/// \return
///
String* allocateString(Arena* ar, int length, char* content) {
    String* result = arenaAllocate(ar, length + 1 + sizeof(String));
    result->length = length;
    memcpy(result->content, content, length + 1);
    return result;
}

String* allocateFromSubstring(Arena* ar, char* content, int start, int length) {
    if (length <= 0 || start < 0) return NULL;
    int i = 0;
    while (content[i] != '\0') {
        ++i;
    }
    if (i < start) return NULL;
    int realLength = MIN(i - start, length);

    String* result = arenaAllocate(ar, realLength + 1 + sizeof(String));
    result->length = realLength;
    strncpy(result->content, content, realLength);
    result->content[realLength] = '\0';
    return result;
}
