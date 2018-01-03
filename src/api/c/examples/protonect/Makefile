# basic makefile modified from atsm/examples/c/call-cpp-from-c/example01
#

all: protonect
	@echo "CProtonect compiled."

protonect: CProtonect.o CProtonectWrapper.o
	gcc *.o -lstdc++ -lfreenect2 -o protonect

CProtonect.o: CProtonect.c CProtonectWrapper.h Makefile
	gcc -c CProtonect.c -o CProtonect.o

CProtonectWrapper.o: CProtonectWrapper.cpp CProtonectWrapper.h
	g++ -c CProtonectWrapper.cpp
