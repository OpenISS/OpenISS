#! /bin/bash
# build.sh
# Brian Baron, Colin Brady, Robert Gentile
# CSI-230
#
# Need to be root when running this script

if [[ $1 == "el6" ]]; then
	#statements
	echo "running el6.sh"
	./dependencies/el6.sh
fi