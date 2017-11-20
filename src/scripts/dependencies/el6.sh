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

for var in "$@"
do
	if [ $var == $install_option ]; then
		mode=$install_option
	elif [ $var == $cleanup_option ]; then
		mode=$cleanup_option
	elif [ $var == $tinyosc_option ]; then
		tinyosc_option=1
	elif [ $var == $ofx_option ]; then
		ofx_option=1
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

if [[ "$1" == "--ogl" ]]; then
	install_ogl_deps
        echo "ogl"
fi
