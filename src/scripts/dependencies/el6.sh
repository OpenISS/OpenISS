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
wget --no-check-certificate https://sourceforge.net/projects/libusb/files/libusb-1.0/libusb-1.0.20/libusb-1.0.20.tar.bz2/download
tar -xf libusb-1.0.20.tar.bz2
cd libusb-1.0.20.tar.bz2
./configure; make; make install
cd ..

#turbojpeg
yum install -y turbojpeg
yum install -y turbojpeg-devel

#opencv dependencies
yum groupinstall "Development Tools"
yum install -y gtk+-devel gtk2-devel
yum install -y pkgconfig.x86_64
yum install -y python
yum install -y numpy
yum install -y libavc1394-devel.x86_64
yum install -y libavc1394.x86_64