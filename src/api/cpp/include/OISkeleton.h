//
// OISkeleton.h
// Skeleton
//
// Created by Haotao Lai on 2018-08-08.
// Created by Jashanjot Singh on 2018-07-22.
//
// made into .c by Cameron

#ifndef OPENISS_OISKELETON_H
#define OPENISS_OISKELETON_H

#include <unordered_map>
#include <memory>
#include <vector>
#include "OIEnum.h"
#include "OITracker.h"
#include "OIFrame.h"

using std::unordered_map;
using std::shared_ptr;
using std::vector;

namespace openiss
{

struct OITracker;

/**
 * Point of the skeleton joint (real world coordinate)
 */
struct OISkeletonJoint {
public:
    OISkeletonJoint(){};
    OISkeletonJoint(float x, float y, float z, JointType type,
                    float row=-1.0f, float col=-1.0f);

    JointType type;
    float x, y, z;  // real world coordinate
    float row, col; // image coordinate
};

/**
 * A map associates the JointType to the Joint
 *   JointType -> shared_ptr<SkeletonJoint>
 */
struct OISkeleton
{
public:
    shared_ptr<openiss::OISkeletonJoint> getJointByType(JointType type);
    void addJointByType(JointType type, shared_ptr<OISkeletonJoint> jointPtr);
    void drawToFrame(OIFrame *displayFrame, vector<JointType> &supportedJoints);
    void mapWorld2Image(OITracker* tracker);
    void setSkeletonState(bool isAvailable);
    bool getSkeletonState() const;

private:
    unordered_map<JointType, shared_ptr<OISkeletonJoint>, std::hash<int>> mJoints;
    bool mIsSkeletonAvailable;
};

} // end of namespace
#endif //OPENISS_OISKELETON_H
