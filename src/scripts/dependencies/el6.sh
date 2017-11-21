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

libfreenect2_option="--freenect2"
opencv_option="--opencv"

install_option="--install"
cleanup_option="--cleanup"
mode=0
system="el6"
el6_system="el6"

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

for var in "$@"
do
        if [ $var == $install_option ]; then
                mode=$install_option
        elif [ $var == $cleanup_option ]; then
                mode=$cleanup_option
        elif [ $var == $opencv_option ]; then
                opencv_option=1
        elif [ $var == $libfreenect2_option ]; then
		libfreenect2_option=1
	fi
done

if [ $libfreenect2_option == 1 ]; then
	if [ $mode == $install_option ]; then
		install_libfreenect2
	elif [ $mode == $cleanup_option ]; then
		cleanup_libfreenect2
	fi
fi

if [ $opencv_option == 1 ]; then
        if [$mode == $install_option ]; then
                install_opencv
        if [$mode == $cleanup_option ]; then
                cleanup_opencv
        fi
fi

