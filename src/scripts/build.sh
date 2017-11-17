#!/bin/bash -x

# build.sh
#
# Need to be root when running this script
#
# CSI-230 Fall 2016
#   Brian Baron, Colin Brady, Robert Gentile
#   Justin Mulkin, Gabriel Pereyra, Duncan Carrol, Lucas Spiker
#

ogl_option = "--ogl"

# install ogl - Matthew Roy
function install_ogl()
{
	#compile ogl
        pushd ../../ogl
        rm -rf build
        mkdir build && cd build
        cmake ..
        make
        popd
        echo "ogl" >> build.cache
}

# cleanup ogl - Matthew Roy
function cleanup_ogl()
{
		
}

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
		# XXX: BUILD_OPENNI2_DRIVER=ON would work with cmake3 and gcc 4.8+ once installed
		cmake \
			-DLIBUSB_1_LIBRARY=../../libfreenect2/depends/libusb/lib/libusb-1.0.so \
			-DLIBUSB_1_INCLUDE_DIR=../../libfreenect2/depends/libusb/include/libusb-1.0 \
			-DBUILD_OPENNI2_DRIVER=OFF \
			-L ..
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
		pushd ../../openFrameworks/scripts/linux
		#tells scripts to use 3 cpu cores compile

		#cant actually compile openframeworks as 11/14/16 becuase github master branch doesn't build
		#but it looks like theyre working on fixing

		#./compileOF.sh -j3
		popd
		echo "openframeworks" >> build.cache
	else
		echo "openframeworks already installed"
	fi

	# call install ogl function - Matthew Roy
	for var in "$@"
	do
		if [ $var == $ogl_option ]; then
			install_ogl		
		fi
	done 

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

	# call cleanup ogl function - Matthew Roy
        for var in "$@"
        do
                if [ $var == $ogl_option ]; then
                        cleanup_ogl
                fi
        done
fi
