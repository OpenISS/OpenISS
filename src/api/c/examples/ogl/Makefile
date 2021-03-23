# Compiler and compiler flags
CC=gcc

#-Wall -ansi -pedantic -Wmissing-declarations -Wmissing-prototypes \

CFLAGS=\
	-g \
	-D__EXTENSIONS__ -D_GNU_SOURCE \
	-I ../../include \
	-I ../../../../../ogl/external/glm-0.9.7.1 \
	-I ../../../../../ogl/external/glew-1.13.0/include \
	-I ../../../../../ogl/external/glfw-3.1.2/include/GLFW

VFXLIBS=\
	-L ../../../../../ogl/build/external/glfw-3.1.2/src \
	-L ../../../../../ogl/build/external

# A list of executable, object, and library files
EXE=tutorial02 
EXE2=tutorial03
OBJ=tutorial02.o tutorial02Wrapper.o 
OBJ2=tutorial03.o tutorial03Wrapper.o

# Default target
all: $(EXE) $(EXE2)
	@echo "All is perfectly done!"

# Other subtargets to make it happen
$(EXE): $(OBJ)
	$(CC) $(OBJ) $(VFXLIBS) \
		-lGL -lglfw3 -lGLEW_1130 -lANTTWEAKBAR_116_OGLCORE_GLFW -lstdc++ \
		-lXxf86vm -lXcursor -lpthread -lrt -lX11 -lXrandr -lXi -lXinerama -lm -ldl \
		-o $(EXE)

$(EXE2): $(OBJ2)
	$(CC) $(OBJ2) $(VFXLIBS) \
		-lGL -lglfw3 -lGLEW_1130 -lANTTWEAKBAR_116_OGLCORE_GLFW -lstdc++ \
		-lXxf86vm -lXcursor -lpthread -lrt -lX11 -lXrandr -lXi -lXinerama -lm -ldl \
		-o $(EXE2)

%.o: %.c %.h
	$(CC) $(CFLAGS) $(VFXLIBS) -c $<

tutorial02Wrapper.o: tutorial02Wrapper.cpp tutorial02Wrapper.h
	g++ $(CFLAGS) -c tutorial02Wrapper.cpp

tutorial03Wrapper.o: tutorial03Wrapper.cpp tutorial03Wrapper.h
	g++ $(CFLAGS) -c tutorial03Wrapper.cpp

# run
run: all
	./$(EXE)

# Cleaning up afterwards
clean:
	rm -f $(OBJ)
