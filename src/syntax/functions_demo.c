#include <stdio.h>

#include "syntax/functions_demo.h"

static int square(int value) {
    return value * value;
}

static void swap_values(int *left, int *right) {
    int temp = *left;
    *left = *right;
    *right = temp;
}

void run_functions_demo(void) {
    int left = 4;
    int right = 9;

    printf("--- functions ---\n");
    printf("square of %d = %d\n", left, square(left));
    printf("before swap %d %d\n", left, right);
    swap_values(&left, &right);
    printf("after swap %d %d\n", left, right);
    printf("\n");
}
