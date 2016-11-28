#!/bin/bash

# build.sh
# Brian Baron, Colin Brady, Robert Gentile
# Justin, Gabriel Pereyra, Duncan, Lucas Spiker
# CSI-230
#
# Need to be root when running this script

if [[ $1 == "el6" ]]; then
	#install dependencies
	echo "running el6.sh"
	./dependencies/el6.sh --install

	# compile the libfreenect2 stuff
	pushd ../../libfreenect2
	rm -rf build
	mkdir build && cd build
	cmake -L ..
	make install

	# compile opencv
	cd ../../opencv
	rm -rf build
	mkdir build && cd build
	cmake ..
	make

	popd

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
elif [[ $1 == "--cleanup" ]]; then
	./dependencies/el6.sh --cleanup

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
