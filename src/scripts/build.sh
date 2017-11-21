#!/bin/bash -x

# build.sh
#
# Need to be root when running this script
#
# CSI-230 Fall 2016
#   Brian Baron, Colin Brady, Robert Gentile
#   Justin Mulkin, Gabriel Pereyra, Duncan Carrol, Lucas Spiker
#

opencv_option="--opencv"

do_all=1
install_option="--install"
cleanup_option="--cleanup"
mode=0
system="el6"
el6_system="el6"

install_opencv()
{
        if [ "$(grep "opencv" build.cache)" != "opencv" ]
        then
		./dependencies/$system.sh --install --opencv

                # compile opencv
                pushd ../../opencv
                rm -rf build
                mkdir build && cd build
                cmake ..
                make
                popd
                echo "opencv" >> build.cache
        else
                echo "opencv already installed"
        fi
}

cleanup_opencv()
{

	if [ "$(grep "opencv" build.cache)" == "opencv" ]
        then
                # compile opencv
                ./dependencies/$system.sh --cleanup --opencv
        	sed -i '/foo/d' ./build.cache
		echo "opencv removed"

        else
                echo "opencv not installed"
        fi


}


for var in "$@"
do
	if [ $var == $install_option ]; then
		mode=$install_option
	elif [ $var == $cleanup_option ]; then
		mode=$cleanup_option
	elif [ $var == $el6_system ]; then
		system=$el6_system
	elif [ $var == $opencv_option ]; then
		opencv_option=1
		do_all=0
	fi
done

if [ $opencv_option == 1 -o $do_all == 1 ]; then
	if [$mode == $install_option ]; then
		install_opencv
	if [$mode == $cleanup_option ]; then
		cleanup_opencv
	fi
fi
