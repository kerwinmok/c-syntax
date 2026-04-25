#include <stdio.h>

#include "syntax/pointers.h"

void run_pointers_demo(void) {
    int score = 42;
    int *score_ptr = &score;

    printf("--- pointers ---\n");
    printf("score = %d\n", score);
    printf("address of score = %p\n", (void *)score_ptr);
    printf("value through pointer = %d\n", *score_ptr);

    *score_ptr = 99;
    printf("updated score = %d\n", score);
    printf("\n");
}
