#!/bin/bash -x

# build.sh
#
# Need to be root when running this script
#
# CSI-230 Fall 2016
#   Brian Baron, Colin Brady, Robert Gentile
#   Justin Mulkin, Gabriel Pereyra, Duncan Carrol, Lucas Spiker
#

if [ ! -e "build.cache" ]
then
	touch build.cache
fi

libfreenect2_option="--freenect2"

do_all=1
install_option="--install"
cleanup_option="--cleanup"
mode=0
system="el6"
el6_system="el6"

function install_libfreenect2()
{
	#install deps
	./dependencies/$system.sh --install --freenect2

	if [ "$(grep "libfreenect2" build.cache)" != "libfreenect2" ]
        then
                # compile the libfreenect2 stuff
                pushd ../../libfreenect2
                rm -rf build
                mkdir build && cd build
                cmake -L ..
                make install
                popd
                echo "libfreenect2" >> build.cache
        else
                echo "libfreenect2 already installed"
	fi
}

function cleanup_libfreenect2()
{
	if [ "$(grep "libfreenect2" build.cache)" == "libfreenect2" ]
        then
		./dependencies/$system.sh --cleanup --freenect2
		sed -i '/libfreenect2/d' build.cache
		echo "cleaned libfreenect2"
	else
		echo "libfreenect2 not installed"
	fi
}

for var in $@
do
	if [ $var == $install_option ]; then
		mode=$install_option
	elif [ $var == $cleanup_option ]; then
		mode=$cleanup_option
	elif [ $var == $el6_system ]; then
		system=$el6_system
	elif [ $var == $libfreenect2_option ]; then
		libfreenect2_option=1
		do_all=0
	fi
done

if [ $libfreenect2_option == 1 -o $do_all == 1 ]; then
	if [ $mode == $install_option ]; then
		install_libfreenect2
	elif [ $mode == $cleanup_option ]; then
		cleanup_libfreenect2
	fi
fi
