#include <stdio.h>

#include "syntax/file_io.h"

void run_file_io_demo(void) {
    const char *path = "out/sample.txt";
    FILE *writer = fopen(path, "w");

    printf("--- file io ---\n");

    if (writer == NULL) {
        printf("could not open %s for writing\n\n", path);
        return;
    }

    fprintf(writer, "hello from c\n");
    fclose(writer);

    FILE *reader = fopen(path, "r");
    if (reader == NULL) {
        printf("could not reopen %s\n\n", path);
        return;
    }

    char buffer[64];
    if (fgets(buffer, sizeof(buffer), reader) != NULL) {
        printf("read line %s", buffer);
    }
    fclose(reader);
    printf("\n");
}
