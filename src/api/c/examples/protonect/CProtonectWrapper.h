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
extern "C" {
#else
struct Freenect2Device;
struct Freenect2;
struct PacketPipeline;
struct CpuPacketPipeline;
struct OpenGLPacketPipeline;
struct OpenCLPacketPipeline;
struct OpenCLKdePacketPipeline;
struct CudaPacketPipeline;
struct CudaKdePiacketPipeline;
struct FrameMap;
struct SyncMultiFrameListener;
struct Frame;
struct Register;
typedef struct Freenect2Device;
typedef struct Freenect2;
typedef struct PacketPipeline;
typedef struct CpuPacketPipeline;
typedef struct OpenGLPacketPipeline;
typedef struct OpenCLPacketPipeline;
typedef struct OpenCLKdePacketPipeline;
typedef struct CudaPacketPipeline;
typedef struct CudaKdePiacketPipeline;
typedef struct FrameMap;
typedef struct SyncMultiFrameListener;
typedef struct Frame;
typedef struct Register;
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

Freenect2Device* Freenect2Device_create();
void Freenect2Device_start(Freenect2Device* instance);
void Freenect2Device_stop(Freenect2Device* instance);
void Freenect2Device_setColorFrameListener(Freenect2Device* instance, SyncMultiFrameListener* listener);
void Freenect2Device_startStreams(Freenect2Device* instance, int rgb, int depth);
void Freenect2Device_getIrCameraParams(Freenect2Device* instance);
void Freenect2Device_getColorCameraParams(Freenect2Device* instance);
void Freenect2Device_setColorFrameListener(Freenect2Device* instance, SyncMultiFrameListener* listener);
void Freenect2Device_setIrAndDepthFrameListener(Freenect2Device* instance, SyncMultiFrameListener* listener); 
int Freenect2Device_getSerialNumber(Freenect2Device* instance);
void Freenect2Device_getFirmwareVersion(Freenect2Device* instance);
void Freenect2Device_destroy(Freenect2Device* instance);

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
//PacketPipeline
//{
//	constructor
//	destructor
//}
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
//
//SyncMultiFrameListener (types)
//{	
//	const/ des
//	waitForNewFrame(framemap, number of seconds 10 * 1000 for referece)
//	release(framemap)
//}
//
//Frame
//{
//	contructor with 3 values, figure out what they mean: ascension 512, 424, 4   registered 521, 424, 4 IM LEAD TO BELIEVE THAT THIS IS COLOR, IR, AND DEPTH RESPECTIVELY
//	const/des
//	Color()
//	Ir
//	Depth
//}
//
//Registration
//{
//	constructor TAKES IRCAMERAPARAMS, COLORCAMERAPARAMS
//	apply(Frame for rgb, frame for depth, earlier instantiated frame, earlier instantiated frame) (4 frames total as params)
//	destructor
//}
//



#ifdef __cplusplus
}
#endif
