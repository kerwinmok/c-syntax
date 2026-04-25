CC = gcc
CFLAGS = -std=c11 -Wall -Wextra -pedantic -Iinclude
OUT_DIR = out
TARGET = $(OUT_DIR)/c_syntax

SOURCES = \
	src/main.c \
	src/app/menu.c \
	src/syntax/basics.c \
	src/syntax/control_flow.c \
	src/syntax/functions_demo.c \
	src/syntax/pointers.c \
	src/syntax/arrays_strings.c \
	src/syntax/structs_memory.c \
	src/syntax/file_io.c

all: $(TARGET)

$(TARGET): $(SOURCES)
	mkdir -p $(OUT_DIR)
	$(CC) $(CFLAGS) $(SOURCES) -o $(TARGET)

run: $(TARGET)
	$(TARGET)

clean:
	rm -rf $(OUT_DIR)

.PHONY: all run clean
