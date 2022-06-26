#include <stdlib.h>
typedef struct ArenaChunk ArenaChunk;
typedef struct Arena Arena;

Arena* mkArena();
void* arenaAllocate(Arena* ar, size_t allocSize);
void arenaDelete(Arena* ar);
