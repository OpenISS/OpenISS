#!/bin/bash

# el6.sh
# Script to install requirements:
#		libraries
#		compilers
#		kernels
#		drivers
#
# These are all needed to compile submodules in OpenISS
#
# Team 1: libfreenect2, and opencv
# Brian Baron, Colin Brady, Robert Gentile
# CSI-230
#
# Need to be root when running this script


if [[ $1 == "--install" ]]; then
	echo "install"

	# install requirements
	yum install -y git
	
	yum install -y mesa-libGL
	yum install -y mesa-libGL-devel

	yum --enablerepo=elrepo-kernel install -y kernel-ml
	
	yum install -y gcc
	yum install -y make
	yum install -y cmake
	
	#libfreenect2 dependencies
	#libusb
	cd ../../libfreenect2/depends
	./install_libusb.sh

	#turbojpeg
	yum install -y turbojpeg
	yum install -y turbojpeg-devel
	
	#opencv dependencies
	yum groupinstall -y "Development Tools"
	yum install -y gtk+-devel gtk2-devel
	yum install -y pkgconfig.x86_64
	yum install -y python
	yum install -y numpy
	yum install -y libavc1394-devel.x86_64
	yum install -y libavc1394.x86_64
	
	#packages for openGL and libusb
	yum install -y libXmu-devel libXi-devel glut-devel libudev-devel cmake make

	#installing libusb from source because repo version is outdated
	# Team 1 also needed libusb, which is installed above, differently, from libfreenect2
	#wget --no-check-certificate -O libusb.tar.bz2 "http://downloads.sourceforge.net/project/libusb/libusb-1.0/libusb-1.0.20/libusb-1.0.20.tar.bz2?r=http%3A%2F%2Flibusb.info%2F&ts=1479155730&use_mirror=heanet"
	#tar -xvf libusb.tar.bz2
	#cd libusb-1.0.20
	#./configure
	#make
	#make install

	#openframeworks dependencies from provided script
	cd ../../../../openFrameworks/scripts/linux/fedora
	./install_dependencies.sh
	yum install -y gstreamer-devel gstreamer-plugins-base-devel
elif [[ $1 == "--cleanup" ]]; then
	echo "cleanup"
	
	#libusb
	cd ../../../libfreenect2/depends/libusb_src
	make distclean
	cd ..
	rm -rf libusb
	rm -rf libusb_src

	#turbojpeg
	yum remove -y turbojpeg
	yum remove -y turbojpeg-devel

	#opencv
	yum remove -y gtk+-devel gtk2-devel
	yum remove -y pkgconfig.x86_64
	yum remove -y python
	yum remove -y numpy
	yum remove -y libavc1394-devel.x86_64
	yum remove -y libavc1394.x86_64

	#remove packages installed by yum
	yum remove -y libXmu-devel libXi-devel glut-devel libudev-devel gstreamer-devel gstreamer-plugins-base-devel
fi
