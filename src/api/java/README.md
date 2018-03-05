# OpenISS Webservices #

This api is for for using the Microsoft Kinect sensor with Web Services. It uses the open source drivers and libraries from the [libfreenect](https://github.com/OpenKinect/libfreenect) and [OpenKinect-for-Processing](https://github.com/shiffman/OpenKinect-for-Processing) projects. 

Two methods currently supported:

* getFrame(String type) - Returns the current frame from a kinect device or a recorded session in JPEG blob format

* MixFrame(String type) - Reads a JPEG as input and returns a mixed frame from a kinect device or a recorded session

## Contributors ##

* Project Lead: Konstantinos Psimoulis
* Yasmine Chiter
* Inna Taushanova-Atanasova
* Amjrali Shirkhodaei
* Paul Palmieri

## Using Filesystem fakenect recorded stream ##

Edit src/api/java/openiss/ws/soap/endpoint/ServicePublisher.java and set the following:

* USE_FREENECT = true (To enable using libfreenect libraries)
* USE_FILESYSTEM = true (If you want to use a fakenect recorded stream without using libfakenect library)
* USE_FAKENECT = true (If you want to use a fakenect recorded stream using libfakenect library)

## Build Instructions ##

    Compile:
        mkdir build	
        javac -d build -classpath "./lib/java/jna.jar:./src/api/java" src/api/java/openiss/ws/soap/endpoint/ServicePublisher.java

    Run:		
        java -classpath build openiss.ws.soap.endpoint.ServicePublisher
        
    Run using a fakenect recorded stream:		
        FAKENECT_PATH="./session" java -classpath build openiss.ws.soap.endpoint.ServicePublisher       

    Clean:
        Delete directory build:
            rm -fr build


### Build Support ###

* Windows (Using Filesystem fakenect recorded stream)
* OS X (Using Real Kinect Device, Fakenect library or Filesystem fakenect recorded stream)
* Linux (Not yet tested)
