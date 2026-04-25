#include <stdio.h>
#include <string.h>

#include "syntax/arrays_strings.h"

void run_arrays_strings_demo(void) {
    int numbers[] = {2, 4, 6, 8};
    char word[32] = "c syntax";
    size_t count = sizeof(numbers) / sizeof(numbers[0]);

    printf("--- arrays and strings ---\n");
    printf("numbers: ");
    for (size_t index = 0; index < count; index++) {
        printf("%d ", numbers[index]);
    }
    printf("\n");

    strcat(word, " guide");
    printf("string value = %s\n", word);
    printf("string length = %zu\n", strlen(word));
    printf("\n");
}
