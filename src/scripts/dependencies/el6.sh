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
# - Need to be root when running this script.
# - For NVIDIA install to work, need to run it in runlevel 2 with nouveau blacklisted

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

# Generic dependecies for development
function install_dev_dependencies()
{
	# Making sure gcc is installed
	yum -y clean all
	yum -y clean expire-cache

	# add epel and elrepo repos needed form some packages below
	EL6TYPE=`head -1 /etc/issue | cut -d ' ' -f 1`
	if [[ "$EL6TYPE" == "Scientific" ]];
	then
		yum install -y epel-release elrepo-release
	else
		# Presuming CentOS and RHEL
		# From 'extras'
		yum install -y epel-release
		# From elrepo.org
		rpm --import https://www.elrepo.org/RPM-GPG-KEY-elrepo.org
		rpm -Uvh http://www.elrepo.org/elrepo-release-6-8.el6.elrepo.noarch.rpm
	fi

	# basic install/compile requirements
	yum install -y git
	yum install -y gcc
	yum install -y make cmake
	
	# recent kernel install for the latest USB3 drivers
	#yum --enablerepo=elrepo-kernel install -y kernel-ml-devel-4.8.7-1.el6.elrepo.x86_64
	yum --enablerepo=elrepo-kernel install -y kernel-ml kernel-ml-devel

	# packages for OpenGL and libusb
	yum install -y libXmu-devel glut-devel libudev-devel libtool

	# install python 34 from epel
	yum --enablerepo=epel install -y python34.x86_64
}

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

	yum install -y gstreamer-devel gstreamer-plugins-base-devel
}

function cleanup_open_frameworks()
{
	# Empty for now
	#remove packages installed by yum
	yum remove -y gstreamer-devel gstreamer-plugins-base-devel
	echo "openFrameworks el6 cleanup complete"
}

function install_ogl()
{
	# dependencies for OpenGL
	yum install -y \
		cmake3 make \
		gcc-c++ \
		libX11-devel libXi-devel \
		mesa-libGL mesa-libGL-devel mesa-libGLU \
		libXrandr-devel libXext-devel libXcursor-devel \
		libXinerama-devel libXi-devel

	# more packages for OpenGL
	yum install -y libXmu-devel glut-devel

	# TODO: refactor somehow; to select dynamically from lspci,
	#       then download or dpkg-nvidia from elrepo
	# TODO: this will need to be installed when booted
	VIDEODRIVERSCRIPT=NVIDIA-Linux-x86_64-375.20.run
	VIDEODRIVERPATH=XFree86/Linux-x86_64/375.20/$VIDEODRIVERSCRIPT
	#VIDEODRIVER=XFree86/Linux-x86_64/340.104/NVIDIA-Linux-x86_64-340.104.run
	wget us.download.nvidia.com/$VIDEODRIVERPATH
	# Suppress non-zero exist code if fails, may need to resolve manually
	sh $VIDEODRIVERSCRIPT || echo 0 > /dev/null
}

function cleanup_ogl()
{
	# Empty for now
	#remove packages installed by yum
	yum remove -y libXmu-devel libXi-devel glut-devel libudev-devel
	echo "cleaned ogl"
}

function install_opencv()
{
	# opencv dependencies
        yum groupinstall -y "Development Tools"

        yum install -y opencv
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
	yum remove -y opencv
	#yum remove -y gtk+-devel gtk2-devel
	#yum remove -y pkgconfig.x86_64
	#yum remove -y python
	#yum remove -y numpy
	#yum remove -y libavc1394-devel.x86_64
	#yum remove -y libavc1394.x86_64

	echo "opencv cleaned up!"
}

function install_libfreenect2()
{
	# libfreenect2 dependencies
	# libusb, requires libudev-devel, libtool from above
	pushd ../../libfreenect2/depends
		./install_libusb.sh
		./install_glfw.sh
	popd

	# turbojpeg (libfreenect2)
	yum install -y turbojpeg-devel

	echo "libfreenect2 deps installed"
}

function cleanup_libfreenect2()
{
	#turbojpeg
	yum remove -y turbojpeg
	yum remove -y turbojpeg-devel

	#libusb
	pushd ../../libfreenect2/depends/libusb_src
		make distclean
		cd ..
		rm -rf libusb
		rm -rf libusb_src
	popd

	echo "libfreenect2 deps cleaned"
}

function install_libfreenect()
{
	# libfreenect	
	# needs libusb, which is installed above in libfreenect2
	# TODO: check if libfrenect2's libusb is already installed and if not install it
	#       we link to it in build.sh
# 	pushd ../../libfreenect2/depends
# 		./install_libusb.sh
# 	popd

	# TODO: OpenNI2 will require cmake3 and gcc 4.8+ from devtoolset-2
	yum install -y cmake3

	echo "libfreenect deps installed"
}

function cleanup_libfreenect()
{
	echo "libfreenect deps cleaned"
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
if [ "$mode" == "$install_option" ]; then

	echo "install"

	install_dev_dependencies

	if [ "$ogl_option" == "1" ]; then
		install_ogl
	fi

	if [ "$libfreenect2_option" == "1" ]; then
		install_libfreenect2
	fi

	if [ "$libfreenect_option" == "1" ]; then
		install_libfreenect
	fi

	if [ "$tinyosc_option" == "1" ]; then
		install_tinyosc
	fi

	if [ "$opencv_option" == "1" ]; then
		install_opencv
	fi

	if [ "$ofx_option" == "1" ]; then
		install_open_frameworks
	fi

elif [ "$mode" == "$cleanup_option" ]; then

	echo "cleanup"

	if [ "$ofx_option" == "1" ]; then
		cleanup_open_frameworks
	fi

	if [ "$opencv_option" == "1" ]; then
		cleanup_opencv
	fi

	if [ "$tinyosc_option" == "1" ]; then
		cleanup_tinyosc
	fi

	if [ "$libfreenect_option" == "1" ]; then
		cleanup_libfreenect
	fi

	if [ "$libfreenect2_option" == "1" ]; then
		cleanup_libfreenect2
	fi

	if [ "$ogl_option" == "1" ]; then
		cleanup_ogl
	fi

else
	echo "$0: unrecognized mode $mode"
	exit 1
fi

exit 0

# EOF
