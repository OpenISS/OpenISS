//
// Created by Haotao Lai on 2018-10-24.
//

#include "OIDataFrame.h"
#include "OIFrameCVImpl.h"

void openiss::OIDataFrame::createDisplayImg() {
    if (type == FrameType::DEPTH_FRAME) {
        cv::Mat img(height, width, CV_16U, mpData);
        displayCVImpl = new OIFrameCVImpl(img);
    }
    else if (type == FrameType::COLOR_FRAME) {
        cv::Mat img(height, width, CV_8UC3, mpData);
        displayCVImpl = new OIFrameCVImpl(img);
    }
    else if (type == FrameType::IR_FRAME) {
        cv::Mat img(height, width, CV_8UC1, mpData);
        displayCVImpl = new OIFrameCVImpl(img);
    }
    else {
        assert("not support frame type");
    }
}

openiss::OIDataFrame::OIDataFrame(FrameType type, void *data, int bpp, int width, int height)
  : type(type), mpData(data), bpp(bpp), width(width), height(height)
  , displayCVImpl(nullptr), hasDispImg(false)
{ }

void *openiss::OIDataFrame::getData() {
    return mpData;
}

int openiss::OIDataFrame::getBytesPerPixel() {
    return bpp;
}

int openiss::OIDataFrame::getHeight() const {
    return height;
}

int openiss::OIDataFrame::getWidth() const {
    return width;
}

void openiss::OIDataFrame::save(std::string fileName) {
    if (!hasDispImg) { createDisplayImg(); }
    displayCVImpl->save(fileName);
}

void openiss::OIDataFrame::show(const char *winName) {
    if (!hasDispImg) { createDisplayImg(); }
    displayCVImpl->show(winName);
}

void openiss::OIDataFrame::drawSkeleton(openiss::OISkeleton *pSkeleton, vector<JointType> &types) {
    if (!hasDispImg) { createDisplayImg(); }
    displayCVImpl->drawSkeleton(pSkeleton, types);
}

openiss::OIDataFrame::~OIDataFrame() {
    delete displayCVImpl;
}

