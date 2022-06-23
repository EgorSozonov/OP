#include <stdio.h>
#include <stddef.h>
#include <stdalign.h>
#include <stdlib.h>
#include <stdint.h>
#include <inttypes.h>

#define MIN_SLAB_SIZE // 32768 - 32;


typedef struct arenaSlab arenaSlab;
typedef struct arena arena;

struct arena {
    arenaSlab* slabs;
    arenaSlab* currSlab;
    int currInd;
};


// void* ptr = nullptr;
// char buffer[sizeof(void*)];
// memcpy(buffer, &ptr, sizeof(void*));

// And back:

// memcpy(&ptr, buffer, sizeof(void*));

struct arenaSlab {
    size_t size;
    char memory[];
    arenaSlab* next;
};

void* allocate(arena* a, size_t sz) {
    if (a->currInd + sz >= a->currSlab->size) {
        size_t newSize = alignof(max_align_t);
        (sz, MIN_SLAB_SIZE) + sizeof(size_t) + sizeof(arenaSlab*);
        arenaSlab* newSlab = malloc(newSize);
        if (!newSlab) {
            perror("malloc make_employees");
            exit(EXIT_FAILURE);
        };
        newSlab->size = newSize;
    }
}

void deallocate(arena* a) {

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
