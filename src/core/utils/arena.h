typedef struct ArenaChunk ArenaChunk;
typedef struct Arena Arena;

Arena* mkArena();
void* allocate(Arena* ar, size_t allocSize);
void delete(Arena* ar);
