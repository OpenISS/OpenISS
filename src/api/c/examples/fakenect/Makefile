# Compiler and compiler flags

CC=gcc
CFLAGS=\
	-g\
	-D__EXTENSIONS__ -D_GNU_SOURCE \
	-I ../../../../../ogl/external/glm-0.9.7.1 \
        -I ../../../../../ogl/external/glew-1.13.0/include \
        -I ../../../../../ogl/external/glfw-3.1.2/include/GLFW \
	-I ../../../../../libfreenect/include \
	-I ../../../../../libfreenect/wrappers/c_sync

VFXFLAGS=\
	../../../../../ogl/build/external/glfw-3.1.2/src/libglfw3.a \
	../../../../../ogl/build/external/libGLEW_1130.a

VFXLIBS=\
	-L ../../../../../libfreenect/build/lib/ \
	 ../../../../../libfreenect/build/lib/libfreenect.so.0.6
#        -L ../../../../../ogl/build/external/glfw-3.1.2/src \
#        -L ../../../../../ogl/build/external

# -Wall -ansi -pedantic -Wmissing-declarations -Wmissing-prototypes \ 

# A list of executable, object, and library files
GL_EXE=freenect-glview
GL_OBJ=$(GL_EXE).o

GLPCL_EXE=freenect-glpclview
GLPCL_OBJ=$(GLPCL_EXE).o

PRELOAD="../../../../../libfreenect/lib/fakenect/libfakenect.so"
RECORDING_PATH="./sessions"

# Default target
all: $(GL_EXE) $(GLPCL_EXE)
	@echo "All is perfectly done!"

rungl: $(GL_EXE)
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(GL_EXE)
	@echo "Run successful"

runglpcl: $(GLPCL_EXE)
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(GLPCL_EXE)

runfakenect:
	rm -rf sessions
	mkdir sessions
	cd ../../../../../libfreenect/bin && ./fakenect-record ../../src/api/c/examples/fakenect/sessions
	
# Other subtargets to make it happen
$(GL_EXE): $(GL_OBJ)
	$(CC) $(VFXLIBS) $(GL_OBJ) -lfreenect -lGL -lglut -lm -lpthread -o $(GL_EXE)

$(GL_OBJ): glview.c
	$(CC) $(CFLAGS) -c glview.c -o $(GL_OBJ)
	 export LD_LIBRARY_PATH=../../../../../libfreenect/lib/

$(GLPCL_EXE): $(GLPCL_OBJ)
	$(CC) $(VFXLIBS) $(GLPCL_OBJ) -lfreenect -lfreenect_sync -lGL -lglut -lGLU -lm -lpthread -o $(GLPCL_EXE)


$(GLPCL_OBJ): glpclview.c
	$(CC) $(CFLAGS) -c glpclview.c -o $(GLPCL_OBJ)
	 export LD_LIBRARY_PATH=../../../../../libfreenect/lib/

cmakeFreenect:
	cd ../../../../../libfreenect
	cmake -DLIBUSB_1_LIBRARY=../../libfreenect2/depends/libusb/lib/libusb-1.0.so -DLIBUSB_1_INCLUDE_DIR=../../libfreenect2/depends/libusb/include/libusb-1.0 -DBUILD_OPENNI2_DRIVER=OFF -L ..

# Cleaning up afterwards
clean:
	rm -f $(GL_OBJ)
	rm -f $(GL_EXE)
	rm -f $(GLPCL_OBJ)
	rm -f $(GLPCL_EXE)
