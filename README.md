# c syntax reference

a simple c reference repo that mirrors the structure and teaching style of the java syntax project

## project structure

```text
include/
  app/
    menu.h
  syntax/
    arrays_strings.h
    basics.h
    control_flow.h
    file_io.h
    functions_demo.h
    pointers.h
    structs_memory.h
src/
  app/
    menu.c
  syntax/
    arrays_strings.c
    basics.c
    control_flow.c
    file_io.c
    functions_demo.c
    pointers.c
    structs_memory.c
  main.c
makefile
```

## what this gives you

- one small runnable app with a topic menu
- one file per topic so each concept stays focused
- short examples that show what c is doing without too much noise
- comments only where they actually help

## build and run

### powershell on windows

```powershell
Set-Location .\c-syntax
if (-not (Test-Path out)) { New-Item -ItemType Directory -Path out | Out-Null }
gcc -std=c11 -Wall -Wextra -pedantic -Iinclude `
  src\main.c `
  src\app\menu.c `
  src\syntax\basics.c `
  src\syntax\control_flow.c `
  src\syntax\functions_demo.c `
  src\syntax\pointers.c `
  src\syntax\arrays_strings.c `
  src\syntax\structs_memory.c `
  src\syntax\file_io.c `
  -o out\c_syntax.exe

.\out\c_syntax.exe
```

### with make

```bash
make
make run
```

## topics

- basics
- control flow
- functions
- pointers
- arrays and strings
- structs and memory
- file io

## next cleanup ideas

- add a small exercises folder once the core syntax pages feel complete
- add a second pass with common pitfalls like dangling pointers and buffer limits
- keep each topic file short enough that you can scan it in one sitting
