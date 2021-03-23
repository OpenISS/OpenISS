#!/bin/bash -x

# el7.sh
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

# TODO: add an option for unconditional installation of all dependencies

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
# Generic dependencies for development
function install_dev_dependencies()
{
 	echo "==============================="
 	echo "installing GENERAL dependencies"
 	echo "==============================="

	yum -y clean all
	yum -y clean expire-cache

	# add epel and elrepo repos needed form some dependencies
	EL_TYPE=`head -1 /etc/system-release | cut -d ' ' -f 1`
	if [[ "$EL_TYPE" == "Scientific" ]];
	then
		if [ "$(echo $0 | grep "el6")" -eq 0 ]; then
			yum install -y epel-release elrepo-release
		else
			# 7.2 onwards
			yum install -y yum-conf-repos
			yum install -y yum-conf-epel yum-conf-elrepo
		fi
	else
		# Presuming CentOS and RHEL
		# From 'extras'
		yum install -y epel-release
		# From elrepo.org
		rpm --import https://www.elrepo.org/RPM-GPG-KEY-elrepo.org
		rpm -Uvh https://www.elrepo.org/elrepo-release-7.0-3.el7.elrepo.noarch.rpm
	fi

	# Making sure gcc is installed
	# basic install/compile requirements
	yum install -y git
	yum install -y gcc
	yum install -y make cmake
	yum install -y patch wget
	yum install -y libtool systemd-devel

	# recent kernel install for the latest USB3 drivers
	#yum --enablerepo=elrepo-kernel install -y kernel-ml-devel-4.8.7-1.el6.elrepo.x86_64
	yum --enablerepo=elrepo-kernel install -y kernel-ml kernel-ml-devel

	# packages for OpenGL and libusb
	yum install -y libXmu-devel glut-devel libudev-devel
	yum install -y libusbx-devel libusb-devel

	# install python 34 from epel
	yum --enablerepo=epel install -y python34.x86_64

	echo "==============================="
	echo "GENERAL dependencies installed"
	echo "==============================="
}

function install_tinyosc()
{
	# Making sure gcc is installed
	yum install -y gcc

	echo "=============================="
	echo "tinyosc dependencies installed"
	echo "=============================="
}

function cleanup_tinyosc()
{
	# Intentionally not removing gcc as needed in many places    
	echo "====================================="
	echo "NOOP: unistalled tinyosc dependencies"
	echo "====================================="
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
		../download_libs.sh

		yum install -y gstreamer-devel gstreamer-plugins-base-devel

		# need glfw a-la libfreenect2
		DEPENDS_DIR=`pwd`

		# glfw
		GLFW_SOURCE_DIR=$DEPENDS_DIR/glfw_src
		rm -rf $GLFW_SOURCE_DIR
		git clone https://github.com/glfw/glfw.git $GLFW_SOURCE_DIR
		pushd $GLFW_SOURCE_DIR
			git checkout 3.0.4
			mkdir build
			cd build
			cmake -DBUILD_SHARED_LIBS=TRUE ..
			make && make install
		popd

		# el6 need gcc 4.7 and 4.8; el7 has 4.8 by default
		if [ "$(echo $0 | grep "el6")" -eq 0 ]; then
			# allows us to have gcc 4.7 and 4.8
			wget -O /etc/yum.repos.d/slc6-devtoolset.repo http://linuxsoft.cern.ch/cern/devtoolset/slc6-devtoolset.repo
			rpm --import http://linuxsoft.cern.ch/cern/slc6X/x86_64/RPM-GPG-KEY-cern

			# ofx itself requires gcc 4.8 on el6, else other errors crop up
			# scl enable it in build.sh
			yum install -y devtoolset-2-gcc-c++

			# glm requires gcc 4.7 on el6, else assembly errors crop up
			yum install -y devtoolset-1.1-gcc-c++
			. /opt/rh/devtoolset-1.1/enable
		fi

		which gcc
		whereis gcc
		gcc --version

		git clone https://github.com/g-truc/glm.git
		pushd glm
			mkdir build
			cd build
			cmake3 ..
			make
			make install
		popd
	popd

	echo "====================================="
	echo "openFrameworks dependencies installed"
	echo "====================================="
}

function cleanup_open_frameworks()
{
	#remove packages installed by yum
	yum remove -y gstreamer-devel gstreamer-plugins-base-devel

	echo "==================================="
	echo "openFrameworks el6 cleanup complete"
	echo "==================================="
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
	VIDEODRIVERSCRIPT=NVIDIA-Linux-x86_64-384.98.run
	VIDEODRIVERPATH=XFree86/Linux-x86_64/384.98/$VIDEODRIVERSCRIPT
	#VIDEODRIVER=XFree86/Linux-x86_64/340.104/NVIDIA-Linux-x86_64-340.104.run
	wget us.download.nvidia.com/$VIDEODRIVERPATH
	# Suppress non-zero exist code if fails, may need to resolve manually
	sh $VIDEODRIVERSCRIPT --silent || echo 0 > /dev/null

	echo "============================="
	echo "OpenGL dependencies installed"
	echo "============================="
}

function cleanup_ogl()
{
	#remove packages installed by yum
	yum remove -y libXmu-devel libXi-devel glut-devel libudev-devel

	echo "==========================="
	echo "cleaned OpenGL dependencies"
	echo "==========================="
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

	echo "============================="
	echo "OpenCV dependencies installed"	
	echo "============================="
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

	echo "=================="
	echo "OpenCV cleaned up!"
	echo "=================="
}

function install_libfreenect2()
{
	echo "**************************"
	# libfreenect2 dependencies
	# libusb, requires libudev-devel, libtool from above
	time install_freenect_depends	

	# turbojpeg (libfreenect2)
	yum install -y turbojpeg
	yum install -y turbojpeg-devel

	echo "==========================="
	echo "libfreenect2 deps installed"
	echo "==========================="
}

function cleanup_libfreenect2()
{
	
	#turbojpeg
	yum remove -y turbojpeg
	yum remove -y turbojpeg-devel

	#libusb
	# TODO: ignores the fact that libfreenect may still be using it
	time cleanup_freenect_depends

	echo "========================="
	echo "libfreenect2 deps cleaned"
	echo "========================="
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
	time install_freenect_depends

	# TODO: OpenNI2 will require cmake3 and gcc 4.8+ from devtoolset-2
	yum install -y cmake3

	echo "=========================="
	echo "libfreenect deps installed"
	echo "=========================="
}

function cleanup_libfreenect()
{
	time cleanup_freenect_depends
	echo "=============================="
	echo "libfreenect deps cleaned"
	echo "=============================="
}

function cleanup_freenect_depends()
{

	if [ "$(grep "freenect_deps" $0.cache)" == "freenect_deps" ];
	then
		pushd ../../../libfreenect2/depends/libusb_src
			make distclean
			cd ..
			rm -rf libusb
			rm -rf libusb_src
		popd
		pwd
		echo "freenect depends cleaned"
		sed -i '/freenect_deps/d' $0.cache 
	else
		echo "freenect depends are not installed"
	fi

}

function install_freenect_depends()
{
	if [ "$(grep "freenect_deps" $0.cache)" != "freenect_deps" ];
	then
		pushd ../../../libfreenect2/depends
			./install_libusb.sh
			./install_glfw.sh
		popd

		echo "freenect depends installed"
		echo "freenect_deps" >> $0.cache 
	else
		echo "freenect depends already installed"
	fi

}

# figure out what we're doing
for current_option in "$@"
do
	# find out whether or not we're running install or cleanup
	if [ "$current_option" == "$install_option" ]; then
		mode=$install_option
	elif [ "$current_option" == "$cleanup_option" ]; then
		mode=$cleanup_optionadd

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

	echo "======="
	echo "INSTALL"
	echo -n "Started: " ; date
	echo "======="

	if [ ! -e "$0.cache" ]
	then
		touch $0.cache
	fi

	if [ "$(grep "$0-dependencies" $0.cache)" != "$0-dependencies" ]; then
		time install_dev_dependencies
		echo "$0-dependencies" >> $0.cache
	fi

	if [ "$ogl_option" == "1" ]; then
		if [ "$(grep "ogl" $0.cache)" != "ogl" ]; then
			time install_ogl
			echo "ogl" >> $0.cache 
		fi
	fi

	if [ "$libfreenect2_option" == "1" ]; then
		if [ "$(grep "libfreenect2" $0.cache)" != "libfreenect2" ]; then
			time install_libfreenect2
			echo "libfreenect2" >> $0.cache 
		fi
	fi

	if [ "$libfreenect_option" == "1" ]; then
		echo "*******************"
		if [ "$(grep "libfreenect_" $0.cache)" != "libfreenect_" ]; then
			time install_libfreenect
			echo "libfreenect_" >> $0.cache 
		fi
	fi

	if [ "$tinyosc_option" == "1" ]; then
		if [ "$(grep "tinyosc" $0.cache)" != "tinyosc" ]; then
			time install_tinyosc
			echo "tinyosc" >> $0.cache 
		fi
	fi

	if [ "$opencv_option" == "1" ]; then
		if [ "$(grep "opencv" $0.cache)" != "opencv" ]; then
			time install_opencv
			echo "opencv" >> $0.cache 
		fi
	fi

	if [ "$ofx_option" == "1" ]; then
		if [ "$(grep "ofx" $0.cache)" != "ofx" ]; then
			time install_open_frameworks
			echo "ofx" >> $0.cache 
		fi
	fi

	echo "============"
	echo "INSTALL DONE"
	date
	echo "Cache:"
	cat $0.cache
	echo "============"

# TODO: cache entries removal
elif [ "$mode" == "$cleanup_option" ]; then

	echo "======="
	echo "CLEANUP"
	echo "======="

	if [ "$ofx_option" == "1" ]; then
		time cleanup_open_frameworks
		sed -i '/ofx/d' $0.cache
	fi

	if [ "$opencv_option" == "1" ]; then
		time cleanup_opencv
		sed -i '/opencv/d' $0.cache
	fi

	if [ "$tinyosc_option" == "1" ]; then
		time cleanup_tinyosc
		sed -i '/tinyosc/d' $0.cache
	fi

	if [ "$libfreenect_option" == "1" ]; then
		time cleanup_libfreenect
		sed -i '/libfreenect_/d' $0.cache
	fi

	if [ "$libfreenect2_option" == "1" ]; then
		time cleanup_libfreenect2
		sed -i '/libfreenect2/d' $0.cache
	fi

	if [ "$ogl_option" == "1" ]; then
		time cleanup_ogl
		sed -i '/ogl/d' $0.cache
	fi

else
	echo "$0: unrecognized mode $mode"
	exit 1
fi

exit 0

# EOF
