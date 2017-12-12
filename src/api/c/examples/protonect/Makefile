# basic makefile modified from atsm/examples/c/syscalls/util/Makefile
# Compiler and compiler flags
CC=gcc
CFLAGS=-g -Wall -ansi -pedantic

# A list of executable, object, and library files
EXE=CProtonect
OBJ=$(EXE).o CProtonectWrapper.o

# Default target
all: $(EXE)
	@echo "All is perfectly done!"

$(EXE): $(OBJ)
	$(CC) $(OBJ) -o $(EXE)

%.o: %.c
	$(CC) $(CFLAGS) -c $<

# Cleaning up afterwards
clean:
	rm -f $(OBJ) $(EXE)

