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

## Contributors ##

* Project Lead: Konstantinos Psimoulis
* Yasmine Chiter
* Inna Taushanova-Atanasova
* Amjrali Shirkhodaei
* Paul Palmieri

## Using Filesystem fakenect recorded stream (SOAP Only) ##

Edit src/api/java/openiss/ws/soap/endpoint/ServicePublisher.java and set the following:

* USE_FREENECT = true (To enable using libfreenect libraries)
* USE_STATIC_IMAGES = true (Uses two static images color_example.jpg and depth_example.jpg)
* USE_FAKENECT = true (If you want to use a fakenect recorded stream using libfakenect library)

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


## REST Build Instructions ##

    Compile:
        mvn install

    Run (Local Glassfish):
        mvn embedded-glassfish:run
        
    Run (Using Docker with Tomcat7 image):
        cd tools/docker/rest-webserver
        docker-compose up

    Clean:
        mvn clean
