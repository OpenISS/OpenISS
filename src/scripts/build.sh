#!/bin/bash -x

# build.sh
#
# Need to be root when running this script
#
# CSI-230 Fall 2016
#   Brian Baron, Colin Brady, Robert Gentile
#   Justin Mulkin, Gabriel Pereyra, Duncan Carrol, Lucas Spiker
#

ofx_option="--ofx"

do_all=1
install_option="--install"
cleanup_option="--cleanup"
mode=0
system="el6"
el6_system="el6"

function installOpenFrameworks()
{
	if [ "$(grep "openframeworks" build.cache)" != "openframeworks" ];
	then
		#install dependencies
		echo "running el6.sh"
		./dependencies/$system.sh $cleanup_option $ofx_option
		echo "openframeworks" >> build.cache

		#run install script to openframeworks
		pushd ../../openFrameworks/scripts/linux
		#tells scripts to use 3 cpu cores compile

		#cant actually compile openframeworks as 11/14/16 becuase github master branch doesn't build
		#but it looks like theyre working on fixing

		#./compileOF.sh -j3
		popd
		echo "openframeworks" >> build.cache
	else
		echo "openframeworks already installed"
	fi
}

function cleanOpenFrameworks()
{

	if [ "$(grep "openframeworks" build.cache)" == "openframeworks" ];
	then
		./dependencies/$system.sh --cleanup --ofx
		sed -i '/openframeworks/d' build.cache
		echo "openframeworks cleanup complete"

	else
		echo "openframeworks is not installed"
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
	elif [ $var == $ofx_option ]; then
		ofx_option=1
		do_all=0
	fi
done

if [ $ofx_option == 1 -o $do_all == 1 ]; then
	if [ $mode == $install_option ]; then
		installOpenFrameworks
	elif [ $mode == $cleanup_option ]; then
		cleanOpenFrameworks
	fi
fi
