#!/bin/sh
bindir=$(pwd)
cd /root/final/OpenISS/ogl/tutorial08_basic_shading/
export 

if test "x$1" = "x--debugger"; then
	shift
	if test "xYES" = "xYES"; then
		echo "r  " > $bindir/gdbscript
		echo "bt" >> $bindir/gdbscript
		/usr/bin/gdb -batch -command=$bindir/gdbscript --return-child-result /root/final/OpenISS/oglBuild/tutorial08_basic_shading 
	else
		"/root/final/OpenISS/oglBuild/tutorial08_basic_shading"  
	fi
else
	"/root/final/OpenISS/oglBuild/tutorial08_basic_shading"  
fi
