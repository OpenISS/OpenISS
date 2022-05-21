# OpenISS Webservices #

This api is for for using the Microsoft Kinect sensor with Web Services. It uses the open source drivers and libraries from the [libfreenect](https://github.com/OpenKinect/libfreenect) and [OpenKinect-for-Processing](https://github.com/shiffman/OpenKinect-for-Processing) projects. 

Our SOAP Service currently works in 4 different ways: 

- Without any libraries and without fakenect recorded session using sample images (Default behavior since the other ways require to download a fakenect recorded session)
- Without any libraries using a FAKENECT_PATH from the file system
- With fakenect library using a FAKENECT_PATH
- With freenect library using the real Kinect device

Two methods currently supported:

* getFrame(String type) - Returns the current frame from a kinect device or a recorded session in JPEG blob format

* MixFrame(String type) - Reads a JPEG as input and returns a mixed frame from a kinect device or a recorded session

* doCanny(String type) - Reads a JPEG as input and returns bright lines representing the edges on a black background

* contour(String type) - Reads a JPEG as input and retrieves contours from the image

## Contributors ##

team10:
* Project Lead: Konstantinos Psimoulis
* Yasmine Chiter
* Inna Taushanova-Atanasova
* Amjrali Shirkhodaei
* Paul Palmieri

team11:
* Project Lead: Navid Golabian
* Mohammad-Ali Eghtesadi
* Behrooz Hedayati
* Piratheeban Annamalai
* Andrew Laramee

## Editing Config Values ##

Edit src/api/java/openiss/utils/OpenISSConfig.java and set the following depending on which implementation you want to use:

* SENSOR_TYPE = (FREENECT, FREENECT2, FAKENECT, STATIC_SENSOR)
* USE_OPENCV = (true, false)

## SOAP Build Instructions ##

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


### SOAP Build Support ###

* Windows (Using Filesystem fakenect recorded stream)
* OS X (Using Real Kinect Device, Fakenect library or Filesystem fakenect recorded stream)
* Linux (Not yet tested)

## REST Requirements ##
* Maven

### Using HTTP PATCH with JAX-RS
The @PATCH annotation is not supported by jax-rs, as a workaround we create our own annotation
having `@HttpMethod("PATCH")` as its own annotation. We can use it in the same way as the other 
provided jax-rs annotations.

## OpenCV 3.4.12 (Mac)
    brew install opencv@3

## OpenCV 3.4.16 (Mac M1)
Follow these steps to use the solution with an M1 processor:

Install opencv version 3.4.16:

    brew install opencv@3

You will also need to install JDK8, since jaxws does not currently work with JDK11+. Here's a download link to the jdk for M1 Macs:
https://cdn.azul.com/zulu/bin/zulu8.62.0.19-ca-jdk8.0.332-macosx_aarch64.dmg


After installing add the following command to your .bashrc or .zshrc then restart your terminal:

    export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre


## REST Build Instructions ##

    Compile:
        mvn install

    Run (Local Glassfish):
        mvn embedded-glassfish:run

    Run (Java OpencV Replica):
        mvn exec:java -Dexec.mainClass="openiss.ws.JavaReplica.javaReplica"
        
    Run (Using Docker with Tomcat7 image):
        cd tools/docker/rest-webserver
        docker-compose up

    Clean:
        mvn clean

## Test Rest API with Static Images

    Color (resources/color_example.jpg):
        http://localhost:8080/rest/openiss/color
     
    Depth (resources/depth_example.jpg):
        http://localhost:8080/rest/openiss/depth

## JavaScript Frontend

The ws-client folder within src/api/js contains a frontend implementation of a simple UI, to test the streams with OpenCV.

[JS Frontend](../js/ws-client)