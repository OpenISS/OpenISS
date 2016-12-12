# Compiler and compiler flags
CC=gcc
#CFLAGS=-g -Wall -ansi -pedantic -Wmissing-declarations -Wmissing-prototypes -D__EXTENSIONS__ -D_GNU_SOURCE -I ../util/ -L ../util/
CFLAGS=\
	-g \
	-Wall -ansi -pedantic -Wmissing-declarations -Wmissing-prototypes \
	-D__EXTENSIONS__ -D_GNU_SOURCE \
	-I include \
	-I ../../../ogl/external/glm-0.9.7.1 \
	-I ../../../ogl/external/glew-1.13.0/include \
	-I ../../../ogl/external/glfw-3.1.2/include/GLFW

# A list of executable, object, and library files
EXE=OpenISS
OBJ=$(EXE).o

# Default target
all: $(EXE)
	@echo "All is perfectly done!"

# Other subtargets to make it happen
$(EXE): $(OBJ)
	$(CC) $(OBJ) -o $(EXE)

%.o: %.c %.h
	$(CC) $(CFLAGS) -c $<

vfx.o: vfx.c
	$(CC) $(CFLAGS) -c $<


# Cleaning up afterwards
clean:
	rm -f $(OBJ)
