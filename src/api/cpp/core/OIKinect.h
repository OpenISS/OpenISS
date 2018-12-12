//
// Created by Haotao Lai on 2018-08-01.
//

#ifndef OPENISS_OIKINECT
#define OPENISS_OIKINECT

#include <iostream>
#include <OpenNI.h>
#include "../include/OIDevice.hpp"

namespace openiss {
class OIFrame;

class OIKinect : public openiss::OIDevice {

public:
    OIKinect();
    ~OIKinect();

    void *rawDevice() override;

    void init() override;
    void open() override;
    void close() override;

    bool enableColor() override;
    bool enableDepth() override;
    bool enableRegistered() override;
    bool enable() override;

    OIFrame *readFrame(StreamType typeFrame) override;
    Intrinsic getIntrinsic(StreamType streamType) override;
    Extrinsic getExtrinsic(StreamType from, StreamType to) override;
    float getDepthScale() override;

private:

    bool m_is_enable_rgb = false;
    bool m_is_enable_depth = false;
    bool m_is_enable_registered = false;

    OIFrame *mpColorOIFrame;
    OIFrame *mpDepthOIFrame;
    OIFrame *mpIrOIFrame;
    openni::VideoFrameRef mColorFrame;
    openni::VideoFrameRef mDepthFrame;
    openni::VideoFrameRef mIrFrame;

    openni::Device mDevice;
    openni::VideoStream *mpDepthStream;
    openni::VideoStream *mpIrStream;
    openni::VideoStream *mpColorStream;
    openni::Status mStatus;

    openni::VideoStream *getStream(int type);
};

} // end of namespace
#endif //OPENISS_OIKINECT
