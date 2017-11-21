#!/bin/bash

# el6.sh
#
# Script to install requirements:
#		libraries
#		compilers
#		kernels
#		drivers
#
# These are all needed to compile submodules in OpenISS
#
# Need to be root when running this script

tinyosc_option="--tinyosc"

install_option="--install"
cleanup_option="--cleanup"
mode=0

function install_tinyosc()
{
	yum install -y gcc
	echo "tinyosc dependencies installed"
}
function cleanup_tinyosc()
{
	echo "unistalled tinyosc"
}


for var in "$@"
do
	if [ $var == $install_option ]; then
		mode=$install_option
	elif [ $var == $cleanup_option ]; then
		mode=$cleanup_option
	elif [ $var == $tinyosc_option ]; then
		tinyosc_option=1
	fi

done

if [ $tinyosc_option == 1 ]; then
	if [ $mode == $install_option ]; then
		install_tinyosc
	elif [ $mode == $cleanup_option ]; then
		cleanup_tinyosc
	fi
fi

