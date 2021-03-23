#Compiler
CXX=g++

# Header files

# Compiler Flags
CPPFLAGS=\
	-g \
	-I include/

EXE=OpenISS
OBJS=OISkeleton.o OIFace.o

all: $(OBJS)
	$(CXX) -o $(EXE) $(OBJS)

OISkeleton.o : OISkeleton.cpp include/OISkeleton.hpp
	$(CXX) -c $(CPPFLAGS) OISkeleton.cpp

OIFace.o : OIFace.cpp include/OIFace.hpp
	$(CXX) -c $(CPPFLAGS) OIFace.cpp