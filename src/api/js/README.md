# OpenISS Webservices #

This is a javascript SOAP Client for OpenISS Webservices

Two methods currently supported:

* getFrame(String type) - Returns the current frame from a kinect device or a recorded session in JPEG blob format

* MixFrame(String type) - Reads a JPEG as input and returns a mixed frame from a kinect device or a recorded session

## Contributors ##

* Project Lead: Konstantinos Psimoulis
* Yasmine Chiter
* Inna Taushanova-Atanasova
* Amjrali Shirkhodaei
* Paul Palmieri

## Build Instructions ##

    Compile:
        npm install

    Run:		
        node soapclient.js
    
    Browse to http://localhost:8080
    
    Example to get a color frame:    
        http://localhost:8080/getFrame/color
    
    Example to get a depth frame:
        http://localhost:8080/getFrame/depth
    
    Example to get a mix frame of type color with example.jpg using operand +
        http://localhost:8080/mixFrame/example&color&+

    Clean:
        Delete directory node_modules:
            rm -fr node_modules
