
CSI230-101 Fall 2016 Teams:

	- Brian Baron
	- Colin Brady
	- Robert Gentile

	- Gabriel Pereyra
	- Lucas Spiker
	- Duncan Carrol
	- Justin Mulkin
	
Build Instructions:
	- In /src/scripts/dependencies, find the script applicable to your OS and run it
		this will install everything needed to compile the project
	- Run build.sh in /src/scripts adding the platform as an argument (for example: "./build.sh el6") 
		this builds the project's 3rd party components, and runs the platform dependencies script if needed
	- Run build.sh with "--cleanup" in order to clear unnecessary files and remove dependencies
	
Currently Known Bugs:
	- Cannot build openframeworks as they are currently having some issues with their linux version
		According to their repo they know of the issue and are working on it

Team 1: libfreenect2, and opencv

Instructions:
	Install:	cd to /OpenISS/src
				make (installs all dependencies)

	Compile:	Happens during make process

	Clean:		cd to /OpenISS/src
				make clean
Bugs:
N/A

Special Features:
N/A