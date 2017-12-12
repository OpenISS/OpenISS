#include <fstream>
#include <cstdlib>

#ifdef __cplusplus

using namespace libfreenect2;

class Freenect2Device;
class Freenect2;
class PacketPipeline;
class CpuPacketPipeline;
class OpenGLPacketPipeline;
class OpenCLPacketPipeline;
class OpenCLKdePacketPipeline;
class CudaPacketPipeline;
class CudaKdePiacketPipeline;
class FrameMap;
class SyncMultiFrameListener;
class Frame;
class Register;
class Viewer;
extern "C" {
#else
struct CFreenect2Device;
struct CFreenect2;
struct CPacketPipeline;
struct CFrameMap;
struct CSyncMultiFrameListener;
struct CFrame;
struct CRegister;
struct CViewer;
typedef struct CFreenect2Device;
typedef struct CFreenect2;
typedef struct CPacketPipeline;
typedef struct CFrameMap;
typedef struct CSyncMultiFrameListener;
typedef struct CFrame;
typedef struct CRegister;
typedef struct CViewer;
#endif

//LIST OF FUNCTIONS TO WRAP
//Freenect2Device
//{
//	 constructor
//	 start()
//	 stop()
//	 setColorFrameListener( takes the listener listed below)
//	 startStreams(enable_rgb, enable depth) ? I GUESS THESE ARE OPTIONS???
//	 getIrCameraParams() camelcase on IR noticed
//	 getColorCameraParams()
//	 getSerialNumber()
//	 getFirmwareVersion()
//	 destructor
//} 

CFreenect2Device* libfreenect2_CFreenect2Device_create();
void CFreenect2Device_start(CFreenect2Device* instance);
void CFreenect2Device_stop(CFreenect2Device* instance);
void CFreenect2Device_setColorFrameListener(CFreenect2Device* instance, CSyncMultiFrameListener* listener);
void CFreenect2Device_startStreams(CFreenect2Device* instance, int rgb, int depth);
struct CFreenect2Device_getIrCameraParams(CFreenect2Device* instance);
struct CFreenect2Device_getColorCameraParams(CFreenect2Device* instance);
void CFreenect2Device_setColorFrameListener(CFreenect2Device* instance, CSyncMultiFrameListener* listener);
void CFreenect2Device_setIrAndDepthFrameListener(CFreenect2Device* instance, CSyncMultiFrameListener* listener); 
char* CFreenect2Device_getSerialNumber(CFreenect2Device* instance);
char* CFreenect2Device_getFirmwareVersion(CFreenect2Device* instance);
void CFreenect2Device_destroy(CFreenect2Device* instance);

//Freenect2
//{
//	constructor
//	destructor
//	enumerateDevices()
//	getDefaultDeviceSerialNumber
//	openDevice(serial, pipeline)
//	openDevice(serial) (serial is a string so make it a char array)
//	stop()
//	close()
//}
//

CFreenect2* libfreenect2_Freenect2_create();
void CFreenect2_enumerateDevices(CFreenect2* instance);
char* CFreenect2_getDefaultDeviceSerialNumber(CFreenect2* instance);
void CFreenect2_openDevice(CFreenect2* instance, char* serial, CPacketPipeline* pipeline);
void CFreenect2_openDevice(CFreenect2* instance, char* serial);
void CFreenect2_stop(CFreenect2* instance);
void CFreenect2_destroy(CFreenect2* instance);


CPacketPipeline* libfreenect2_PacketPipeline_create();
CPacketPipeline* libfreenect2_CpuPacketPipeline();
CPacketPipeline* libfreenect2_COpenGLPacketPipeline();
CPacketPipeline* libfreenect2_COpenCLPacketPipeline();
CPacketPipeline* libfreenect2_COpenCLKdePacketPipeline();
CPacketPipeline* libfreenect2_CCudaPacketPipeline();
CPacketPipeline* libfreenect2_CCudaKdePacketPipeline();
void CPacketPipeline_destroy(CPacketPipeline* instance);

//functions that extend packetpipeline?? all constructors i think maybe?? whatever look into it
//	CpuPacketPipeline()
//	OpenGLPacketPipeline()
//	OpenCLPacketPipeline(deviceID)
//	OpenCLKdePacketPipeline(deviceID)
//	CudaPacketPipeline(deviceID)
//	CudaKdePiacketPipeline(deviceID)
//
//FrameMap
//{
//	constructor
//	destructor
//}




CFrameMap* libfreenect2_CFrameMap_create();
void CFrameMap_destroy(FrameMap* instance);

//SyncMultiFrameListener (types)
//{	
//	const/ des
//	waitForNewFrame(framemap, number of seconds 10 * 1000 for referece)
//	release(framemap)
//}

CSyncMultiFrameListener* libfreenect2_CSyncMultiFrameListener_create(int types);
void CSyncMultiFrameListener_waitForNewFrame(CSyncMultiFrameListener* instance, CFrameMap* fm, int seconds);
void CSyncMultiFrameListener_destroy(CSyncMultiFrameListener* instance);

//
//Frame
//{
//	contructor with 3 values, figure out what they mean: ascension 512, 424, 4   registered 521, 424, 4 IM LEAD TO BELIEVE THAT THIS IS COLOR, IR, AND DEPTH RESPECTIVELY
//	const/des
//	Color()
//	Ir
//	Depth
//}

CFrame* libfreenect2_CFrame_create(int color, int ir, int depth);
int CFrame_Color(CFrame* instance);
int CFrame_Ir(CFrame* instance);
int CFrame_Depth(CFrame* instance);
void CFrame_destroy(CFrame* instance);

//Registration

CRegistration* libfreenect2_CRegistration_create(CFreenect2Device* device); //ir and color
void CRegistration_apply(CRegistration* instance, CFrame* rgb, CFrame* depth, CFrame* undistort, CFrame* unreg);
void CRegistration_destroy(CRegistration* instance);

CViewer* CViewer_create();
void CViewer_initialize(CViewer* instance);
void CViewer_addFrame(CViewer* instance, char* string, Frame* frameToAdd);
void CViewer_destroy(CViewer* instance);


#ifdef __cplusplus
}
#endif
