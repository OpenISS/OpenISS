ROJECT = visualizer_linux
SOURCES = $(wildcard src/*.cpp)
OBJECTS = $(SOURCES:.cpp=.o)
CFLAGS  = -c -O2 -Wall -pedantic
INCLUDES = -I./fmod_include
#LIBRARIES = `pkg-config --libs libglfw` -lGLEW -lGL ./libfmodex64-4.44.32.so
#LIBRARIES = -l../../../../../../ogl/external/glew-1.13.0/include/GL -l../../../../../../ogl/external/glfw-3.1.2/include/GLFW ./libfmodex64-4.44.32.so
LIBRARIES = ../../../../../../ogl/builds/external/glfw-3.1.2/src/libglfw3.a -lGLEW -lGL -lglfw3 ./libfmodex64-4.44.32.so

all: $(PROJECT)
	echo built
%.o: %.cpp
	g++ $(CFLAGS) $(INCLUDES) $< -o $@

$(PROJECT): $(OBJECTS)
	g++ -s $(OBJECTS) -o $(PROJECT) $(LIBRARIES)

clean:
	rm $(OBJECTS) -f

