SHELL:=/bin/bash
CC=gcc
CFLAGS= - g -WALL

EXE=build
OBJ=el6

$(EXE) :
$(OBJ) :

all: $(EXE) $(OBJ)
	cd ./scripts; \
		./$(EXE).sh $(OBJ)

compile: $(EXE)
	d ./scripts; \
		./$(EXE).sh --compile
	

clean: $(EXE)
	cd ./scripts; \
	./$(EXE).sh --cleanup 