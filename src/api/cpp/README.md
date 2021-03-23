# README

This repo. is part of Eric's thesis project, all the code 
should be tested here and then merge to the official repo.

All code here are compiled by `CMake`, the minimum version
requirement is `3.8`, if you don't have can download from
[here](https://cmake.org/download/)

*If you reall don't want to upgrade. It has been tested that can work with `CMake 3.4`*

## Milestones

- 2018-08-30, we are able to do skeleton tracking via OpenISS API
- 2018-08-09, we can get both color and depth image from both Kinect v1 and v2
  via a common interface (in the other words, the same code can be used for both two types of device).

## Build instruction

The instruction here take "user_track_example" as an example,
try to run the program with Kinect v2.

### Install needed libraries

First of all, make sure you have all the libraries installed:

- [OpenNI2](https://github.com/occipital/OpenNI2)
- [NiTE2](https://bitbucket.org/kaorun55/openni-2.2/src/master/NITE%202.2%20%CE%B1/)
- [OpenCV](https://opencv.org/releases.html) (Not useful for "user_track_example",
   but we need it later, a build instruction
   can be found [here](https://www.learnopencv.com/install-opencv3-on-ubuntu/))
- [Freenect2](https://github.com/OpenKinect/libfreenect2) (should be built
   with openni supported, support kinect v2)
- [Freenect](https://github.com/OpenKinect/libfreenect) (support kinect v1)


If you are in a Mac, make sure you have the openni2 and nite2 shared 
libraries (or symbol link) under path: `/usr/lib/`

How to build the above libs from source code, please refer to the specific
project website for instructions. Need to note that when you finish building
the freenect (or freenect2) lib, need to copy the `libfreenect2-openni2.0.dylib`
(take freenect2 as example) file to the director `your_openni2_lib_path/OpenNI2/Driver/`.

After you get all those libs, you need to manually copy the `NiTE2/` folder of
NiTE2 lib which contains the pre-trained data into the root directory of the
current project.

### Environment variables

If you get a error like some libs cannot be found, for example, OpenNI or NiTE.
A good way to solve it is define some environment variables. In the cmake moudle
files, it will search for the following environment vars:

- NITE2_INCLUDE
- NITE2_REDIST
- OPENNI2_INCLUDE
- OPENNI2_REDIST

Those variables can be accessed after you run the "install.sh" script comes with the
lib's repo. After running the script, you should find a file in the same folder with 
the name "*EnvironmentVariable".

```
// command to add environment variables
export NITE2_INCLUDE=your_nite_include_path
```

### Build

Then do the following:

1. cd into the root directory of the project
2. make a "build/" folder and then cd into the folder
3. cmake ..
4. make
5. make install
6. ./user_track_example/user_track_example
