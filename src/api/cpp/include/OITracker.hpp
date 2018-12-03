//
// Created by Haotao Lai on 2018-08-08.
//

#ifndef OPENISS_OITRACKER_H
#define OPENISS_OITRACKER_H

#include <vector>
#include <memory>
#include <NiTE.h>
#include "OIEnum.hpp"

using std::shared_ptr;
using std::vector;

namespace openiss {
class OITrackerFrame;
class OIUserData;
class OITracker;
class OIUserMap;
class OIFrame;
class OIDevice;

class OITrackerFactory {
public:
    shared_ptr<OITracker> createTracker(const char *tracName, OIDevice *pDev);
    shared_ptr<OITrackerFrame> createTrackerFrame(const char *tracName);
    ~OITrackerFactory();
private:
    bool mIsNiteInit = false;
};

/**
 * OITracker provides interface to all kind of trackers.
 * The main goal of tracker is to get skeleton:
 *    Tracker -> TrackerFrame -> UserData -> Skeleton
 */
class OITracker {
public:
    virtual ~OITracker() = default;

    virtual void startTracking() = 0;
    virtual void stopTracking() = 0;
    virtual void readFrame(OITrackerFrame *trackerFrame) = 0;

    virtual void mapJoint2Depth(float x, float y, float z, float *pOutX, float *pOutY) const = 0;
    virtual void mapDepth2Joint(int x, int y, int z, float *pOutX, float *pOutY) const = 0;
};

/**
 * OITrackerFrame defines the interface of a tracker
 * frame which should be able to provide a map contains
 * all the users' position and all the users' data
 * within the current frame
 */
class OITrackerFrame {
public:
    virtual ~OITrackerFrame() = default;
    virtual const vector<shared_ptr<OIUserData>> getUsers() = 0;
    virtual const OIUserData &getUserById(int userId) const = 0;
    virtual const OIUserMap &getUserMap() const = 0;

    virtual vector<JointType> &getSupportedJointType() = 0;
};

} // end of namespace
#endif //OPENISS_OITRACKER_H
