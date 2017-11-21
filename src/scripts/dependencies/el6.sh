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

ogl_option = "--ogl"

install_option = "--install"
cleanup_option = "--cleanup"

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

# figure out what we're doing
for var in "$@"
do
	# find out whether or not we're running install or cleanup
	if [ $var == $install_option ]; then
		mode=$install_option
	elif [ $var == $cleanup_option ]; then
		mode=$cleanup_option

	# according to mode, do something with the inputted program
	elif [ $var == $ogl_option ]; then
		ogl_option=1
	fi
done

# check if our option has been affected
if [ ogl_option == 1 ]
	if [ $mode == $install_option ]; then
		install_ogl
	elif [ $mode == $cleanup_option]; then
		cleanup_ogl
	fi
fi

