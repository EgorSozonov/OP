#include <stdio.h>
#include <stdlib.h>
#include "arena.h"

#define CHUNK_QUANT 32768


struct arenaChunk {
    size_t size;
    arenaChunk* next;
    char memory[]; // flexible array member
};

struct arena {
    arenaChunk* slabs;
    arenaChunk* currChunk;
    int currInd;
};



size_t minChunkSize() {
    return (size_t)(CHUNK_QUANT - 32);
}

size_t calculateChunkSize(size_t allocSize) {
    // 32 for any possible padding malloc might use internally,
    // so that the total allocation size is a good even number of OS memory pages.
    size_t minSize = CHUNK_QUANT - 32;
    if (allocSize < minSize) return minSize;
    int tmp = allocSize / CHUNK_QUANT + 1;
    return (size_t)(tmp*CHUNK_QUANT - 32);
}


arena* mkArena() {
    printf("mkArena %zu\n", sizeof(arenaChunk));
    arena* result = malloc(sizeof(arena));

    result->currInd = 0;
    size_t firstChunkSize = minChunkSize();

    arenaChunk* firstChunk = malloc(firstChunkSize);

    printf("after first chunk allocation\n");

    firstChunk->size = firstChunkSize - sizeof(arenaChunk);
    firstChunk->next = NULL;

    result->slabs = firstChunk;
    result->currChunk = firstChunk;
    result->currInd = 0;
    return result;
}

void* allocate(arena* ar, size_t allocSize) {
    if (ar->currInd + allocSize >= ar->currChunk->size) {
        size_t newSize = calculateChunkSize(allocSize);

        arenaChunk* newChunk = malloc(newSize);
        if (!newChunk) {
            perror("malloc make_employees");
            exit(EXIT_FAILURE);
        };
        // sizeof includes everything but the flexible array member, that's why we subtract it
        newChunk->size = newSize - sizeof(arenaChunk);
        printf("Allocated a new chunk with bookkeep size %zu, array size %zu", sizeof(arenaChunk), newChunk->size);
        newChunk->next = NULL;
        ar->currChunk->next = newChunk;
    }
    void* result = (void*)(ar->currChunk->memory[ar->currInd]);
    ar->currInd += allocSize;
    return result;
}


void delete(arena* ar) {
    arenaChunk* curr = ar->currChunk;
    while (curr != NULL) {
        printf("freeing a chunk of size %zu", curr->size);
        free(curr);
        curr = curr->next;
    }
    printf("freeing arena itself");
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
