#!/bin/bash -x

# build.sh
#
# Need to be root when running this script
#
# CSI-230 Fall 2016
#   Brian Baron, Colin Brady, Robert Gentile
#   Justin Mulkin, Gabriel Pereyra, Duncan Carrol, Lucas Spiker
#

# worked on by Alex Rader, Cory Smith, Nicholas Robbins


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
system="el6"
el6_system="el6"

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
		./dependencies/$system.sh --install --tinyosc
		sed -i '/tinyosc/d' build.cache
		echo "tinyosc uninstalled"
	else
		echo "tinyosc not installed"
	fi
}

function installOpenFrameworks()
{
	if [ "$(grep "openframeworks" build.cache)" != "openframeworks" ];
	then
		#install dependencies
		echo "running el6.sh"
		./dependencies/$system.sh $cleanup_option $ofx_option
		echo "openframeworks" >> build.cache

		#run install script to openframeworks
		pushd ../../openFrameworks/scripts/linux
		#tells scripts to use 3 cpu cores compile
		#./compileOF.sh -j3
		popd
		echo "openframeworks" >> build.cache
	else
		echo "openframeworks already installed"
	fi
}

install_opencv()
{
        if [ "$(grep "opencv" build.cache)" != "opencv" ]
        then
		./dependencies/$system.sh --install --opencv

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
}

cleanup_opencv()
{

	if [ "$(grep "opencv" build.cache)" == "opencv" ]
        then
                # compile opencv
                ./dependencies/$system.sh --cleanup --opencv
        	sed -i '/foo/d' ./build.cache
		echo "opencv removed"
        else
                echo "opencv not installed"
        fi
}

function cleanOpenFrameworks()
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

function install_libfreenect()
{
        if [ "$(grep "libfreenect_" build.cache)" != "libfreenect_" ]
        then
		./dependencies/$system.sh --install --freenect
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
	if [ "$(grep "libfreenect_" build.cache)" == "openframeworks" ];
	then
		./dependencies/$system.sh --cleanup --freenect
        	#uninstall libfreenect
       		cd ../../../libfreenect/build
        	make uninstall
        	cd ../
        	rm -rf build

       		#remove links created by libfreenect
        	rm -f /usr/local/lib/libfreenect*
        	rm -rf /usr/local/lib/fakenect

		echo "libfreenect uninstalled"
	else
		echo "libfreenect is not installed"
	fi
}

# install ogl - Matthew Roy
function install_ogl()
{
	# check if we need to install
	if [ "$(grep "ogl" build.cache)" != "ogl" ]; then
		# call the function in &system.sh to install dependencies
		./dependencies/$system.sh --install --ogl

        	pushd ../../ogl
        	rm -rf build
        	mkdir build && cd build
        	cmake ..
        	make
        	popd
        	echo "ogl" >> build.cache
	else
		# else don't bother
		echo "ogl already installed"
	fi
}

# cleanup ogl - Matthew Roy
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

function install_libfreenect2()
{
	#install deps
	./dependencies/$system.sh --install --freenect2

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


if [ ! -e "build.cache" ]
then
	touch build.cache
fi

# figure out what we're doing
for var in "$@"
do
	# find out whether or not we're running install or cleanup
	if [ $var == $install_option ]; then
		mode=$install_option
	elif [ $var == $cleanup_option ]; then
		mode=$cleanup_option

	# in case we want to be able to install to a different system
	elif [ $var == $el6_system ]; then
		system=$el6_system

	# according to mode, do something with the inputted program
	elif [ $var == $libfreenect2_option ]; then
		libfreenect2_option=1
		do_all=0
	elif [ $var == $opencv_option ]; then
		opencv_option=1
		do_all=0
	elif [ $var == $tinyosc_option ]; then
		tinyosc_option=1
		do_all=0
	elif [ $var == $ofx_option ]; then
		ofx_option=1
		do_all=0
	elif [ $var == $libfreenect_option ]; then
		libfreenect_option=1	
		do_all=0
	elif [ $var == $ogl_option ]; then
		ogl_option=1	
		do_all=0
	fi
done

if [ $tinyosc_option == 1 -o $do_all == 1 ]; then
	if [ $mode == $install_option ]; then
		install_tinyosc
	elif [ $mode == $cleanup_option ]; then
		cleanup_tinyosc
	fi
fi

if [ $ofx_option == 1 -o $do_all == 1 ]; then
	if [ $mode == $install_option ]; then
		installOpenFrameworks
	elif [ $mode == $cleanup_option ]; then
		cleanOpenFrameworks
	fi
fi

if [ $libfreenect_option == 1 -o $do_all == 1 ]; then
	if [ $mode == $install_option ]; then
		install_libfreenect
	elif [ $mode == $cleanup_option ]; then
		cleanup_libfreenect
	fi
fi

if [ $libfreenect_option == 1 -o $do_all == 1 ]; then
	# call install ogl function - Matthew Roy
	if [ $mode == $install_option ]; then
		install_ogl
	# call cleanup ogl function - Matthew Roy
	elif [ $mode == $cleanup_option ]; then
		cleanup_ogl
	fi
fi

# check if our option has been affected or we're doing all
if [ ogl_option == 1 -o do_all == 1 ]; then
	if [ $mode == $install_option ]; then
		install_ogl
	elif [ $mode == $cleanup_option]; then
		cleanup_ogl
fi

if [ $opencv_option == 1 -o $do_all == 1 ]; then
	if [$mode == $install_option ]; then
		install_opencv
	if [$mode == $cleanup_option ]; then
		cleanup_opencv
	fi
fi

if [ $libfreenect2_option == 1 -o $do_all == 1 ]; then
	if [ $mode == $install_option ]; then
		install_libfreenect2
	elif [ $mode == $cleanup_option ]; then
		cleanup_libfreenect2
	fi
fi
