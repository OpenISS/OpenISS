#!/bin/bash -x

# build.sh
#
# Need to be root when running this script
#
# CSI-230 Fall 2016
#   Brian Baron, Colin Brady, Robert Gentile
#   Justin Mulkin, Gabriel Pereyra, Duncan Carrol, Lucas Spiker
#

if [ "$1" == "el6" ]; then

	if [ ! -e "build.cache" ]
	then
		touch build.cache
	fi

	if [ "$(grep "el6-dependencies" build.cache)" != "el6-dependencies" ]
	then
		#install dependencies
		echo "running el6.sh"
		./dependencies/el6.sh --install
		echo "el6-dependencies" >> build.cache
	else
		echo "el6-dependencies already installed"
	fi

	if [ "$(grep "libfreenect2" build.cache)" != "libfreenect2" ]
	then
		# compile the libfreenect2 stuff
		pushd ../../libfreenect2
		rm -rf build
		mkdir build && cd build
		cmake -L ..
		make install
		popd
		echo "libfreenect2" >> build.cache
	else
		echo "libfreenect2 already installed"
	fi		

	if [ "$(grep "opencv" build.cache)" != "opencv" ]
	then
		# compile opencv
		pushd ../../opencv
		rm -rf build
		mkdir build && cd build
		cmake ..
		make
		popd
		echo "opencv" >> build.cache
	else
		echo "opencv already installed"
	fi

	if [ "$(grep "tinyosc" build.cache)" != "tinyosc" ]
	then
		#patch and compile tinyosc
		pushd ../../tinyosc
		patch build.sh < ../src/scripts/dependencies/tinyosc.build.sh.patch
		./build.sh
		popd
		echo "tinyosc" >> build.cache
	else
		echo "tinyosc already installed"
	fi

	if [ "$(grep "libfreenect_" build.cache)" != "libfreenect_" ]
	then
		#run cmake and make files for libfreenect
		pushd ../../libfreenect
		mkdir build && cd build
		cmake -L ..
		make
		make install
		popd
		echo "libfreenect_" >> build.cache
	else
		echo "libfreenect already installed"
	fi

	if [ "$(grep "openframeworks" build.cache)" != "openframeworks" ]
	then
		#run install script to openframeworks
		cd ../../openFrameworks/scripts/linux
		#tells scripts to use 3 cpu cores compile

		#cant actually compile openframeworks as 11/14/16 becuase github master branch doesn't build
		#but it looks like theyre working on fixing

		#./compileOF.sh -j3
		echo "openframeworks" >> build.cache
	else
		echo "openframeworks already installed"
	fi

elif [[ "$1" == "--cleanup" ]]; then
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
fi
