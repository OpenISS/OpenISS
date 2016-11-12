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

# Paths
LFN2_DIR=../../../../libfreenect2
OPCV_DIR=../../../../opencv

# install requirements
yum install -y git

yum install -y mesa-libGL
yum install -y mesa-libGL-devel

yum --enablerepo=elrepo-kernel install -y kernel-ml

yum install -y gcc
yum install -y make

yum install -y cmake
yum install -y turbojpeg
yum install -y libusb

# install libfreenect2
cd $LFN2_DIR
git clone https://github.com/OpenKinect/libfreenect2.git
cd libfreenect2


# install opencv
cd $OPCV_DIR
git clone https://github.com/opencv/opencv.git

