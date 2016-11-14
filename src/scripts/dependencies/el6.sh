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
#ligbtk2.0-dev
#pkg-congfig
yum install -y python
#python-numpy
#libavcodec-dev libavformat-dev libswscale-dev
