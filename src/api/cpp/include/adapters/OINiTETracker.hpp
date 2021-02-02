//
// Created by Haotao Lai on 2018-08-01.
//

#ifndef OPENISS_OINITETRACKER_H
#define OPENISS_OINITETRACKER_H

#include <iostream>
#include <unordered_map>
#include <vector>
#include <NiTE.h>
#include "../include/OITracker.hpp"

using std::shared_ptr;

namespace openiss {
class OINiTETracker : public openiss::OITracker {
public:
    explicit OINiTETracker(OIDevice &pDev);
    ~OINiTETracker();

    void startTracking() override;
    void stopTracking() override;
    void readFrame(OITrackerFrame *trackerFrame) override;

    void mapJoint2Depth(float x, float y, float z, float *pOutX, float *pOutY) const override;
    void mapDepth2Joint(int x, int y, int z, float *pOutX, float *pOutY) const override;

private:
    nite::UserTracker *mpTracker;
    nite::UserTrackerFrameRef mNiteTrackerFrame;
    nite::Status mStatus;

    // disable copy constructor and copy assignment
    OINiTETracker(const OINiTETracker &pTracker);
    OINiTETracker &operator=(OINiTETracker &);
};

class OINiTETrackerFrame : public OITrackerFrame {
public:
    OINiTETrackerFrame();
    ~OINiTETrackerFrame();

    const std::vector<shared_ptr<OIUserData>> getUsers() override;
    const OIUserData &getUserById(int userId) const override;
    const OIUserMap &getUserMap() const override;
    std::vector<JointType> &getSupportedJointType() override;
    void update(nite::UserTracker *tracker, nite::UserTrackerFrameRef &frameRef);

private:
    std::unordered_map<int, shared_ptr<OIUserData>> mAvailableUsers;
    std::vector<shared_ptr<OIUserData>> mAllUsers;
    std::vector<JointType> supportedJointType;
    OIUserMap *mpOIUserMap;
    void prepareSupportedJoints();

    OINiTETrackerFrame(const OINiTETrackerFrame &frame);
    OINiTETrackerFrame &operator=(const OINiTETrackerFrame &frame);
};
} // end of namespace
#endif //OPENISS_OINITETRACKER_H
