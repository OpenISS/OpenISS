#! /bin/bash
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

	# todo: add epel and elrepo repos. install python 34 from epel. fix kernel install
	yum --enablerepo=elrepo-kernel install -y kernel-ml-devel-4.8.7-1.el6.elrepo.x86_64
	wget us.download.nvidia.com/XFree86/Linux-x86_64/375.20/NVIDIA-Linux-x86_64-375.20.run
	sh NVIDIA-Linux-x86_64-375.20.run
	
	yum --enablerepo=epel install -y python34.x86_64

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
	
elif [[ $1 == "--cleanup" ]]; then
	echo "cleanup"
	
	rm -f NVIDIA-Linux-x86_64-375.20.run

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
	#yum remove -y python
	yum remove -y numpy
	yum remove -y libavc1394-devel.x86_64
	yum remove -y libavc1394.x86_64
fi
