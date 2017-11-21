#!/bin/bash

# build.sh
#
# Need to be root when running this script
#
# CSI-230 Fall 2016
#   Brian Baron, Colin Brady, Robert Gentile
#   Justin Mulkin, Gabriel Pereyra, Duncan Carrol, Lucas Spiker
#

tinyosc_option="--tinyosc"
ogl_option="--ogl"
opencv_option="--opencv"
libfreenect_option="--freenect"
libfreenect2_option="--freenect2"

install_option="--install"
cleanup_option="--cleanup"
mode=0
do_all=0
system="el6"
el6_system="el6"

#alex
function install_tinyosc()
{
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
}
#alex
function cleanup_tinyosc()
{
	echo "nothing to uninstall to small"
}

#Nick
function installOpenFrameworks()
{
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
}

#Nick
function installOpenFrameworksDeps()
{
	if [ "$(grep "openframeworks" build.cache)" != "openframeworks" ]
	then
		#install dependencies
		echo "running el6.sh"
		./dependencies/el6.sh --ofx
		echo "openframeworks" >> build.cache
	else
		echo "openframeworks already installed"
	fi
}

function install_libfreenect()
{
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

}

function cleanup_libfreenect()
{

        #uninstall libfreenect
        cd ../../../libfreenect/build
        make uninstall
        cd ../
        rm -rf build

        #remove links created by libfreenect
        rm -f /usr/local/lib/libfreenect*
        rm -rf /usr/local/lib/fakenect

	echo "libfreenect uninstalled"
}

if [ ! -e "build.cache" ]
then
	touch build.cache
fi


for var in "$@"
do
	if [ $var == $install_option ]; then
		mode=$install_option		
	elif [ $var == $cleanup_option ]; then
		mode=$cleanup_option
	elif [ $var == $libfreenect_option ]; then
		libfreenect_option=1	
		do_all=0
	elif [ $var == $el6_system ]; then
		system=$el6_system
	fi
done 


if [ $libfreenect_option == 1 -o $do_all == 1 ]; 
then
	if [ $mode == $install_option ]; 
	then
		install_libfreenect
	elif [ $mode == $cleanup_option ];
	then
		cleanup_libfreenect
	fi
fi

