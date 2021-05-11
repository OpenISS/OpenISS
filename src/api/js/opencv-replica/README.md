# OpenISS Webservices #

This api is for for using the Microsoft Kinect sensor with Web Services. It uses the open source drivers and libraries from the [libfreenect](https://github.com/OpenKinect/libfreenect) and [OpenKinect-for-Processing](https://github.com/shiffman/OpenKinect-for-Processing) projects. 

There are two applications. Web FrontEnd and OpenCV Job Replica

For demo purposes we test static image from the OpenISS Java Backend. There are 10 static frames that can be accessed with the following URL:

E.g. For the first Frame

    http://localhost:8080/rest/openiss/getStaticFrame/1

## Contributors ##

* Project Lead: Konstantinos Psimoulis
* Gheith Abi-Nader
* Athanasios Babouras

## Requirements ##
* NodeJs 10.19

## Web FrontEnd Build Instructions ##

    Compile:
        npm install
        
    Run (Web FrontEnd):
        npm run frontend

    Run (Javascript replica):
        npm run replica

## Web FrontEnd Run

    http://localhost:3000

## Test GetJob Image from Replica

Make sure you are running the UDP sequencer and have already sent the image with the sequence number you are querying.

    E.g. For Sequence number 3:
        http://localhost:8003/getJob/3

## Patching ##

    Scan for security vulnerabilities:
        npm audit
    
    Fix security vulnerabilities:
        npm audit fix
    
    Note: If the issue cannot be fixed with command above, you will either need to patch manually or contact the package maintainer.

    If you want to check dependency tree for a specific package e.g. underscore:
        npm list underscore
     