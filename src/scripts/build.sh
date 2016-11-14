#!/bin/bash

if [[ $1 == "el6" ]]; then
	#install dependencies
	cd ./dependencies
	./el6.sh

	#run cmake and make files for libfreenect
	cd ../../../libfreenect
	mkdir build && cd build
	cmake -L ..
	make
	make install

	#run install script to openframeworks
	cd ../../openFrameworks/scripts/linux
	#tells scripts to use 3 cpu cores compile

	#cant actually compile openframeworks as 11/14/16 becuase github master branch doesn't build
	#but it looks like theyre working on fixing

	#./compileOF.sh -j3
fi

if [[ $1 == "--cleanup" ]]; then
	#uninstall libusb
	cd ./dependencies
	cd libusb-1.0.20
	make uninstall
	cd ../
	rm -rf libusb-1.0.20 libusb.tar.bz2

	#uninstall libfreenect
	cd ../../../libfreenect/build
	make uninstall
	cd ../
	rm -rf build

	#remove links created by libfreenect
	rm -f /usr/local/lib/libfreenect*
	rm -rf /usr/local/lib/fakenect

	#remove packages installed by yum
	yum remove -y libXmu-devel libXi-devel glut-devel libudev-devel gstreamer-devel gstreamer-plugins-base-devel
fi