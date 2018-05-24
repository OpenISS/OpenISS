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

    Run (SOAP Service):		
        npm start

    Run (REST Service):		
        npm run rest
    
    Browse to http://localhost:3000
    
    Example to get a color frame:    
        http://localhost:3000/getFrame/color
    
    Example to get a depth frame:
        http://localhost:3000/getFrame/depth
    
    Example to get a mix frame of type color with example.jpg using operand +
        http://localhost:3000/mixFrame/example&color&+

    Example to do a rest api call to /rest/openiss/hello:
        http://localhost:3000/api/hello

    Clean:
        Delete directory node_modules:
            rm -fr node_modules
