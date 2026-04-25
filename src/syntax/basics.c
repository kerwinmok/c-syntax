#include <stdio.h>

#include "syntax/basics.h"

void run_basics_demo(void) {
    int age = 21;
    double ratio = 3.5;
    char grade = 'a';
    const char *label = "starter";

    printf("--- basics ---\n");
    printf("int age = %d\n", age);
    printf("double ratio = %.1f\n", ratio);
    printf("char grade = %c\n", grade);
    printf("string label = %s\n", label);
    printf("sizeof int = %zu bytes\n", sizeof(age));
    printf("sizeof double = %zu bytes\n", sizeof(ratio));
    printf("\n");
}
