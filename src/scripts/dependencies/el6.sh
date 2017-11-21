#!/bin/bash -x

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

ofx_option="--ofx"
install_option="--install"
cleanup_option="--cleanup"
mode=0


function installOpenFrameworks()
{
	 # openFrameworks
        pushd ../../openFrameworks
        	git submodule update --init --recursive
	popd
        # openframeworks dependencies from the provided script
        pushd ../../openFrameworks/scripts/linux/el6
                ./install_codecs.sh
                ./install_dependencies.sh
	popd
}

function cleanOpenFrameworks()
{
	echo "openframeworks el6 cleanup complete"
}

for var in "$@"
do
	if [ $var == $install_option ]; then
		mode=$install_option
	elif [ $var == $cleanup_option ]; then
		mode=$cleanup_option
	elif [ $var == $ofx_option ]; then
		ofx_option=1
	fi
done

if [ $ofx_option == 1 ]; then
	if [ $mode == $install_option ]; then
		installOpenFrameworks
	elif [ $mode == $cleanup_option ]; then
		cleanOpenFrameworks
	fi
fi


