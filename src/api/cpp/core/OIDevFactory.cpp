//
// Created by Haotao Lai on 2018-08-09.
//

#include "../include/OIDevice.hpp"
#include "OIKinect.h"
// #include "../adapters/OIRealSenseD435.h"

std::shared_ptr<openiss::OIDevice> openiss::OIDevFactory::create(const char *devName) {
    if (strcmp(devName, "kinect") == 0) {
        if (!mIsOpenNIInit) {
            openni::OpenNI::initialize();
            mIsOpenNIInit = true;
        }
        std::shared_ptr<OIDevice> pDev(new OIKinect);
        return pDev;
    }
    // else if (std::strcmp(devName, "rs_d435") == 0) {
    //     std::shared_ptr<OIDevice> pDev(new OIRealSenseD435);
    //     return pDev;
    // }
    // std::cout << "cannot create a Realsense-D435 device" << std::endl;
    // exit(1);
}
