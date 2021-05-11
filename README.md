# OpenISS

*Open Illimitable Space System*

OpenISS is a motion capture data aggregation and delivery framwork for VFX that
has library instances abstracting various middleware and cameras for many application types.

Applications include AI+Art, performing arts, interactive film, facial animation,
NUI applications using OpenGL, Processing, TensorFlow, Keras, and MARF.

The OpenISS framework provides a uniform abstraction layer over libfreenect,
libfreenect2, librealsense, OpenNI2, NiTE2, NuiTrack, and ROS and has
REST and SOAP web services. There are different front-end modules as well based on OpenGL, Processing, and web browsers (JS).

## History ##

OpenISS is inspired by the development of ISSv1, ISSv2, ISSv3 and MultiCamTk++, but is being built
from scratch using C/C++ and various wrappers and uses similar or same open-source libraries, middleware and toolkits for
sensors and creative coding. Various wrappers are being developed. See background on the inspirational ISS below.
OpenISS API is likewise for the first time made it possble to access Kinect 1 and 2
as well as OpenCV as REST and SOAP services for creative near-realtime online broadcasting.
OpenISS is poised to be the core replacement for ISSv2's pipeline. It also serves as
an educational tool for graduate and undergraduate students in computer vision, computation
arts, pattern recognition, AI, machine learning, and game development. It is designed
to be portable.

- OpenISS component development began in 2016 in C
- Expanded to C++ and Java since and ongoing

## Contributors ##

* Project Lead: [Serguei A. Mokhov](https://github.com/smokhov)

### Current ###

* [Jashanjot Singh](https://github.com/jashanj0tsingh): Gesture and Hand Tracking Framework, NiTE2/NuiTrack, and ROS support, SWIG support for Java/Processing
* [Haotao (Eric) Lai](https://github.com/laihaotao): Person Re-identifcation, Green Screening, device abstracion (Kinects 1 and 2, RealSense D435), TensorFlow/Keras/Python abstractions; Kinect 2 web service
* [Yiran (Bernie) Shen](https://github.com/UNO998): Facial Recognition and data (landmarks and expressions) provider framework abstraction over OpenFace, OpenCV, including TensortFlow for VFX
* [Konstantinos (Kosta) Psimoulis](https://github.com/kostapsimoulis): Web Services lead (REST and SOAP), SimpleOpenNIRS/liblrealsense2 support
* [Jonathan Llewellyn](https://github.com/inexistenz): actvity recognition, VFX pipeline, softbody linkage
* Yuhao Mao: Art and AI, Sound Visualization, Style Transfer
* [Chao Wang](https://github.com/chaowangCanada): Style Transfer, Magenta support, motion-based audio support, finger tracking
* Zihao Song: Singnal Processing for Audio and Image Processing effects and MARF

### Web Services ###

* See [here](https://github.com/OpenISS/OpenISS/tree/master/src/api/java) for more documentation.

### Early C API and Linux Builds ###

* See detailed list [here](doc/CONTRIBUTORS.md).
* Updates for Linux and C in EL6 (CentOS 6.x), CSI230-101 Fall 2017 course students teams:
  - Calum Phillips, Rosser Martinez, Matthew Roy
  - Alex Rader, Cory Smith, Nicholas Robbins

* Original build automation contributors for Linux in EL6 (CentOS 6.x), CSI230-101 Fall 2016 course students teams:
  - Brian Baron, Colin Brady, Robert Gentile
  - Gabriel Pereyra, Justin Mulkin, Duncan Carrol, Lucas Spiker 

## Build Instructions ##

### Docker ###

* We are working on our Docker images [here](https://hub.docker.com/u/openiss)

### Ubuntu ###

* Please use `cmake`
* More documentation to follow

### MacOS ###

* Please use `cmake`
* More documentation to follow

### EL7 ###

EL7 (RHEL, CentOS, Scientific Linux) are as of September 2018 default and
preferred build, so the development effort focuses around this platform,
but it is known to run on macOS and Ubuntu.

### EL6 ###

EL6 is now considered legacy.

Notice, EL6 requires a newer kernel for
proper USB3 and NVIDIA support. If you prefer
to install dependencies manually, you can follow
the scripts referenced here (`build.sh` and `el6.sh`) and repeat their
relevant steps one by one.

	Install:
			yum install git
			git clone https://github.com/OpenISS/OpenISS.git
			git submodule update --init --recursive
			Navigate to OpenISS/src
			Run the command:
				make deps

			This will install all of the 3rd party
			dependencies

	Compile:	
			cd OpenISS/src
			make

	Clean:		
			Navigate to OpenISS/src
			Run the command:
				make clean

			Removes the installed dependencies

## TODO ##

* See [issues](https://github.com/OpenISS/OpenISS/issues)
* See [pull requests](https://github.com/OpenISS/OpenISS/pulls)

### Build Support ###

* OS X (mostly there)
* Windows (partially there for some components)

### Wrappers ###

* Java (in progress)
* C++ (mostly there)
* Processing (in progress)
* Max (in progress)
* PureData (in progress)
* Python (started)
* ...

## Background on the ISSv1, ISSv2, ISSv3 ##

OpenISS’s core goals are to enable achieving something akin
to the original ISS below in an open educational setting. Then,
to build custom applications based on it, using OpenISS API
as a core.

### Academic Publications ###

* Origins are in Dr. Miao Song’s thesis:
  * http://arxiv.org/abs/1212.6250
* SIGGRAPH and SIGGRAPH Asia
  * https://doi.org/10.1145/3355047.3359423
  * https://doi.org/10.1145/3084863.3084864
  * https://doi.org/10.1145/2988458.2988460
  * https://doi.org/10.1145/2992138.2992139
  * https://doi.org/10.1145/2668947.2668953
  * https://doi.org/10.1145/2820926.2820940
* JIDPS
  * https://doi.org/10.3233/jid-2016-0026
* GEM
  * http://dx.doi.org/10.1109/GEM.2015.7377204
  * http://dx.doi.org/10.1109/GEM.2015.7377247
* VSMM
  * http://dx.doi.org/10.1109/VSMM.2014.7136675
* IDEAS
  * http://doi.acm.org/10.1145/3105831.3105862

### Videos ###

* https://www.youtube.com/watch?v=yjH0S7Otwj4
* https://vimeo.com/169321384
* https://vimeo.com/169319502
* https://vimeo.com/166999333
* https://vimeo.com/153062959
* https://vimeo.com/152476114
* https://vimeo.com/151683852
* https://vimeo.com/141811579
* https://vimeo.com/141081567
* https://vimeo.com/130122925
* https://vimeo.com/129692753
* https://vimeo.com/121177927
* https://vimeo.com/96355443
* https://vimeo.com/85049604
* https://vimeo.com/68347351
* https://vimeo.com/51329588
* https://vimeo.com/50069419
* https://vimeo.com/49682696
* https://vimeo.com/49399617

## References and Acknowledgements ##

* TravisCI EL6 and EL7 builds on GitHub:
  Derek Weitzel, Building CentOS packages on Travis-CI,
  https://djw8605.github.io/2016/05/03/building-centos-packages-on-travisci/
