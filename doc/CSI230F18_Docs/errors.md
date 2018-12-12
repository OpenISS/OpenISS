NVidia
--------
You appear to be running an X server; please exit X before installing.
For further details, please see the section INSTALLING THE NVIDIA DRIVER
in the README available on the Linux driver download page at www.nvidia.com.

Fixed by running init 1 but not every computer ran init 1 without showing a blank screen.

Next issue was that it required Nouveau to be disabled 
Fixed by follow these instructions http://www.advancedclustering.com/act_kb/installing-nvidia-drivers-rhel-centos-7/

Next issue was that the kernel source tree was not found
Fixed by appending --kernel-source /usr/src/kernels/3.10.0-862.14.4.el7.x86_64

Next Error said “an Error occurred while performing the step building kernel modules
Fixed by installing nvidia-detect and all the packages with that

Next issue said the installation was cancelled due to the availability  or an alternate driver installation
Did not fix this issue and the next morning my system does not boot up anymore

Screen says “Oh no! Something has gone wrong.
A problem has occurred and the system can’t recover. Please log out and try again.”
I have nothing to show for on my computer now.

OpenISS/openFrameworks/scripts/linux/el6/install_codecs.sh
Error: Package: ffmpeg-libs-0.10.16-1.el6.x86_64 (rpmfusion-free-updates)
           Requires: libgnutls.so.26(GNUTLS_1_4)(64bit)
Error: Package: librtmp-2.3-3.el6.x86_64 (rpmfusion-free-updates)
           Requires: libgnutls.so.26()(64bit)
Error: Package: ffmpeg-libs-0.10.16-1.el6.x86_64 (rpmfusion-free-updates)
           Requires: libcelt0.so.1()(64bit)
Error: Package: librtmp-2.3-3.el6.x86_64 (rpmfusion-free-updates)
           Requires: libgnutls.so.26(GNUTLS_1_4)(64bit)
Error: Package: ffmpeg-libs-0.10.16-1.el6.x86_64 (rpmfusion-free-updates)
           Requires: libcdio_paranoia.so.0()(64bit)
Error: Package: ffmpeg-libs-0.10.16-1.el6.x86_64 (rpmfusion-free-updates)
           Requires: libgnutls.so.26()(64bit)
Error: Package: ffmpeg-libs-0.10.16-1.el6.x86_64 (rpmfusion-free-updates)
           Requires: libcdio_cdda.so.0(CDIO_CDDA_0)(64bit)
Error: Package: ffmpeg-libs-0.10.16-1.el6.x86_64 (rpmfusion-free-updates)
           Requires: libcdio_paranoia.so.0(CDIO_PARANOIA_0)(64bit)
Error: Package: ffmpeg-libs-0.10.16-1.el6.x86_64 (rpmfusion-free-updates)
           Requires: libopenjpeg.so.2()(64bit)
Error: Package: ffmpeg-compat-0.6.7-1.el6.x86_64 (rpmfusion-free-updates)
           Requires: libopenjpeg.so.2()(64bit)
Error: Package: ffmpeg-libs-0.10.16-1.el6.x86_64 (rpmfusion-free-updates)
           Requires: libass.so.4()(64bit)
Error: Package: ffmpeg-libs-0.10.16-1.el6.x86_64 (rpmfusion-free-updates)
           Requires: libcdio_cdda.so.0()(64bit)     
fix: https://linoxide.com/linux-how-to/install-ffmpeg-centos-7/ 

OpenISS/openFrameworks/scripts/linux/el6/install_dependencies.sh
------------------------------------------------------------------------
No package rtaudio-devel available.
Can only find one for Centos6

fix: https://centos.pkgs.org/6/linuxtech/librtaudio-4.0.11-4.el6.x86_64.rpm.html 
Install Howto
1. Create the repository config file /etc/yum.repos.d/linuxtech.repo:
2. [linuxtech]
    >name=LinuxTECH  
    >baseurl=http://pkgrepo.linuxtech.net/el6/release/  
    >enabled=1  
    >gpgcheck=1  
    >gpgkey=http://pkgrepo.linuxtech.net/el6/release/RPM-GPG-KEY-LinuxTECH.NET
3. Install librtaudio rpm package:
4. >\# yum install librtaudio

OpenISS/openFrameworks/scripts/linux/dependencies/el7.sh #cmake -DBUILD_SHARED_LIBS=TRUE ..
--------------------------------------------------------------------

-Looking for glXGetProcAddressEXT - not found

OpenISS/src/scripts/dependencies/el7.sh #yum install -y devtoolset-2-gcc-c++
-----------------------------------------------------------------------------
Error: Package: devtoolset-2-gcc-c++-4.8.2-15.1.el6.x86_64 (slc6-devtoolset)
           Requires: libgmp.so.3()(64bit)
Error: Package: devtoolset-2-gcc-4.8.2-15.1.el6.x86_64 (slc6-devtoolset)
           Requires: libgmp.so.3()(64bit)
Error: Package: devtoolset-2-gcc-4.8.2-15.1.el6.x86_64 (slc6-devtoolset)
           Requires: libmpfr.so.1()(64bit)
Error: Package: devtoolset-2-gcc-c++-4.8.2-15.1.el6.x86_64 (slc6-devtoolset)
           Requires: libmpfr.so.1()(64bit)
fix: skipped in el7
This package is supposed to add C++ to GCC 4.8, hopefully we already have a version of this installed.
g++ is installed.
\# yum install gmp-devel did not fix

el7.sh #yum install -y devtoolset-1.1-gcc-c++
-------------------------------------------------
no package available.
Fix: Skipped in el7


Error with building libfreenect2
-------------------------------------
-- using tinythread as threading library
-- Could NOT find TegraJPEG (missing: TegraJPEG_LIBRARIES TegraJPEG_INCLUDE_DIRS TegraJPEG_L4T_OK TegraJPEG_DRIVER_OK TegraJPEG_WORKS)
CMake Error at /usr/share/cmake/Modules/FindPackageHandleStandardArgs.cmake:108 (message):
Could NOT find TurboJPEG (missing: TURBOJPEG_WORKS)
Call Stack (most recent call first):
/usr/share/cmake/Modules/FindPackageHandleStandardArgs.cmake:315 (_FPHSA_FAILURE_MESSAGE)
cmake_modules/FindTurboJPEG.cmake:66 (FIND_PACKAGE_HANDLE_STANDARD_ARGS)
CMakeLists.txt:242 (FIND_PACKAGE)

Tried reinstalling turboJPEG and other directories but to no avail.

Open Frameworks Examples
------------------------------------------
Open Frameworks has a project generator tool to compile the given examples which is not currently working on Centos7.
Following the directions in INSTALL_FROM_GITHUB.md I have tried gcc6+, gcc5, and gcc4 project generator versions. 

The error running from either command line or the provided gui is:
Command failed: /bin/sh -c "/home/jacob/OpenISS/openFrameworks/projectGenerator/resources/app/app/projectGenerator" -r -o"/home/jacob/OpenISS/openFrameworks" -p"linux64" "../examples"
/bin/sh: /home/jacob/OpenISS/openFrameworks/projectGenerator/resources/app/app/projectGenerator: cannot execute binary file

My best guess for this error is that we can't execute the project generator file because it's a Mach-O 64-bit executable which won't run on linux - https://stackoverflow.com/questions/41505163/what-are-the-alternatives-for-running-a-mach-o-64-bit-binary-file-on-a-linux-mac

Following https://openframeworks.cc/setup/linux-install/ does not work either
1. ofx/scripts/linux/el6 ./install_dependecies.sh
2. ofx/scripts/linux ./compileOF.sh -j3
    + error: 'JPEG_EXIFRORATE' was not declared in this scope
    + error: 'JPEG_GREYSCALE' was not declared in this scope
    + Error 2
3. no makefile in ofx/examples/graphics/polygonExample
    + Skipping step
4. ofx/scripts/linux ./compilePG.sh
    + error: 'JPEG_EXIFRORATE' was not declared in this scope
    + error: 'JPEG_GREYSCALE' was not declared in this scope
    + Error 2
5. ofx/ projectGenerator
    + command not found

Manual compilation may still be possible.
```
[root@203-5-L issimagedrv]# make
make -C /lib/modules/3.10.0-862.14.4.el7.x86_64/build M=/home/student/Assignment3/OpenISS/src/api/c/issimagedrv modules
make[1]: Entering directory `/usr/src/kernels/3.10.0-862.14.4.el7.x86_64'
  Building modules, stage 2.
  MODPOST 1 modules
make[1]: Leaving directory `/usr/src/kernels/3.10.0-862.14.4.el7.x86_64'
make register
make[1]: Entering directory `/home/student/Assignment3/OpenISS/src/api/c/issimagedrv'
insmod ./issimagedrv.ko
insmod: ERROR: could not insert module ./issimagedrv.ko: File exists
make[1]: *** [register] Error 1
make[1]: Leaving directory `/home/student/Assignment3/OpenISS/src/api/c/issimagedrv'
make: *** [all] Error 2
```

ISS Image Driver
-----------------
```
[root@203-5-L issimagedrv]# ./driver-test
open(/dev/ISSIMAGEDRV) failed: No such file or directory

[root@203-5-L issimagedrv]# make register
insmod ./issimagedrv.ko
mknod /dev/ISSIMAGEDRV c  1
mknod: missing operand after ‘1’
Try 'mknod --help' for more information.
make: *** [register] Error 1

[root@203-5-L issimagedrv]# make register
insmod ./issimagedrv.ko
insmod: ERROR: could not insert module ./issimagedrv.ko: File exists
make: *** [register] Error 1
[root@203-5-L issimagedrv]# make clean
rm -f driver-test main.o issimagedrv.o
make -C /lib/modules/3.10.0-862.14.4.el7.x86_64/build M=/home/student/Assignment3/OpenISS/src/api/c/issimagedrv clean
make[1]: Entering directory `/usr/src/kernels/3.10.0-862.14.4.el7.x86_64'
  CLEAN   /home/student/Assignment3/OpenISS/src/api/c/issimagedrv/.tmp_versions
  CLEAN   /home/student/Assignment3/OpenISS/src/api/c/issimagedrv/Module.symvers
make[1]: Leaving directory `/usr/src/kernels/3.10.0-862.14.4.el7.x86_64'
rmmod issimagedrv
rm -f /dev/ISSIMAGEDRV
rm -f parent_image.ppd child_image.ppd


[root@203-5-L issimagedrv]# make register
insmod ./issimagedrv.ko
\#mknod /dev/ISSIMAGEDRV c  1
******To finish installing the driver type in:******
mknod /dev/ISSIMAGEDRV c `dmesg | tail -5 | grep 'ISSIMG' | sed s/"ISSIMG "/\/` 1
mknod: extra operand ‘238’
Try 'mknod --help' for more information.
make: *** [register] Error 1

Error while Building OpenNI2-FreenectDriver - RJ
---------------------------------------------
/usr/bin/ld: cannot find -lfreenectstatic
collect2: error: ld returned 1 exit status
make[2]: *** [lib/OpenNI2-FreenectDriver/libFreenectDriver.so.SOVERSION] Error 1
make[1]: *** [CMakeFiles/FreenectDriver.dir/all] Error 2
make: *** [all] Error 2
```
While trying to build the freenect error inside the This error was after I included multiple directories in the flags.make file

OSC ERROR
--------------
As part of issoscsender.c, the issue is that it never actually sends data. We originally thought that its because the receiver is not seeing anything. 
  int sockfd = socket(AF_INET, SOCK_DGRAM, 9000);
int mess = send(sockfd,buffer,len,0);
 printf("%i\n",mess); //mess prints out a -1
I assume that it could be either the file descriptor is wrong or that the sender and receiver has to be connected.
