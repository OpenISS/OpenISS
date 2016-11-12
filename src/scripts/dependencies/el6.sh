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

# install requirements
yum install git -y

yum install mesa-libGL -y
yum install mesa-libGL-devel -y

yum --enablerepo=elrepo-kernel install kernel-ml -y

yum install gcc -y
yum install make -y

yum install cmake -y
yum install turbojpeg -y
yum install libusb -y

# install libfreenect2
cd $LFN2_DIR
git clone https://github.com/OpenKinect/libfreenect2.git
cd libfreenect2


# install opencv