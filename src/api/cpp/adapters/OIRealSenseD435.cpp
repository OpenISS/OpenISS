//
// Created by Haotao Lai on 2018-09-07.
//

#include <opencv2/core/mat.hpp>
#include "OIRealSenseD435.h"
#include "OIFrameCVImpl.h"


openiss::OIRealSenseD435::OIRealSenseD435()
    : mpColorOIFrame(nullptr)
    , mpDepthOIFrame(nullptr)
    , mpIrOIFrame(nullptr)
{ }

openiss::OIRealSenseD435::~OIRealSenseD435() {
    delete mpDepthOIFrame;
    delete mpColorOIFrame;
    delete mpIrOIFrame;
}

void openiss::OIRealSenseD435::init() {
    mConfig.enable_stream(RS2_STREAM_INFRARED, mFrameSize.width, mFrameSize.height,
                          RS2_FORMAT_Y8, 30);
    mConfig.enable_stream(RS2_STREAM_DEPTH, mFrameSize.width, mFrameSize.height,
                          RS2_FORMAT_Z16, 30);
    mConfig.enable_stream(RS2_STREAM_COLOR, mFrameSize.width, mFrameSize.height,
                          RS2_FORMAT_BGR8, 30);
     mProfile = mPipeline.start(mConfig);
}

void openiss::OIRealSenseD435::open() {
    init();
    mDepthProfile = mProfile.get_stream(rs2_stream::RS2_STREAM_DEPTH);
    mColorProfile = mProfile.get_stream(rs2_stream::RS2_STREAM_COLOR);
    mIrProfile = mProfile.get_stream(rs2_stream::RS2_STREAM_INFRARED);
    for (rs2::sensor &sensor : mProfile.get_device().query_sensors()) {
        if (rs2::depth_sensor dpt = sensor.as<rs2::depth_sensor>()) {
            depthScale = dpt.get_depth_scale();
            hasDepthScale = true;
            break;
        }
    }
}

openiss::OIFrame *openiss::OIRealSenseD435::readFrame(openiss::StreamType streamType) {
    mDataSrc = mPipeline.wait_for_frames();

    if (streamType == openiss::COLOR_STREAM) {
        delete mpColorOIFrame;

        mColorFrame = mDataSrc.get_color_frame();
        mpColorOIFrame = new OIDataFrame(
                FrameType::COLOR_FRAME,
                (void *) mColorFrame.get_data(),
                mColorFrame.as<rs2::video_frame>().get_bytes_per_pixel(),
                mFrameSize.width, mFrameSize.height
        );
        return mpColorOIFrame;
    }
    if (streamType == openiss::DEPTH_STREAM) {
        delete mpDepthOIFrame;
        mDepthFrame = mDataSrc.get_depth_frame();
        mpDepthOIFrame = new OIDataFrame(
                FrameType::DEPTH_FRAME,
                (void *) mDepthFrame.get_data(),
                mDepthFrame.as<rs2::video_frame>().get_bytes_per_pixel(),
                mFrameSize.width, mFrameSize.height
        );
        return mpDepthOIFrame;
    }
    if (streamType == openiss::REGISTERED_STREAM) {
        delete mpIrOIFrame;

        mIrFrame = mDataSrc.get_infrared_frame();
        mpIrOIFrame = new OIDataFrame(
                FrameType::IR_FRAME,
                (void *) mIrFrame.get_data(),
                mIrFrame.as<rs2::video_frame>().get_bytes_per_pixel(),
                mFrameSize.width, mFrameSize.height
        );
        return mpIrOIFrame;
    }

    return nullptr;
}

openiss::Intrinsic openiss::OIRealSenseD435::getIntrinsic(openiss::StreamType streamType) {
    rs2_intrinsics tmp;
    Intrinsic i;
    if (streamType == DEPTH_STREAM) {
        tmp = mDepthProfile.as<rs2::video_stream_profile>().get_intrinsics();
    }
    else if (streamType == COLOR_STREAM) {
        tmp = mColorProfile.as<rs2::video_stream_profile>().get_intrinsics();
    }
    else if (streamType == IR_STREAM) {
        tmp = mIrProfile.as<rs2::video_stream_profile>().get_intrinsics();
    }
    else {
        assert("not support stream type");
    }
    i.width = tmp.width;
    i.height = tmp.height;
    i.fx = tmp.fx;
    i.fy = tmp.fy;
    i.cx = tmp.ppx;
    i.cy = tmp.ppy;
    for (int c = 0; c < 5; c++) i.coeffs[c] = tmp.coeffs[c];
    return i;
}

openiss::Extrinsic openiss::OIRealSenseD435::getExtrinsic(openiss::StreamType from, openiss::StreamType to) {
    rs2::stream_profile fromStreamProfile;
    rs2::stream_profile toStreamProfile;
    switch (from) {
        case DEPTH_STREAM:fromStreamProfile = mDepthProfile;break;
        case COLOR_STREAM:fromStreamProfile = mColorProfile;break;
        case IR_STREAM:fromStreamProfile = mIrProfile;break;
        default: assert("not support stream type");break;
    }
    switch (to) {
        case DEPTH_STREAM:toStreamProfile = mDepthProfile;break;
        case COLOR_STREAM:toStreamProfile = mColorProfile;break;
        case IR_STREAM:toStreamProfile = mIrProfile;break;
        default: assert("not support stream type");break;
    }
    const rs2_extrinsics &tmp = fromStreamProfile.get_extrinsics_to(toStreamProfile);
    Extrinsic e;
    for (int i = 0; i < 9; i++) e.rotation[i] = tmp.rotation[i];
    for (int i = 0; i < 3; i++) e.translation[i] = tmp.translation[i];
    return e;
}

float openiss::OIRealSenseD435::getDepthScale() {
    if (!hasDepthScale) assert("this camera doesn't support depth");
    return depthScale;
}

/* -----> keep a copy in case refactor fail <---------
 *
openiss::OIFrame *openiss::OIRealSenseD435::readFrame(openiss::StreamType frameType) {
    mDataSrc = mPipeline.wait_for_frames();

    if (frameType == openiss::RGB_STREAM) {
        delete mpColorOIFrame;

        mColorFrame = mDataSrc.get_color_frame();
        cv::Mat img(mFrameSize, CV_8UC3, (void *) mColorFrame.get_data(), cv::Mat::AUTO_STEP);
        mpColorOIFrame = new OIFrameCVImpl(img);
        return mpColorOIFrame;
    }
    if (frameType == openiss::DEPTH_STREAM) {
        delete mpDepthOIFrame;

        mDepthFrame = mColorMap.process(mDataSrc.get_depth_frame());
        cv::Mat img(mFrameSize, CV_8UC3, (void *) mDepthFrame.get_data(), cv::Mat::AUTO_STEP);
        mpDepthOIFrame = new OIFrameCVImpl(img);
        return mpDepthOIFrame;
    }
    if (frameType == openiss::REGISTERED_STREAM) {
        delete mpIrOIFrame;

        mIrFrame = mDataSrc.get_infrared_frame();
        cv::Mat img(mFrameSize, CV_8UC1, (void *) mIrFrame.get_data(), cv::Mat::AUTO_STEP);
        mpIrOIFrame = new OIFrameCVImpl(img);
        return mpIrOIFrame;
    }

    return nullptr;
}
 */

// ***************************************************************** //
// the following methods won't have any effect, just respect the
// super class OIFrame, but will not affect the functionality
// ***************************************************************** //
bool openiss::OIRealSenseD435::enable() {
    return false;
}

bool openiss::OIRealSenseD435::enableRegistered() {
    return false;
}

bool openiss::OIRealSenseD435::enableDepth() {
    return false;
}

bool openiss::OIRealSenseD435::enableColor() {
    return false;
}

void openiss::OIRealSenseD435::close() {

}

void *openiss::OIRealSenseD435::rawDevice() {
    return nullptr;
}
// ***************************************************************** //

