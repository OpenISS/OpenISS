#!/bin/bash -x

# build.sh
#
# Need to be root when running this script
#

# options
opencv_option="--opencv"
tinyosc_option="--tinyosc"
libfreenect_option="--freenect"
ofx_option="--ofx"
ogl_option="--ogl"
libfreenect2_option="--freenect2"

do_all=1
install_option="--install"
cleanup_option="--cleanup"
mode=0
system="el7"
el6_system="el6"
el7_system="el7"

# install/cleanup functions

function install_tinyosc()
{
	if [ "$(grep "tinyosc" build.cache)" != "tinyosc" ]; then
		#patch and compile tinyosc
		./dependencies/$system.sh --install --tinyosc
		pushd ../../tinyosc
			patch build.sh < ../src/scripts/dependencies/tinyosc.build.sh.patch
			./build.sh
		popd
		echo "tinyosc" >> build.cache
	else
 		echo "tinyosc already installed"
	fi
}

function cleanup_tinyosc()
{	
	if [ "$(grep "tinyosc" build.cache)" == "tinyosc" ]; then
		./dependencies/$system.sh --cleanup --tinyosc
		sed -i '/tinyosc/d' build.cache
		echo "tinyosc uninstalled"
	else
		echo "tinyosc not installed"
	fi
}

function install_open_frameworks()
{
	if [ "$(grep "openframeworks" build.cache)" != "openframeworks" ];
	then
		# install dependencies
		echo "running $system.sh $install_option $ofx_option"
		./dependencies/$system.sh $install_option $ofx_option

		# run install script to openFrameworks
		# nvidia driver must be present with OpenGL 4 support
		pushd ../../openFrameworks/scripts/linux
			which gcc
			whereis gcc
			gcc --version

			# have to use gcc 4.8 with c++11
			#scl enable devtoolset-2 bash
			. /opt/rh/devtoolset-2/enable
				# tells scripts to use 3 cpu cores compile
				./compileOF.sh -j3
				./compilePG.sh
			#exit 0
  
			# compile examples
			cd ../../
			ls -al
			find . -name projectGenerator -ls
			./projectGenerator -r -o"." examples
		popd
		echo "openframeworks" >> build.cache
	else
		echo "openframeworks already installed"
	fi
}

function cleanup_open_frameworks()
{
	if [ "$(grep "openframeworks" build.cache)" == "openframeworks" ];
	then
		./dependencies/$system.sh --cleanup --ofx
		sed -i '/openframeworks/d' build.cache
		echo "openframeworks cleanup complete"
	else
		echo "openframeworks is not installed"
	fi
}

function install_libfreenect2()
{
	if [ "$(grep "libfreenect2" build.cache)" != "libfreenect2" ]
	then
		#install deps
		./dependencies/$system.sh --install --freenect2

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
}

function cleanup_libfreenect2()
{
	if [ "$(grep "libfreenect2" build.cache)" == "libfreenect2" ]
        then
		./dependencies/$system.sh --cleanup --freenect2
		sed -i '/libfreenect2/d' build.cache
		echo "cleaned libfreenect2"
	else
		echo "libfreenect2 not installed"
	fi
}

function install_opencv()
{
	if [ "$(grep "opencv" build.cache)" != "opencv" ]
	then
		./dependencies/$system.sh --install --opencv

		# compile opencv
		# TODO: add examples compilation
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
}

function cleanup_opencv()
{
	if [ "$(grep "opencv" build.cache)" == "opencv" ]
	then
		# compile opencv
		./dependencies/$system.sh --cleanup --opencv
		sed -i '/opencv/d' ./build.cache
		echo "opencv removed"
	else
		echo "opencv not installed"
	fi
}

function install_ogl()
{
	# check if we need to install
	if [ "$(grep "ogl" build.cache)" != "ogl" ]; then
		# call the function in &system.sh to install dependencies
		./dependencies/$system.sh --install --ogl

        	pushd ../../ogl
			rm -rf build
			mkdir build && cd build
			cmake3 ..
			make
        	popd
        	echo "ogl" >> build.cache
	else
		# else don't bother
		echo "ogl already installed"
	fi
}

function cleanup_ogl()
{
	# check if we need to uninstall
	if [ "$(grep "ogl" build.cache)" == "ogl" ]; then
		# call the function in $system.sh to cleanup dependencies
		./dependencies/$system.sh --cleanup --ogl

		# remove the line from build.cache
		sed -i '/ogl/d' build.cache	

		# except it really isn't
		echo "cleaned ogl"
	else
		# else we don't need to uninstall it
		echo "ogl not installed"
	fi
}

function install_libfreenect()
{
	if [ "$(grep "libfreenect_" build.cache)" != "libfreenect_" ]
	then
		./dependencies/$system.sh --install --freenect
		#run cmake and make files for libfreenect
		pushd ../../libfreenect
			mkdir build && cd build
			# TODO: BUILD_OPENNI2_DRIVER=ON would work with cmake3 and gcc 4.8+ once installed
			# TODO: add patch and flag for fwfetcher.py to load firmware for 1473 models, etc.
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
	if [ "$(grep "libfreenect_" build.cache)" == "libfreenect_" ];
	then
		./dependencies/$system.sh --cleanup --freenect
		#uninstall libfreenect
		pushd ../../libfreenect/build
			make uninstall
			cd ../
			rm -rf build

			#remove links created by libfreenect
			rm -f /usr/local/lib/libfreenect*
			rm -rf /usr/local/lib/fakenect
		popd

		echo "libfreenect uninstalled"
		sed -i '/libfreenect_/d' build.cache
	else
		echo "libfreenect is not installed"
	fi
}

#
# main
#

echo "============="
echo "$0 STARTED"
date
echo "============="

if [ ! -e "build.cache" ]
then
	touch build.cache
fi

# for loop to parse input to figure out what we're doing
for current_option in "$@"
do
	#Install or clean inputs
	# find out whether or not we're running install or cleanup
	if [ "$current_option" == "$install_option" ]; then
		mode=$install_option
	elif [ "$current_option" == "$cleanup_option" ]; then
		mode=$cleanup_option

	#system inputs
	# in case we want to be able to install to a different system
	elif [ "$current_option" == "$el6_system" ]; then
		system=$el6_system
	elif [ "$current_option" == "$el7_system" ]; then
		system=$el7_system

	#Specific install options
	# according to mode, do something with the inputted program
	elif [ "$current_option" == "$libfreenect2_option" ]; then
		libfreenect2_option=1
		do_all=0
	elif [ "$current_option" == "$opencv_option" ]; then
		opencv_option=1
		do_all=0
	elif [ "$current_option" == "$tinyosc_option" ]; then
		tinyosc_option=1
		do_all=0
	elif [ "$current_option" == "$ofx_option" ]; then
		ofx_option=1
		do_all=0
	elif [ "$current_option" == "$libfreenect_option" ]; then
		libfreenect_option=1	
		do_all=0
	elif [ "$current_option" == "$ogl_option" ]; then
		ogl_option=1
		do_all=0
	fi
done

# Ifs to parse selected inputs
# check if our options have been affected or we're doing all
if [ "$tinyosc_option" == "1" -o "$do_all" == "1" ]; then
	if [ "$mode" == "$install_option" ]; then
		time install_tinyosc
	elif [ "$mode" == "$cleanup_option" ]; then
		time cleanup_tinyosc
	fi
fi

if [ "$ogl_option" == "1" -o "$do_all" == "1" ]; then
	# call install ogl function - Matthew Roy
	if [ "$mode" == "$install_option" ]; then
		time install_ogl
	# call cleanup ogl function - Matthew Roy
	elif [ "$mode" == "$cleanup_option" ]; then
		time cleanup_ogl
	fi
fi

if [ "$libfreenect2_option" == "1" -o "$do_all" == "1" ]; then
	if [ "$mode" == "$install_option" ]; then
		time install_libfreenect2
	elif [ "$mode" == "$cleanup_option" ]; then
		time cleanup_libfreenect2
	fi
fi

if [ "$libfreenect_option" == "1" -o "$do_all" == "1" ]; then
	if [ "$mode" == "$install_option" ]; then
		time install_libfreenect
	elif [ "$mode" == "$cleanup_option" ]; then
		time cleanup_libfreenect
	fi
fi

if [ "$opencv_option" == "1" -o "$do_all" == "1" ]; then
	if [ "$mode" == "$install_option" ]; then
		time install_opencv
	elif [ "$mode" == "$cleanup_option" ]; then
		time cleanup_opencv
	fi
fi

if [ "$ofx_option" == "1" -o "$do_all" == "1" ]; then
	if [ "$mode" == "$install_option" ]; then
		time install_open_frameworks
	elif [ "$mode" == "$cleanup_option" ]; then
		time cleanup_open_frameworks
	fi
fi

echo "============="
echo "$0 DONE"
date
echo "============="

# EOF
