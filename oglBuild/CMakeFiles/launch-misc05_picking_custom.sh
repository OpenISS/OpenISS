#!/bin/sh
bindir=$(pwd)
cd /root/final/OpenISS/ogl/misc05_picking/
export 

if test "x$1" = "x--debugger"; then
	shift
	if test "xYES" = "xYES"; then
		echo "r  " > $bindir/gdbscript
		echo "bt" >> $bindir/gdbscript
		/usr/bin/gdb -batch -command=$bindir/gdbscript --return-child-result /root/final/OpenISS/oglBuild/misc05_picking_custom 
	else
		"/root/final/OpenISS/oglBuild/misc05_picking_custom"  
	fi
else
	"/root/final/OpenISS/oglBuild/misc05_picking_custom"  
fi
