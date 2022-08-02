#include <stdio.h>
#include <stdlib.h>
#include "Arena.h"

#define CHUNK_QUANT 32768


struct ArenaChunk {
    size_t size;
    ArenaChunk* next;
    char memory[]; // flexible array member
};

struct Arena {
    ArenaChunk* firstChunk;
    ArenaChunk* currChunk;
    int currInd;
};


size_t minChunkSize() {
    return (size_t)(CHUNK_QUANT - 32);
}

Arena* mkArena() {
    Arena* result = malloc(sizeof(Arena));

    size_t firstChunkSize = minChunkSize();

    ArenaChunk* firstChunk = malloc(firstChunkSize);

    firstChunk->size = firstChunkSize - sizeof(ArenaChunk);
    firstChunk->next = NULL;

    result->firstChunk = firstChunk;
    result->currChunk = firstChunk;
    result->currInd = 0;

    return result;
}

/**
 * Calculates memory for a new chunk. Memory is quantized and is always 32 bytes less
 */
size_t calculateChunkSize(size_t allocSize) {
    // 32 for any possible padding malloc might use internally,
    // so that the total allocation size is a good even number of OS memory pages.
    // TODO review
    size_t fullMemory = sizeof(ArenaChunk) + allocSize + 32; // struct header + main memory chunk + space for malloc bookkeep
    int mallocMemory = fullMemory < CHUNK_QUANT
                        ? CHUNK_QUANT
                        : (fullMemory % CHUNK_QUANT > 0
                           ? (fullMemory/CHUNK_QUANT + 1)*CHUNK_QUANT
                           : fullMemory);

    return mallocMemory - 32;
}

/**
 * Allocate memory in the arena, malloc'ing a new chunk if needed
 */
void* arenaAllocate(Arena* ar, size_t allocSize) {
    if (ar->currInd + allocSize >= ar->currChunk->size) {
        size_t newSize = calculateChunkSize(allocSize);

        ArenaChunk* newChunk = malloc(newSize);
        if (!newChunk) {
            perror("malloc error when allocating arena chunk");
            exit(EXIT_FAILURE);
        };
        // sizeof includes everything but the flexible array member, that's why we subtract it
        newChunk->size = newSize - sizeof(ArenaChunk);
        newChunk->next = NULL;
        printf("Allocated a new chunk with bookkeep size %zu, array size %zu\n", sizeof(ArenaChunk), newChunk->size);

        ar->currChunk->next = newChunk;
        ar->currChunk = newChunk;
        ar->currInd = 0;
    }
    void* result = (void*)(ar->currChunk->memory + (ar->currInd));
    ar->currInd += allocSize;
    return result;
}


void arenaDelete(Arena* ar) {
    ArenaChunk* curr = ar->firstChunk;
    while (curr != NULL) {
        printf("freeing a chunk of size %zu\n", curr->size);
        free(curr);
        curr = curr->next;
    }
    printf("freeing arena itself\n");
    free(ar);
}





// #include "arena.h"
// #include <stdio.h>
// #include <stdlib.h>

// #define PAGE_SIZE 4095

// static arena_t *
// _arena_create(size_t size) {
//   arena_t *arena = (arena_t *) calloc(1, sizeof(arena_t));
//   if(!arena) return NULL;
//   arena->region = (uint8_t *) calloc(size, sizeof(uint8_t));
//   arena->size   = size;
//   if(!arena->region) { free(arena); return NULL; }
//   return arena;
// }

// arena_t *
// arena_create() {
//   return _arena_create(PAGE_SIZE);
// }

// void *
// arena_malloc(arena_t *arena, size_t size) {
//   arena_t *last = arena;

//   do {
//     if((arena->size - arena->current) >= size){
//       arena->current += size;
//       return arena->region + (arena->current - size);
//     }
//     last = arena;
//   } while((arena = arena->next) != NULL);

//   size_t asize   = size > PAGE_SIZE ? size : PAGE_SIZE;
//   arena_t *next  = _arena_create(asize);
//   last->next     = next;
//   next->current += size;
//   return next->region;
// }

// void
// arena_destroy(arena_t *arena) {
//   arena_t *next, *last = arena;
//   do {
//     next = last->next;
//     free(last->region);
//     free(last);
//     last = next;
//   } while(next != NULL);
// }
