#include "CProtonectWrapper.h"

#include <stdio.h>
#include <stdlib.h>
#include <signal.h>

/// [headers]
/*Working on changing these headers*/
#include <libfreenect2/libfreenect2.hpp>
#include <libfreenect2/frame_listener_impl.h>
#include <libfreenect2/registration.h>
#include <libfreenect2/packet_pipeline.h>
#include <libfreenect2/logger.h>
/// [headers]

using namespace libfreenect2;

//

CFreenect2Device* libfreenect2_Freenect2Device_create()
{
	return 0;
        //return new libfreenect2::Freenect2Device;
}

EXTERNC
void Freenect2Device_start(CFreenect2Device* instance)
{
        reinterpret_cast<Freenect2Device*>(instance)->start();
}

EXTERNC
void Freenect2Device_stop(CFreenect2Device* instance)
{
        reinterpret_cast<Freenect2Device*>(instance)->stop();
}

/*
EXTERNC
void Freenect2Device_setColorFrameListener(Freenect2Device* instance, SyncMultiFrameListener* listener)
{
        static_cast<Freenect2Device*>(instance)->setColorFrameListener(static_cast<SyncMultiFrameListener*>(listener));
}

EXTERNC
void Freenect2Device_startStreams(Freenect2Device* instance, int rgb, int depth)
{
        static_cast<Freenect2Device*>(instance)->startStreams(rgb, depth);
}

EXTERNC
struct Freenect2Device_getIrCameraParams(Freenect2Device* instance)
{
        //find return type of ir camera params, track that down via headers
       return static_cast<Freenect2Device*>(instance)->getIrCameraParams();
}

EXTERNC
struct Freenect2Device_getColorCameraParams(Freenect2Device* instance)
{
        //find return type of color camera params
       return static_cast<Freenect2Device*>(instance)->getColorCameraParams();
}
       
EXTERNC 
void Freenect2Device_setColorFrameListener(Freenect2Device* instance, SyncMultiFrameListener* listener)
{
        static_cast<Freenect2Device*>(instance)->setColorFrameListener(static_cast<SyncMultiFrameListener*>(listener));
}

EXTERNC 
void Freenect2Device_setIrAndDepthFrameListener(Freenect2Device* instance, SyncMultiFrameListener* listener)
{
         static_cast<Freenect2Device*>(instance)->setIrAndDepthFrameListener(static_cast<SyncMultiFrameListener*>(listener));
}

EXTERNC
char* Freenect2Device_getSerialNumber(Freenect2Device* instance)
{ 
       return static_cast<Freenect2Device*>(instance)->getSerialNumber().c_str();  
}      

EXTERNC                                                        
char* Freenect2Device_getFirmwareVersion(Freenect2Device* instance)
{
       return static_cast<Freenect2Device*>(instance)->getFirmwareVersion().c_str();
}

EXTERNC
void Freenect2Device_destroy(Freenect2Device* instance)
{
        delete static_cast<Freenect2Device*>(instance);
}
*/

/////////////Freenect2
EXTERNC
CFreenect2* libfreenect2_Freenect2_create()
{
	Freenect2* freenect2 = new Freenect2();
        return reinterpret_cast<CFreenect2*>(freenect2);
}

/*
EXTERNC
void CFreenect2_enumerateDevices(CFreenect2* instance)
{
        static_cast<Freenect2*>(instance)->enumerateDevices();
}

EXTERNC
char* CFreenect2_getDefaultDeviceSerialNumber(CFreenect2* instance)
{
       return static_cast<Freenect2*>(instance)->getDefaultDeviceSerialNumber().c_str();
}

EXTERNC
void CFreenect2_openDevice(CFreenect2* instance, char* serial, CPacketPipeline* pipeline)
{
	static_cast<Freenect2*>(instance)->openDevice(serial.c_str(), static_cast<PacketPipeline*>(pipeline));
}

EXTERNC
void Freenect2_openDevice(CFreenect2* instance, char* serial)
{
        static_cast<Freenect2*>(instance)->openDevice(serial.c_str());
}

EXTERNC
void CFreenect2_stop(CFreenect2* instance)
{
        static_cast<Freenect2*>(instance)->stop();
}

EXTERNC
void CFreenect2_destroy(CFreenect2* instance)
{
        delete static_cast<Freenect2*>(instance);
}

CPacketPipeline* libfreenect2_PacketPipeline_create()
{
	return new PacketPipeline;
}

CPacketPipeline* libfreenect2_CpuPacketPipeline()
{
	return new CpuPacketPipeline;
}

CPacketPipeline* libfreenect2_COpenGLPacketPipeline()
{
        return new OpenGLPacketPipeline;
}

CPacketPipeline* libfreenect2_COpenCLPacketPipeline()
{
        return new OpenCLPacketPipeline;
}

CPacketPipeline* libfreenect2_COpenCLKdePacketPipeline()
{
        return new OpenCLKdePacketPipeline;
}

CPacketPipeline* libfreenect2_CCudaPacketPipeline()
{
        return new CudaPacketPipeline;
}

CPacketPipeline* libfreenect2_CCudaKdePacketPipeline()
{
        return new CudaKdePacketPipeline;
}

void CPacketPipeline_destroy(CPacketPipeline* instance)
{
	delete static_cast<PacketPipeline*>(instance);
}




//framemapEXTERNC
EXTERNC
CFrameMap* libfreenect2_CFrameMap_create()
{
        return new FrameMap;
}

EXTERNC
void CFrameMap_destroy(CFrameMap* instance)
{
        delete static_cast<FrameMap*>(instance);
}

//sync listener
EXTERNC
CSyncMultiFrameListener* libfreenect2_CSyncMultiFrameListener_create(int types)
{
        return new SyncMultiFrameListener(types);
}

EXTERNC
void CSyncMultiFrameListener_waitForNewFrame(CFrameMap* fm, int seconds)
{
	static_cast<SyncMultiFrameListener*>(instance)->waitForNewFrame(static_cast<FrameMap*>(fm), seconds);
}

EXTERNC
void CSyncMultiFrameListener_destroy(CSyncMultiFrameListener* instance)
{
        delete static_cast<SyncMultiFrameListener*>(instance);
}


//frame
EXTERNC
CFrame* libfreenect2_CFrame_create(int color, int ir, int depth)
{
        return new Frame(color, ir, depth);
}

EXTERNC
int CFrame_Color(Frame* instance)
{
       return static_cast<Frame*>(instance)->Color();
}

EXTERNC
int CFrame_Ir(Frame* instance)
{
       return static_cast<Frame*>(instance)->Ir();
}

EXTERNC
int CFrame_Depth(Frame* instance)
{
       return static_cast<Frame*>(instance)->Depth();
}

EXTERNC
void CFrame_destroy(Frame* instance)
{
	delete static_cast<Frame*>(instance);
}

//registration
EXTERNC
CRegistration* libfreenect2_CRegistration_create(CFreenect2Device* device)
{
	return new Registration(CFreenect2_getIrCameraParams(device), CFreenect2_getColorCameraParams(device));
}
//pass in device as parameter, call getters in here since theyre in c++ and it will handle the rest

EXTERNC
void CRegistration_apply(CRegistration* instance, CFrame* rgb, CFrame* depth, CFrame* undistort, CFrame* unreg)
{
	static_cast<Registration*>(instance)->apply(static_cast<Frame*>(rgb), static_cast<Frame*>(depth), static_cast<Frame*>(undistort), static_cast<Frame*>(unreg));
}

EXTERNC
void CRegistration_destroy(CRegistration* instance)
{
	delete static_cast<Registration*>(instance);
}


//viewer
EXTERNC
CViewer* CViewer_create()
{
	return new Viewer;
}

EXTERNC
void CViewer_initialize(CViewer* instance)
{
	static_cast<Viewer*>(instance)->initialize();
}

EXTERNC
void CViewer_addFrame(CViewer* instance, char* string, CFrame* frameToAdd)
{
	static_cast<Viewer*>(instance)->addFrame(string.c_str(), static_cast<Frame*>(frameToAdd));
}

EXTERNC
void CViewer_destroy(CViewer* instance)
{
	delete static_cast<Viewer*>(instance);
}

*/
