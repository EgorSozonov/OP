typedef struct arenaChunk arenaChunk;
typedef struct arena arena;

arena* mkArena();
void* allocate(arena* ar, size_t allocSize);
void delete(arena* ar);
