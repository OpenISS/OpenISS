# OpenISS

*Open Illimitable Space System*

OpenISS is inspired by its counterparts of ISS and ISSv2, but being built
from scratch using C and C++ and open-source libraries and toolkits for
sensors and creative coding. Various wrappers are being developed.
See background on the original ISS below.

## Contributors ##

* Project Lead: Serguei A. Mokhov

* Updates for Linux and C in EL6 (CentOS 6.x), CSI230-101 Fall 2017 course students teams:
  - Calum Phillips, Rosser Martinez, Matthew Roy
  - Alex Rader, Cory Smith, Nicholas Robbins

* Original build automation contributors for Linux in EL6 (CentOS 6.x), CSI230-101 Fall 2016 course students teams:
  - Brian Baron, Colin Brady, Robert Gentile
  - Gabriel Pereyra, Justin Mulkin, Duncan Carrol, Lucas Spiker 

* See detailed list [here](doc/CONTRIBUTORS.md).

## Build Instructions ##

### EL6 ###

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

* See [issues](https://github.com/OpenISS/OpenISS/issues).

### Build Support ###

* OS X
* Windows

### Wrappers ###

* Java
* C++
* Processing
* Max
* Python
* ...

## Background on the original ISS ##

OpenISS’s core goals are to enable achieving something akin
to the original ISS below in an open educational setting. Then,
to build custom applications based on it.

### Academic Publications ### #calls libfreenect cleanup in build.sh'
#struct OpenISS_Openfjdfdskjf and function pointers. changes from cpp to c face,frame,skeleton, gesture. move to api/c

* Origins are in Dr. Miao Song’s thesis:
  * http://arxiv.org/abs/1212.6250
* SIGGRAPH and SIGGRAPH Asia
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

* TravisCI EL6 and EL7 builds on Github:
  Derek Weitzel, Building CentOS packages on Travis-CI,
  https://djw8605.github.io/2016/05/03/building-centos-packages-on-travisci/
