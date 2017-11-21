#!/bin/bash

# build.sh
#
# Need to be root when running this script
#
# CSI-230 Fall 2016
#   Brian Baron, Colin Brady, Robert Gentile
#   Justin Mulkin, Gabriel Pereyra, Duncan Carrol, Lucas Spiker
#

tinyosc_option="--tinyosc"

do_all=1
install_option="--install"
cleanup_option="--cleanup"
mode=0
system="el6"
el6_system="el6"

function install_tinyosc()
{
	if [ "$(grep "tinyosc" build.cache)" != "tinyosc" ]; then
		#patch and compile tinyosc
		./dependencies/$system.sh --install --tinyosc
		pushd ../../tinyosc
		patch build.sh < ../src/scripts/dependencies/tinyosc.build.sh.patch
		./build.sh
		popd
		echo "tinyosc" >> build.cache
	else
		echo "tinyosc already installed"
	fi
}
function cleanup_tinyosc()
{	
	if [ "$(grep "tinyosc" build.cache)" == "tinyosc" ]; then
		./dependencies/$system.sh --install --tinyosc
		sed -i '/tinyosc/d' build.cache
		echo "tinyosc uninstalled"
	else
		echo "tinyosc not installed"
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
	elif [ $var == $tinyosc_option ]; then
		tinyosc_option=1
	fi

done

if [ $tinyosc_option == 1 -o $do_all == 1 ]; then
	if [ $mode == $install_option ]; then
		install_tinyosc
	elif [ $mode == $cleanup_option ]; then
		cleanup_tinyosc
	fi
fi
