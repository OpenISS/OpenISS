#!/bin/bash -x

# el6.sh
#
# Script to install requirements:
#	libraries
#	compilers
#	kernels
#	drivers
#
# These are all needed to compile submodules in OpenISS
#
# Need to be root when running this script

# Supported options
install_option="--install"
cleanup_option="--cleanup"

tinyosc_option="--tinyosc"
ofx_option="--ofx"
ogl_option="--ogl"
opencv_option="--opencv"
libfreenect_option="--freenect"
libfreenect2_option="--freenect2"

# Mode selects between install and cleanup
mode=0

# Install/cleanup functions

function install_tinyosc()
{
	# Making sure gcc is installed
	yum install -y gcc
	echo "tinyosc dependencies installed"
}

function cleanup_tinyosc()
{
	# Intentionally not removing gcc as needed in many places    
	echo "unistalled tinyosc"
}

function install_open_frameworks()
{
	# openFrameworks submodules
	pushd ../../openFrameworks
		git submodule update --init --recursive
	popd

	# openframeworks dependencies from the provided script
	pushd ../../openFrameworks/scripts/linux/el6
		./install_codecs.sh
		./install_dependencies.sh
	popd
}

function cleanup_open_frameworks()
{
	# Empty for now
	echo "openFrameworks el6 cleanup complete"
}

function install_ogl()
{
	# dependencies for ogl
	yum install -y \
		cmake3 make \
		gcc-c++ \
		libX11-devel libXi-devel mesa-libGL mesa-libGLU \
		libXrandr-devel libXext-devel libXcursor-devel \
		libXinerama-devel libXi-devel
}

function cleanup_ogl_deps()
{
	# Empty for now
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

function install_libfreenect2()
{
	# libfreenect2 dependencies
        # libusb, requires libudev-devel, libtool from above
        pushd ../../libfreenect2/depends
                ./install_libusb.sh
        popd

        # turbojpeg (libfreenect2)
        yum install -y turbojpeg-devel

	echo "libfreenect2 deps installed"
}

function cleanup_libfreenect2()
{
	echo "libfreenect2 deps cleaned"
}

function install_libfreenect()
{
	echo "libfreenect deps installed"
}

function cleanup_libfreenect()
{
	echo "libfreenect deps cleaned"
}

# figure out what we're doing
for current_option in "$@"
do
	# find out whether or not we're running install or cleanup
	if [ "$current_option" == "$install_option" ]; then
		mode=$install_option
	elif [ "$current_option" == "$cleanup_option" ]; then
		mode=$cleanup_option

	# according to mode, do something with the inputted program
	elif [ "$current_option" == "$libfreenect2_option" ]; then
		libfreenect2_option=1
	elif [ "$current_option" == "$libfreenect_option" ]; then
		libfreenect_option=1
        elif [ "$current_option" == "$opencv_option" ]; then
                opencv_option=1
	elif [ "$current_option" == "$tinyosc_option" ]; then
		tinyosc_option=1
	elif [ "$current_option" == "$ofx_option" ]; then
		ofx_option=1
	elif [ "$current_option" == "$ogl_option" ]; then
		ogl_option=1
	fi
done

# Parse selected inputs to check if our options have been affected
if [ $libfreenect2_option == 1 ]; then
	if [ "$mode" == "$install_option" ]; then
		install_libfreenect2
	elif [ "$mode" == "$cleanup_option" ]; then
		cleanup_libfreenect2
	fi
fi

if [ $tinyosc_option == 1 ]; then
	if [ "$mode" == "$install_option" ]; then
		install_tinyosc
	elif [ "$mode" == "$cleanup_option" ]; then
		cleanup_tinyosc
	fi
fi

if [ $ofx_option == 1 ]; then
	if [ "$mode" == "$install_option" ]; then
		install_open_frameworks
	elif [ "$mode" == "$cleanup_option" ]; then
		cleanup_open_frameworks
	fi
fi

if [ $ogl_option == 1 ]; then
	if [ "$mode" == "$install_option" ]; then
		install_ogl
	elif [ "$mode" == "$cleanup_option" ]; then
		cleanup_ogl
	fi
fi

if [ $opencv_option == 1 ]; then
	if [ "$mode" == "$install_option" ]; then
		install_opencv
	elif [ "$mode" == "$cleanup_option" ]; then
		cleanup_opencv
	fi
fi

# EOF
