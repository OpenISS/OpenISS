#! /bin/bash
# build.sh
# Brian Baron, Colin Brady, Robert Gentile
# CSI-230
#
# Need to be root when running this script

if [[ $1 == "el6" ]]; then
	echo "running el6.sh"
	./dependencies/el6.sh --install

	# compile the libfreenect2 stuff
	cd ../../libfreenect2
	rm -rf build
	mkdir build && cd build
	cmake -L ..
	make install

	# compile opencv
	cd ../../opencv
	rm -rf build
	mkdir build && cd build
	cmake ..
	make

	#patch and compile tinyosc
	cd ../../tinyosc
	patch build.sh < ../src/scripts/dependencies/tinyosc.build.sh.patch
	./build.sh


elif [[ $1 == "--cleanup" ]]; then
	./dependencies/el6.sh --cleanup
fi
