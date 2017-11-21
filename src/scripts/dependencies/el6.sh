#!/bin/bash -x

# el6.sh
#
# Script to install requirements:
#		libraries
#		compilers
#		kernels
#		drivers
#
# These are all needed to compile submodules in OpenISS
#
# Need to be root when running this script

# worked on by Alex Rader, Cory Smith, Nicholas Robbins


tinyosc_option="--tinyosc"
ofx_option="--ofx"
opencv_option="--opencv"
install_option="--install"
cleanup_option="--cleanup"
mode=0

function install_tinyosc()
{
	yum install -y gcc
	echo "tinyosc dependencies installed"
}
function cleanup_tinyosc()
{
	echo "unistalled tinyosc"
}

function installOpenFrameworks()
{
	 # openFrameworks
        pushd ../../openFrameworks
        	git submodule update --init --recursive
	popd
        # openframeworks dependencies from the provided script
        pushd ../../openFrameworks/scripts/linux/el6
                ./install_codecs.sh
                ./install_dependencies.sh
	popd
}

function cleanOpenFrameworks()
{
	echo "openframeworks el6 cleanup complete"
}

# el6.sh - dependencies for ogl - Matthew Roy
function install_ogl_deps()
{
	# dependencies for ogl
	yum install -y cmake3 make gcc-c++ libX11-devel libXi-devel mesa-libGL mesa-libGLU libXrandr-devel libXext-devel libXcursor-devel libXinerama-devel libXi-devel
}

# el6.sh - dependencies for ogl - Matthew Roy
function cleanup_ogl_deps()
{
	# dependencies for ogl
	echo "cleaned ogl"
}

function install_opencv()
{
	# opencv dependencies
        yum groupinstall -y "Development Tools"
        yum install -y gtk+-devel gtk2-devel
        yum install -y pkgconfig.x86_64
        yum install -y python
        yum install -y numpy
        yum install -y libavc1394-devel.x86_64
        yum install -y libavc1394.x86_64

	echo "opencv installed"	
}

function cleanup_opencv()
{
	#opencv
        yum remove -y gtk+-devel gtk2-devel
        yum remove -y pkgconfig.x86_64
        #yum remove -y python
        yum remove -y numpy
        yum remove -y libavc1394-devel.x86_64
        yum remove -y libavc1394.x86_64

	echo "opencv cleaned up!"

}

# figure out what we're doing
for var in "$@"
do
	# find out whether or not we're running install or cleanup
	if [ $var == $install_option ]; then
		mode=$install_option
	elif [ $var == $cleanup_option ]; then
		mode=$cleanup_option

        elif [ $var == $opencv_option ]; then
                opencv_option=1
	elif [ $var == $tinyosc_option ]; then
		tinyosc_option=1
	elif [ $var == $ofx_option ]; then
		ofx_option=1
	# according to mode, do something with the inputted program
	elif [ $var == $ogl_option ]; then
		ogl_option=1
	fi
done

if [ $tinyosc_option == 1 ]; then
	if [ $mode == $install_option ]; then
		install_tinyosc
	elif [ $mode == $cleanup_option ]; then
		cleanup_tinyosc
	fi
fi

if [ $ofx_option == 1 ]; then
	if [ $mode == $install_option ]; then
		installOpenFrameworks
	elif [ $mode == $cleanup_option ]; then
		cleanOpenFrameworks
	fi
fi

# check if our option has been affected
if [ ogl_option == 1 ]
	if [ $mode == $install_option ]; then
		install_ogl
	elif [ $mode == $cleanup_option]; then
		cleanup_ogl
	fi
fi

if [ $opencv_option == 1 ]; then
        if [$mode == $install_option ]; then
                install_opencv
        if [$mode == $cleanup_option ]; then
                cleanup_opencv
        fi
fi

