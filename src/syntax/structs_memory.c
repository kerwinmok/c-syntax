#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "syntax/structs_memory.h"

typedef struct {
    char name[32];
    int level;
} player;

void run_structs_memory_demo(void) {
    player local_player = {"sam", 3};
    player *heap_player = malloc(sizeof(player));

    printf("--- structs and memory ---\n");
    printf("stack player %s level %d\n", local_player.name, local_player.level);

    if (heap_player == NULL) {
        printf("allocation failed\n\n");
        return;
    }

    strcpy(heap_player->name, "riley");
    heap_player->level = 5;
    printf("heap player %s level %d\n", heap_player->name, heap_player->level);

    free(heap_player);
    heap_player = NULL;
    printf("heap memory released\n\n");
}
