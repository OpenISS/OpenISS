SHELL:=/bin/bash

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

CAMTEST_EXE=freenect-camtest
CAMTEST_OBJ=$(CAMTEST_EXE).o

CHUNK_VIEW_EXE=freenect-chunkview
CHUNK_VIEW_OBJ=$(CHUNK_VIEW_EXE).o

HI_VIEW_EXE=freenect-hiview
HI_VIEW_OBJ=$(HI_VIEW_EXE).o

MIC_VIEW_EXE=freenect-micview
MIC_VIEW_OBJ=$(MIC_VIEW_EXE).o

REG_TEST_EXE=freenect-regtest
REG_TEST_OBJ=$(REG_TEST_EXE).o

REG_VIEW_EXE=freenect-regview
REG_VIEW_OBJ=$(REG_VIEW_EXE).o

WAVE_RECORD_EXE=freenect-waverecord
WAVE_RECORD_OBJ=$(WAVE_RECORD_EXE).o

PRELOAD="../../../../../libfreenect/lib/fakenect/libfakenect.so"
RECORDING_PATH="./sessions"

# Default target $(GL_EXE) $(CAMTEST_EXE) $(CHUNK_VIEW_EXE) do not work
all: $(GLPCL_EXE) $(HI_VIEW_EXE) $(MIC_VIEW_EXE) $(REG_TEST_EXE) $(REG_VIEW_EXE) $(WAVE_RECORD_EXE) 
	@echo "All is perfectly done!"

rungl: $(GL_EXE)
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(GL_EXE)
	@echo "Run successful"

runglpcl: $(GLPCL_EXE)
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(GLPCL_EXE)

runcamtest: $(CAMTEST_EXE)
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(CAMTEST_EXE)

runchunkview: $(CHUNK_VIEW_EXE)
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(CHUNK_VIEW_EXE)

runhiview:$(HI_VIEW_EXE)
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(HI_VIEW_EXE)

runmicview:$(MIC_VIEW_EXE)
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(MIC_VIEW_EXE)

runregtest:$(REG_TEST_EXE)
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(REG_TEST_EXE)

runregview:$(REG_VIEW_EXE)
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(REG_VIEW_EXE)

runwaverecord:$(WAVE_RECORD_EXE)
	LD_PRELOAD=$(PRELOAD) FAKENECT_PATH=$(RECORDING_PATH) ./$(WAVE_RECORD_EXE)

runfakenect:
	rm -rf sessions
	mkdir sessions
	cd ../../../../../libfreenect/bin && ./fakenect-record ../../src/api/c/examples/libfreenect/sessions

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

$(CAMTEST_EXE): $(CAMTEST_OBJ)
	$(CC) $(VFXLIBS) $(CAMTEST_OBJ) -lfreenect -lfreenect_sync -lGL -lglut -lGLU -lm -lpthread -o $(CAMTEST_EXE)
	export LD_LIBRARY_PATH=../../../../../libfreenect/lib/

$(CAMTEST_OBJ): camtest.c
	$(CC) $(CFLAGS) -c camtest.c -o $(CAMTEST_OBJ)
	export LD_LIBRARY_PATH=../../../../../libfreenect/lib/

$(CHUNK_VIEW_EXE): $(CHUNK_VIEW_OBJ)
	$(CC) $(VFXLIBS) $(CHUNK_VIEW_OBJ) -lfreenect -lfreenect_sync -lGL -lglut -lGLU -lm -lpthread -o $(CHUNK_VIEW_EXE)
	export LD_LIBRARY_PATH=../../../../../libfreenect/lib/

$(CHUNK_VIEW_OBJ): chunkview.c
	$(CC) $(CFLAGS) -c chunkview.c -o $(CHUNK_VIEW_OBJ)
	export LD_LIBRARY_PATH=../../../../../libfreenect/lib/

$(HI_VIEW_EXE): $(HI_VIEW_OBJ)
	$(CC) $(VFXLIBS) $(HI_VIEW_OBJ) -lfreenect -lfreenect_sync -lGL -lglut -lGLU -lm -lpthread -o $(HI_VIEW_EXE)

$(HI_VIEW_OBJ): hiview.c
	$(CC) $(CFLAGS) -c hiview.c -o $(HI_VIEW_OBJ)
	export LD_LIBRARY_PATH=../../../../../libfreenect/lib/

$(MIC_VIEW_EXE): $(MIC_VIEW_OBJ)
	$(CC) $(VFXLIBS) $(MIC_VIEW_OBJ) -lfreenect -lfreenect_sync -lGL -lglut -lGLU -lm -lpthread -o $(MIC_VIEW_EXE)
	export LD_LIBRARY_PATH=../../../../../libfreenect/lib/

$(MIC_VIEW_OBJ): micview.c
	$(CC) $(CFLAGS) -c micview.c -o $(MIC_VIEW_OBJ)
	export LD_LIBRARY_PATH=../../../../../libfreenect/lib/

$(REG_TEST_EXE): $(REG_TEST_OBJ)
	$(CC) $(VFXLIBS) $(REG_TEST_OBJ) -lfreenect -lfreenect_sync -lGL -lglut -lGLU -lm -lpthread -o $(REG_TEST_EXE)

$(REG_TEST_OBJ): regtest.c
	$(CC) $(CFLAGS) -c regtest.c -o $(REG_TEST_OBJ)
	export LD_LIBRARY_PATH=../../../../../libfreenect/lib/

$(REG_VIEW_EXE): $(REG_VIEW_OBJ)
	$(CC) $(VFXLIBS) $(REG_VIEW_OBJ) -lfreenect -lfreenect_sync -lGL -lglut -lGLU -lm -lpthread -o $(REG_VIEW_EXE)

$(REG_VIEW_OBJ): tiltdemo.c
	$(CC) $(CFLAGS) -c tiltdemo.c -o $(REG_VIEW_OBJ)
	export LD_LIBRARY_PATH=../../../../../libfreenect/lib/

$(WAVE_RECORD_EXE): $(WAVE_RECORD_OBJ)
	$(CC) $(VFXLIBS) $(WAVE_RECORD_OBJ) -lfreenect -lfreenect_sync -lGL -lglut -lGLU -lm -lpthread -o $(WAVE_RECORD_EXE)

$(WAVE_RECORD_OBJ): wavrecord.c
	$(CC) $(CFLAGS) -c wavrecord.c -o $(WAVE_RECORD_OBJ)
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
	rm -f $(CAMTEST_OBJ)
	rm -f $(CAMTEST_EXE)
	rm -f $(CHUNK_VIEW_OBJ)
	rm -f $(CHUNK_VIEW_EXE)
	rm -f $(HI_VIEW_OBJ)
	rm -f $(HI_VIEW_EXE)
	rm -f $(MIC_VIEW_OBJ)
	rm -f $(MIC_VIEW_EXE)
	rm -f $(REG_TEST_OBJ)
	rm -f $(REG_TEST_EXE)
	rm -f $(REG_VIEW_OBJ)
	rm -f $(REG_VIEW_EXE)
	rm -f $(WAVE_RECORD_OBJ)
	rm -f $(WAVE_RECORD_EXE)
