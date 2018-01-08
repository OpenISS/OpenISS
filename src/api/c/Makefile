# Compiler and compiler flags
CC=gcc

# 	-Wall -ansi -pedantic -Wmissing-declarations -Wmissing-prototypes \

CFLAGS=\
	-g \
	-D__EXTENSIONS__ -D_GNU_SOURCE \
	-I include \
	-I ../../../ogl/external/glm-0.9.7.1 \
	-I ../../../ogl/external/glew-1.13.0/include \
	-I ../../../ogl/external/glfw-3.1.2/include/GLFW \
	-I ../../../libfreenect/include \
	-I ../../../libfreenect/wrappers/c_sync

# A list of executable, object, and library files
EXE=OpenISS
OBJ=$(EXE).o pipeline.o vfx.o GL2Viewer.o kinect1.o
VFX=vfx/Kinect1Depth.o vfx/Kinect1RGB.o

VFXLIBS=\
	-L ../../../ogl/build/external/glfw-3.1.2/src \
	-L ../../../ogl/build/external \
	-L ../../../libfreenect/build/lib

GFXLIBS=\
	-lglfw3 -lGLEW_1130 -lANTTWEAKBAR_116_OGLCORE_GLFW -lstdc++ \
	-lXxf86vm -lXcursor -lpthread -lrt -lX11 -lXrandr -lXi -lXinerama -lm -ldl \
	-lGL -lGLU -lGLEW -lglut

LIBFREENECT=../../../libfreenect/build/lib/libfreenect.so.0.6
LIBFREENECTPATH=../../../libfreenect/build/lib

FREENECTLIBS=-lfreenect -lfreenect_sync

# Default target
all: $(EXE)
	make -C examples $@
	make -C vfx $@
	@echo "All is perfectly done!"

# Other subtargets to make it happen
$(EXE): $(OBJ) Makefile
	$(CC) $(VFXLIBS) $(OBJ) $(VFX) $(GFXLIBS) $(FREENECTLIBS) $(LIBFREENECT) -o $(EXE)

%.o: %.c %.h
	$(CC) $(CFLAGS) -c $<

vfx-test: vfx.c include/vfx.h
	$(CC) $(CFLAGS) -DMAIN_VFX_TEST -c vfx.c
	$(CC) $(VFXLIBS) vfx.o $(GFXLIBS) -o $@

vfx.o: vfx.c include/vfx.h
	$(CC) $(CFLAGS) -c vfx.c
	make -C vfx all

viewers: GL2Viewer.o kinect1.o

GL2Viewer.o: GL2Viewer.c kinect1.c include/vfx.h
	$(CC) $(CFLAGS) -c GL2Viewer.c

kinect1.o: kinect1.c include/kinect1.h include/vfx.h
	$(CC) $(CFLAGS) -c kinect1.c

issimgdrv:
	make -C issimagedrv all

PRELOAD="../../../libfreenect/build/lib/fakenect/libfakenect.so"
RECORDING_PATH="./test-recording"

run: $(EXE)
# 	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(EXE) --gl2
	export LD_LIBRARY_PATH=../../../libfreenect/build/lib
#	LD_PRELOAD=$(PRELOAD) LD_LIBRARY_PATH=$(LIBFREENECTPATH) FAKENECT_PATH=$(RECORDING_PATH) ./$(EXE) --gl2
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(EXE) --gl2
	@echo "Run successful"

# Cleaning up afterwards
clean:
	rm -f $(OBJ) *.o

# Tmp rules, nearly subsumed by build.sh and el6.sh

vfx-deps: ogl

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

