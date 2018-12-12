//
// Created by Haotao Lai on 2018-08-23.
//

#include <iostream>
#include "../include/OISkeleton.hpp"

// /////////////////////////////////////////////////////////////////////////////
// OISkeleton class definition
// /////////////////////////////////////////////////////////////////////////////

void openiss::OISkeleton::addJointByType(JointType type, shared_ptr<OISkeletonJoint> jointPtr) {
    auto search = mJoints.find(type);
    if (search != mJoints.end()) {
        mJoints.erase(type);
    }
    mJoints.insert({type, jointPtr});
}

shared_ptr<openiss::OISkeletonJoint> openiss::OISkeleton::getJointByType(JointType type) {
    auto res = mJoints.find(type);
    if (res != mJoints.end()) {
        return res->second;
    }
    return nullptr;
}

void openiss::OISkeleton::drawToFrame(openiss::OIFrame *displayFrame, std::vector<openiss::JointType> &supportedJoints){
    displayFrame->drawSkeleton(this, supportedJoints);
}

void openiss::OISkeleton::mapWorld2Image(openiss::OITracker* tracker) {
    for (const auto &entry : mJoints) {
        const shared_ptr<OISkeletonJoint> &joint = entry.second;
        tracker->mapJoint2Depth(joint->x, joint->y, joint->z, &(joint->row), &(joint->col));
    }
}

void openiss::OISkeleton::setSkeletonState(bool isAvailable) {
    mIsSkeletonAvailable = isAvailable;
}

bool openiss::OISkeleton::getSkeletonState() const {
    return mIsSkeletonAvailable;
}

// /////////////////////////////////////////////////////////////////////////////
// OISkeletonJoint class definition
// /////////////////////////////////////////////////////////////////////////////

openiss::OISkeletonJoint::OISkeletonJoint(float x, float y, float z, JointType t, float row, float col)
    : x(x), y(y), z(z), type(t), row(row), col(col)
{ }
