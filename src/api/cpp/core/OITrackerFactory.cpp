//
// Created by Haotao Lai on 2018-08-10.
//

#include <NiTE.h>
#include <iostream>
#include "../adapters/OINiTETracker.h"
#include "../include/OITracker.hpp"


openiss::OITrackerFactory::~OITrackerFactory() {

}

std::shared_ptr<openiss::OITracker>
openiss::OITrackerFactory::createTracker(const char *tracName, OIDevice *pDev) {
    if (strcmp(tracName, "nite") == 0) {
        if (!mIsNiteInit) {
            nite::NiTE::initialize();
            mIsNiteInit = true;
        }
        std::shared_ptr<OITracker> tracker(new OINiTETracker(*pDev));
        return tracker;
    }
    std::cout << "cannot create a nite tracker" << std::endl;
    exit(1);
}

shared_ptr<openiss::OITrackerFrame>
openiss::OITrackerFactory::createTrackerFrame(const char *tracName) {
    if (strcmp(tracName, "nite") == 0) {
        std::shared_ptr<OITrackerFrame> frame(new OINiTETrackerFrame);
        return frame;
    }
    std::cout << "cannot create a nite tracker frame" << std::endl;
    exit(1);
}

