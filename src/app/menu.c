#include <stdio.h>

#include "app/menu.h"
#include "syntax/arrays_strings.h"
#include "syntax/basics.h"
#include "syntax/control_flow.h"
#include "syntax/file_io.h"
#include "syntax/functions_demo.h"
#include "syntax/pointers.h"
#include "syntax/structs_memory.h"

static void print_banner(void) {
    printf("========================================\n");
    printf(" c syntax reference\n");
    printf("========================================\n");
}

static void print_menu(void) {
    printf("\nchoose a topic\n");
    printf("1 basics\n");
    printf("2 control flow\n");
    printf("3 functions\n");
    printf("4 pointers\n");
    printf("5 arrays and strings\n");
    printf("6 structs and memory\n");
    printf("7 file io\n");
    printf("8 run all\n");
    printf("9 exit\n");
    printf("selection: ");
}

static void run_topic(int choice) {
    switch (choice) {
        case 1:
            run_basics_demo();
            break;
        case 2:
            run_control_flow_demo();
            break;
        case 3:
            run_functions_demo();
            break;
        case 4:
            run_pointers_demo();
            break;
        case 5:
            run_arrays_strings_demo();
            break;
        case 6:
            run_structs_memory_demo();
            break;
        case 7:
            run_file_io_demo();
            break;
        case 8:
            run_basics_demo();
            run_control_flow_demo();
            run_functions_demo();
            run_pointers_demo();
            run_arrays_strings_demo();
            run_structs_memory_demo();
            run_file_io_demo();
            break;
        default:
            printf("invalid option\n");
            break;
    }
}

void run_menu(void) {
    int choice = 0;

    print_banner();

    while (choice != 9) {
        print_menu();
        if (scanf("%d", &choice) != 1) {
            int discard = 0;
            while ((discard = getchar()) != '\n' && discard != EOF) {
            }
            printf("enter a number\n");
            continue;
        }

        if (choice == 9) {
            printf("done\n");
            return;
        }

        printf("\n");
        run_topic(choice);
    }
}
