#include "CProtonectWrapper.h"

//

EXTERNC
Freenect2Device* Freenect2Device_create()
{
        return new Freenect2Device;
}

void Freenect2Device_start(Freenect2Device* instance)
{
        static_cast<Freenect2Device*>(instance)->start();
}

void Freenect2Device_stop(Freenect2Device* instance)
{
        static_cast<Freenect2Device*>(instance)->stop();
}

void Freenect2Device_setColorFrameListener(Freenect2Device* instance, SyncMultiFrameListener* listener)
{
        static_cast<Freenect2Device*>(instance)->setColorFrameListener(listener);
}

void Freenect2Device_startStreams(Freenect2Device* instance, int rgb, int depth)
{
        static_cast<Freenect2Device*>(instance)->startStreams(rgb, depth);
}

void Freenect2Device_getIrCameraParams(Freenect2Device* instance)
{
        //find return type of ir camera params, track that down via headers
        static_cast<Freenect2Device*>(instance)->getIrCameraParams();
}

void Freenect2Device_getColorCameraParams(Freenect2Device* instance)
{
        //find return type of color camera params
        static_cast<Freenect2Device*>(instance)->getColorCameraParams();
}
        
void Freenect2Device_setColorFrameListener(Freenect2Device* instance, SyncMultiFrameListener* listener)
{
        static_cast<Freenect2Device*>(instance)->setColorFrameListener(listener);
}
        
void Freenect2Device_setIrAndDepthFrameListener(Freenect2Device* instance, SyncMultiFrameListener* listener)
{
         static_cast<Freenect2Device*>(instance)->setIrAndDepthFrameListener(listener);
}

int Freenect2Device_getSerialNumber(Freenect2Device* instance)
{ 
        static_cast<Freenect2Device*>(instance)->getSerialNumber();  
}                                                              
void Freenect2Device_getFirmwareVersion(Freenect2Device* instance)
{
        static_cast<Freenect2Device*>(instance)->getFirmwareVersion();
}

void Freenect2Device_destroy(Freenect2Device* instance)
{
        delete static_cast<Freenect2Device*>(instance);
}
