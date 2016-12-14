#!/bin/sh
bindir=$(pwd)
cd /root/final/OpenISS/ogl/tutorial14_render_to_texture/
export 

if test "x$1" = "x--debugger"; then
	shift
	if test "xYES" = "xYES"; then
		echo "r  " > $bindir/gdbscript
		echo "bt" >> $bindir/gdbscript
		/usr/bin/gdb -batch -command=$bindir/gdbscript --return-child-result /root/final/OpenISS/oglBuild/tutorial14_render_to_texture 
	else
		"/root/final/OpenISS/oglBuild/tutorial14_render_to_texture"  
	fi
else
	"/root/final/OpenISS/oglBuild/tutorial14_render_to_texture"  
fi
