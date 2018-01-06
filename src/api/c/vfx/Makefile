# Compiler and compiler flags
CC=gcc

# 	-Wall -ansi -pedantic -Wmissing-declarations -Wmissing-prototypes \

CFLAGS=\
	-g \
	-D__EXTENSIONS__ -D_GNU_SOURCE \
	-I ../include \
	-I ../../../../ogl/external/glm-0.9.7.1 \
	-I ../../../../ogl/external/glew-1.13.0/include \
	-I ../../../../ogl/external/glfw-3.1.2/include/GLFW \
	-I ../../../../libfreenect/include \
	-I ../../../../libfreenect/wrappers/c_sync

# A list of executable, object, and library files
EXE=
OBJ=Kinect1Depth.o Kinect1RGB.o

VFXLIBS=\
	-L ../../../../ogl/build/external/glfw-3.1.2/src \
	-L ../../../../ogl/build/external \
	-L ../../../../libfreenect/build/lib

GFXLIBS=\
	-lglfw3 -lGLEW_1130 -lANTTWEAKBAR_116_OGLCORE_GLFW -lstdc++ \
	-lXxf86vm -lXcursor -lpthread -lrt -lX11 -lXrandr -lXi -lXinerama -lm -ldl \
	-lGL -lGLU -lGLEW -lglut

FREENECTLIBS=-lfreenect -lfreenect_sync

# Default target
all: $(OBJ)
	@echo "All is perfectly done!"

# Other subtargets to make it happen
$(EXE): $(OBJ)
	$(CC) $(VFXLIBS) $(OBJ) $(GFXLIBS) $(FREENECTLIBS) -o $(EXE)

%.o: %.c %.h
	$(CC) $(CFLAGS) -c $<

Kinect1Depth.o: Kinect1Depth.c ../include/kinect1.h ../include/vfx.h
	$(CC) $(CFLAGS) -c Kinect1Depth.c

Kinect1RGB.o: Kinect1RGB.c ../include/kinect1.h ../include/vfx.h
	$(CC) $(CFLAGS) -c Kinect1RGB.c

# Cleaning up afterwards
clean:
	rm -f $(OBJ)

