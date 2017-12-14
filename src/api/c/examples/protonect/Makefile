# basic makefile modified from atsm/examples/c/call-cpp-from-c/example01
#

# TODO: include and library flags for libreenect2

all: #protonect
	@echo "Uncomment protonect when compiles."

protonect: CProtonect.o CProtonectWrapper.o
	gcc *.o -lstdc++ -o protonect

CProtonect.o: CProtonect.c CProtonectWrapper.h Makefile
	gcc -c CProtonect.c -o CProtonect.o

CProtonectWrapper.o: CProtonectWrapper.cpp CProtonectWrapper.h
	g++ -c CProtonectWrapper.cpp
