//
// Created by Haotao Lai on 2018-08-01.
//

#include <NiteEnums.h>
#include <opencv2/core/mat.hpp>
#include "OIKinect.h"
#include "OIFrameCVImpl.h"

static void check_status(openni::Status status, const char *msg) {
    if (status != openni::STATUS_OK) {
        std::cout << msg << std::endl;
        std::cout << openni::OpenNI::getExtendedError() << std::endl;
        exit(1);
    }
}

openiss::OIKinect::OIKinect()
        : mpColorOIFrame(nullptr)
        , mpDepthOIFrame(nullptr)
        , mpIrOIFrame(nullptr)
        , mpColorStream(nullptr)
        , mpDepthStream(nullptr)
        , mpIrStream(nullptr)
        , mStatus(openni::STATUS_OK)
{ }

openiss::OIKinect::~OIKinect() {
    if (mpColorStream != nullptr) {
        mpColorStream->stop();
        mpColorStream->destroy();
    }
    if (mpDepthStream != nullptr) {
        mpDepthStream->stop();
        mpDepthStream->destroy();
    }
    if (mpIrStream != nullptr) {
        mpIrStream->stop();
        mpIrStream->destroy();
    }
    mDevice.close();
    mColorFrame.release();
    mDepthFrame.release();
    mIrFrame.release();
    openni::OpenNI::shutdown();

    delete mpColorStream;
    delete mpDepthStream;
    delete mpIrStream;
    delete mpColorOIFrame;
    delete mpDepthOIFrame;
    delete mpIrOIFrame;
}

void openiss::OIKinect::init() {

}

void openiss::OIKinect::close() {
    
}

void openiss::OIKinect::open() {
    mStatus = mDevice.open(openni::ANY_DEVICE);
    check_status(mStatus, "Device cannot be opened.");
}

bool openiss::OIKinect::enableColor() {
    if (!m_is_enable_rgb) {
        mpColorStream = new openni::VideoStream;
        mStatus = mpColorStream->create(mDevice, openni::SENSOR_COLOR);
        mpColorStream->start();
        check_status(mStatus, "RGB stream cannot be created.");
        m_is_enable_rgb = true;
        return true;
    }
    return false;
}

bool openiss::OIKinect::enableDepth() {
    if (!m_is_enable_depth) {
        mpDepthStream = new openni::VideoStream;
        mStatus = mpDepthStream->create(mDevice, openni::SENSOR_DEPTH);
        check_status(mStatus, "Depth stream cannot be created.");

        mpIrStream = new openni::VideoStream;
        mStatus = mpIrStream->create(mDevice, openni::SENSOR_IR);
        check_status(mStatus, "IR stream cannot be created.");

        mpDepthStream->start();
        mpIrStream->start();
        m_is_enable_depth = true;
        return true;
    }
    return false;
}

bool openiss::OIKinect::enableRegistered() {
    if (!m_is_enable_registered) {
        bool is_support = mDevice.isImageRegistrationModeSupported(
                openni::IMAGE_REGISTRATION_DEPTH_TO_COLOR);
        if (is_support) {
            mDevice.setImageRegistrationMode(
                    openni::IMAGE_REGISTRATION_DEPTH_TO_COLOR);
            m_is_enable_registered = true;
            return true;
        }
        std::cout << "[debug] current device doesn't support depth "
                     "to color registration." << std::endl;
    }
    return false;
}

bool openiss::OIKinect::enable() {
    enableColor();
    enableDepth();
    enableRegistered();
    return true;
}

openiss::OIFrame* openiss::OIKinect::readFrame(openiss::StreamType typeFrame) {
    if (typeFrame == openiss::COLOR_STREAM) {
        delete mpColorOIFrame;
        getStream(typeFrame)->readFrame(&mColorFrame);
        cv::Mat img(mColorFrame.getHeight(), mColorFrame.getWidth(),
                    CV_8UC3, (void *) mColorFrame.getData());
        cv::cvtColor(img, img, CV_RGB2BGR);
        mpColorOIFrame = new OIFrameCVImpl(img);
        return mpColorOIFrame;
    }
    if (typeFrame == openiss::DEPTH_STREAM) {
        delete mpDepthOIFrame;
        getStream(typeFrame)->readFrame(&mDepthFrame);
        cv::Mat img(mDepthFrame.getHeight(), mDepthFrame.getWidth(),
                    CV_16UC1, (void *) mDepthFrame.getData());
        img.convertTo(img, CV_8U, 255.0/(mpDepthStream->getMaxPixelValue()));
        cv::cvtColor(img, img, CV_GRAY2BGR);
        mpDepthOIFrame = new OIFrameCVImpl(img);
        return mpDepthOIFrame;
    }
    if (typeFrame == openiss::IR_STREAM) {
        delete mpIrOIFrame;
        getStream(typeFrame)->readFrame(&mIrFrame);
        cv::Mat img(mIrFrame.getHeight(), mIrFrame.getWidth(),
                    CV_16UC1, (void *) mIrFrame.getData());
        img.convertTo(img, CV_8U, 255.0/4000);
        cv::cvtColor(img, img, CV_GRAY2BGR);
        mpIrOIFrame = new OIFrameCVImpl(img);
        return mpIrOIFrame;
    }

    std::cout << "[debug] not supported steam type." << std::endl;
    exit(1);
}

openni::VideoStream* openiss::OIKinect::getStream(int type) {
    switch (type) {
        case COLOR_STREAM:
        case REGISTERED_STREAM:
            return mpColorStream;
        case DEPTH_STREAM:
            return mpDepthStream;
        case IR_STREAM:
            return mpIrStream;
        default:
            std::cout << "[debug] not supported steam type." << std::endl;
            exit(1);
    }
}

void* openiss::OIKinect::rawDevice() {
   return &mDevice;
}


openiss::Intrinsic openiss::OIKinect::getIntrinsic(openiss::StreamType streamType) {
    return openiss::Intrinsic();
}

openiss::Extrinsic openiss::OIKinect::getExtrinsic(openiss::StreamType from, openiss::StreamType to) {
    return openiss::Extrinsic();
}

float openiss::OIKinect::getDepthScale() {
    return 0;
}
