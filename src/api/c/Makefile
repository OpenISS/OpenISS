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
OBJ=$(EXE).o pipeline.o

VFXLIBS=\
	-L ../../../ogl/build/external/glfw-3.1.2/src \
	-L ../../../ogl/build/external
	#-L ../../../ogl/build/external/glfw-3.1.2/src/libglfw3.a

# Default target
all: $(EXE)
	make -C examples $@
	@echo "All is perfectly done!"

# Other subtargets to make it happen
$(EXE): $(OBJ)
	$(CC) $(OBJ) -o $(EXE)

%.o: %.c %.h
	$(CC) $(CFLAGS) -c $<

vfx-deps: ogl

vfx: vfx.c include/vfx.h
	$(CC) $(VFXLIBS) -lglfw3 -lGLEW_1130 $(CFLAGS) -DMAIN_VFX_TEST -c vfx.c
# 	$(CC) $(VFXLIBS) $(CFLAGS) \
# 		../../../ogl/build/external/glfw-3.1.2/src/libglfw3.a \
# 		../../../ogl/build/external/libGLEW_1130.a \
# 		../../../ogl/build/external/libANTTWEAKBAR_116_OGLCORE_GLFW.a \
# 		-lstdc++ \
# 		vfx.o -o $@
	#$(CC) $(VFXLIBS) -lglfw3 -lGLEW_1130 -lANTTWEAKBAR_116_OGLCORE_GLFW -lstdc++ vfx.o -o $@
	$(CC) $(VFXLIBS) -lglfw3 -lGLEW_1130 -lANTTWEAKBAR_116_OGLCORE_GLFW -lstdc++ vfx.o -o $@

vfx.o: vfx.c include/vfx.h
	$(CC) $(CFLAGS) -c $<

issimgdrv:
	make -C issimagedrv all


# Cleaning up afterwards
clean:
	rm -f $(OBJ)

# Tmp rules, nearly subsumed by build.sh and el6.sh

deps-clean:
	rm -f epel cmake3


ogl: cmake3
	cd ../../../ogl/ ; \
		mkdir build ; \
		cd build ; \
		cmake3 .. ;  \
		make 

epel:
	yum -y install epel-release
	touch epel

cmake3: epel
	yum -y --enablerepo=epel install cmake3
	touch cmake3

