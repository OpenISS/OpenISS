#include "CProtonectWrapper.h"

//

EXTERNC
CFreenect2Device* libfreenect2_CFreenect2Device_create()
{
        return new Freenect2Device;
}

EXTERNC
void CFreenect2Device_start(CFreenect2Device* instance)
{
        static_cast<Freenect2Device*>(instance)->start();
}

EXTERNC
void CFreenect2Device_stop(CFreenect2Device* instance)
{
        static_cast<Freenect2Device*>(instance)->stop();
}

EXTERNC
void CFreenect2Device_setColorFrameListener(CFreenect2Device* instance, CSyncMultiFrameListener* listener)
{
        static_cast<Freenect2Device*>(instance)->setColorFrameListener(static_cast<SyncMultiFrameListener*>(listener));
}

EXTERNC
void CFreenect2Device_startStreams(CFreenect2Device* instance, int rgb, int depth)
{
        static_cast<Freenect2Device*>(instance)->startStreams(rgb, depth);
}

EXTERNC
struct CFreenect2Device_getIrCameraParams(CFreenect2Device* instance)
{
        //find return type of ir camera params, track that down via headers
       return static_cast<Freenect2Device*>(instance)->getIrCameraParams();
}

EXTERNC
struct CFreenect2Device_getColorCameraParams(CFreenect2Device* instance)
{
        //find return type of color camera params
       return static_cast<Freenect2Device*>(instance)->getColorCameraParams();
}
       
EXTERNC 
void CFreenect2Device_setColorFrameListener(CFreenect2Device* instance, CSyncMultiFrameListener* listener)
{
        static_cast<Freenect2Device*>(instance)->setColorFrameListener(static_cast<SyncMultiFrameListener*>(listener));
}
       
EXTERNC 
void CFreenect2Device_setIrAndDepthFrameListener(CFreenect2Device* instance, CSyncMultiFrameListener* listener)
{
         static_cast<Freenect2Device*>(instance)->setIrAndDepthFrameListener(static_cast<SyncMultiFrameListener*>(listener));
}

EXTERNC
char* CFreenect2Device_getSerialNumber(CFreenect2Device* instance)
{ 
       return static_cast<Freenect2Device*>(instance)->getSerialNumber().c_str();  
}      

EXTERNC                                                        
char* CFreenect2Device_getFirmwareVersion(CFreenect2Device* instance)
{
       return static_cast<Freenect2Device*>(instance)->getFirmwareVersion().c_str();
}

EXTERNC
void CFreenect2Device_destroy(CFreenect2Device* instance)
{
        delete static_cast<Freenect2Device*>(instance);
}

/////////////Freenect2
EXTERNC
CFreenect2* libfreenect2_CFreenect2_create()
{
        return new Freenect2;
}

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

