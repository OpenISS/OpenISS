#!/bin/sh
bindir=$(pwd)
cd /root/final/OpenISS/ogl/tutorial05_textured_cube/
export 

if test "x$1" = "x--debugger"; then
	shift
	if test "xYES" = "xYES"; then
		echo "r  " > $bindir/gdbscript
		echo "bt" >> $bindir/gdbscript
		/usr/bin/gdb -batch -command=$bindir/gdbscript --return-child-result /root/final/OpenISS/oglBuild/tutorial05_textured_cube 
	else
		"/root/final/OpenISS/oglBuild/tutorial05_textured_cube"  
	fi
else
	"/root/final/OpenISS/oglBuild/tutorial05_textured_cube"  
fi
