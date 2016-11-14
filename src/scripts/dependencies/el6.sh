#!/bin/bash

#packages for openGL and libusb
yum install -y libXmu-devel libXi-devel glut-devel libudev-devel cmake make

#installing libusb from source because repo version is outdated
wget --no-check-certificate -O libusb.tar.bz2 "http://downloads.sourceforge.net/project/libusb/libusb-1.0/libusb-1.0.20/libusb-1.0.20.tar.bz2?r=http%3A%2F%2Flibusb.info%2F&ts=1479155730&use_mirror=heanet"
tar -xvf libusb.tar.bz2
cd libusb-1.0.20
./configure
make
make install

#openframeworks dependencies from provided script
cd ../../../../openFrameworks/scripts/linux/fedora
./install_dependencies.sh
yum install -y gstreamer-devel gstreamer-plugins-base-devel