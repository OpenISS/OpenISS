#!/bin/bash -x

# build.sh
#
# Need to be root when running this script
#
# CSI-230 Fall 2016
#   Brian Baron, Colin Brady, Robert Gentile
#   Justin Mulkin, Gabriel Pereyra, Duncan Carrol, Lucas Spiker
#

ogl_option="--ogl"

do_all=1

install_option="--install"
cleanup_option="--cleanup"

system="el6"
el6_system="el6"

# install ogl - Matthew Roy
function install_ogl()
{
	# check if we need to install
	if [ "$(grep "ogl" build.cache)" != "ogl" ]; then
		# call the function in &system.sh to install dependencies
		./dependencies/$system.sh --install --ogl

        	pushd ../../ogl
        	rm -rf build
        	mkdir build && cd build
        	cmake ..
        	make
        	popd
        	echo "ogl" >> build.cache
	else
		# else don't bother
		echo "ogl already installed"
	fi
}

# cleanup ogl - Matthew Roy
function cleanup_ogl()
{
	# check if we need to uninstall
	if [ "$(grep "ogl" build.cache)" == "ogl" ]; then
		# call the function in $system.sh to cleanup dependencies
		./dependencies/$system.sh --cleanup --ogl

		# remove the line from build.cache
		sed -i '/ogl/d' build.cache	

		# except it really isn't
		echo "cleaned ogl"
	else
		# else we don't need to uninstall it
		echo "ogl not installed"
	fi
}

# figure out what we're doing
for var in "$@"
do
	# find out whether or not we're running install or cleanup
	if [ $var == $install_option ]; then
		mode=$install_option
	elif [ $var == $cleanup_option ]; then
		mode=$cleanup_option
	
	# in case we want to be able to install to a different system
	elif [ $var == $el6_system ]; then
		system=$el6_system

	# according to mode, do something with the inputted program
	elif [ $var == $ogl_option ]; then
		ogl_option=1
		do_all=0
	fi
done

# check if our option has been affected or we're doing all
if [ ogl_option == 1 -o do_all == 1 ]; then
	if [ $mode == $install_option ]; then
		install_ogl
	elif [ $mode == $cleanup_option]; then
		cleanup_ogl
	fi
fi
