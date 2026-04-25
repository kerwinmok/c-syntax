#include <stdio.h>

#include "syntax/control_flow.h"

void run_control_flow_demo(void) {
    int value = 7;

    printf("--- control flow ---\n");

    if (value % 2 == 0) {
        printf("%d is even\n", value);
    } else {
        printf("%d is odd\n", value);
    }

    printf("for loop: ");
    for (int index = 0; index < 3; index++) {
        printf("%d ", index);
    }
    printf("\n");

    int countdown = 3;
    printf("while loop: ");
    while (countdown > 0) {
        printf("%d ", countdown);
        countdown--;
    }
    printf("lift off\n\n");
}
