//
// Created by Haotao Lai on 2018-08-01.
//

#include <opencv2/core/mat.hpp>
#include <opencv2/imgproc.hpp>
#include "include/OIDevice.h"
#include "include/OIUser.h"
#include "OINiTETracker.h"
#include "OITools.h"

// /////////////////////////////////////////////////////////////////////////////
// OINiTETracker class definition
// /////////////////////////////////////////////////////////////////////////////

openiss::OINiTETracker::OINiTETracker(OIDevice& pDev) {
    mpTracker = new nite::UserTracker;
    mStatus = mpTracker->create((openni::Device*) pDev.rawDevice());
    OIUtilities::checkStatus(mStatus, "cannot create user tracker");
}

openiss::OINiTETracker::~OINiTETracker() {
    mpTracker->destroy();
    delete mpTracker;
}

void openiss::OINiTETracker::startTracking() {
    
}

void openiss::OINiTETracker::stopTracking() {

}

void openiss::OINiTETracker::readFrame(OITrackerFrame *trackerFrame) {
    mpTracker->readFrame(&mNiteTrackerFrame);
    auto pFrame = dynamic_cast<OINiTETrackerFrame*>(trackerFrame);
    pFrame->update(mpTracker, mNiteTrackerFrame);
}

void openiss::OINiTETracker::mapJoint2Depth(
        float x, float y, float z, float *pOutX, float *pOutY) const {
    mpTracker->convertJointCoordinatesToDepth (x, y, z, pOutX, pOutY);
}

void openiss::OINiTETracker::mapDepth2Joint(
        int x, int y, int z, float *pOutX, float *pOutY) const {
    mpTracker->convertDepthCoordinatesToJoint(x, y, z, pOutX, pOutY);
}

// /////////////////////////////////////////////////////////////////////////////
// OINiTETrackerFrame class definition
// /////////////////////////////////////////////////////////////////////////////

openiss::OINiTETrackerFrame::OINiTETrackerFrame()
    : mpOIUserMap(nullptr)
{ prepareSupportedJoints(); }

openiss::OINiTETrackerFrame::~OINiTETrackerFrame() {
    delete mpOIUserMap;
}

const std::vector<shared_ptr<openiss::OIUserData>> openiss::OINiTETrackerFrame::getUsers() {
    mAllUsers.clear();
    for (auto it = mAvailableUsers.begin(); it != mAvailableUsers.end(); it++) {
        mAllUsers.push_back(it->second);
    }
    return mAllUsers;
}

const openiss::OIUserData &openiss::OINiTETrackerFrame::getUserById(int userId) const {
    auto entry = mAvailableUsers.find(userId);
    if (entry != mAvailableUsers.end()) {
        return *(entry->second);
    }
    std::cout << "Invalid user id" << std::endl;
    exit(-1);
}

const openiss::OIUserMap &openiss::OINiTETrackerFrame::getUserMap() const {
    return *mpOIUserMap;
}

void openiss::OINiTETrackerFrame::update(nite::UserTracker* tracker,
                                         nite::UserTrackerFrameRef& frameRef) {
    // update OIUserData
    const nite::Array<nite::UserData> &niteUsers = frameRef.getUsers();
    for (int i = 0; i < niteUsers.getSize(); ++i) {
        const nite::UserData &niteUser = niteUsers[i];

        // register this user into the available user map and start tracking
        if (niteUser.isNew()) {
            std::cout << "new user" << std::endl;
            shared_ptr<OIUserData> pOIUser(new OIUserData(niteUser.getId()));
            mAvailableUsers.insert({niteUser.getId(), pOIUser});
            tracker->startSkeletonTracking(niteUser.getId());
        }

        // remove from the the map and release the memory
        if (niteUser.isLost()) {
            auto lostUser = mAvailableUsers.find(niteUser.getId());
            if (lostUser != mAvailableUsers.end()) {
                lostUser->second.reset();
            }
        }

        // handle the skeleton data
        if (niteUser.isVisible()) {
            const nite::Skeleton &skeleton = niteUser.getSkeleton();
            shared_ptr<OIUserData> pOIUserData = mAvailableUsers[niteUser.getId()];
            OISkeleton *pOISkeleton = pOIUserData->getSkeleton();
            
            // update bounding box and center point
            Point3f *centerPoint = pOIUserData->getCenterOfMass();
            const nite::Point3f &niteCP = niteUser.getCenterOfMass();
            centerPoint->x = niteCP.x;
            centerPoint->y = niteCP.y;
            centerPoint->z = niteCP.z;
            Point3f *bdBox = pOIUserData->getBoundingBox();
            const nite::BoundingBox &box = niteUser.getBoundingBox();
            const NitePoint3f &maxP = box.max;
            const NitePoint3f &minP = box.min;
            bdBox[0].x = minP.x;
            bdBox[0].y = minP.y;
            bdBox[0].z = minP.z;
            bdBox[1].x = maxP.x;
            bdBox[1].y = maxP.y;
            bdBox[1].z = maxP.z;

            // update skeleton joints
            if (skeleton.getState() == nite::SKELETON_TRACKED) {
                nite::SkeletonJoint niteJoints[15];
                niteJoints[0] = skeleton.getJoint(nite::JOINT_HEAD);
                niteJoints[1] = skeleton.getJoint(nite::JOINT_NECK);
                niteJoints[2] = skeleton.getJoint(nite::JOINT_LEFT_SHOULDER);
                niteJoints[3] = skeleton.getJoint(nite::JOINT_RIGHT_SHOULDER);
                niteJoints[4] = skeleton.getJoint(nite::JOINT_LEFT_ELBOW);
                niteJoints[5] = skeleton.getJoint(nite::JOINT_RIGHT_ELBOW);
                niteJoints[6] = skeleton.getJoint(nite::JOINT_LEFT_HAND);
                niteJoints[7] = skeleton.getJoint(nite::JOINT_RIGHT_HAND);
                niteJoints[8] = skeleton.getJoint(nite::JOINT_TORSO);
                niteJoints[9] = skeleton.getJoint(nite::JOINT_LEFT_HIP);
                niteJoints[10] = skeleton.getJoint(nite::JOINT_RIGHT_HIP);
                niteJoints[11] = skeleton.getJoint(nite::JOINT_LEFT_KNEE);
                niteJoints[12] = skeleton.getJoint(nite::JOINT_RIGHT_KNEE);
                niteJoints[13] = skeleton.getJoint(nite::JOINT_LEFT_FOOT);
                niteJoints[14] = skeleton.getJoint(nite::JOINT_RIGHT_FOOT);

                for (int ii = 0; ii < 15; ii++) {
                    JointType &type = supportedJointType[ii];
                    const nite::Point3f &nitePos = niteJoints[ii].getPosition();
                    float row, col;
                    tracker->convertJointCoordinatesToDepth(
                            nitePos.x, nitePos.y, nitePos.z, &row, &col);
                    shared_ptr<OISkeletonJoint> joint(new OISkeletonJoint(
                            nitePos.x, nitePos.y, nitePos.z, type, row, col));
                    pOISkeleton->addJointByType(type, joint);
                }
                
                pOISkeleton->setSkeletonState(true);
            }
            else {
                pOISkeleton->setSkeletonState(false);
            }
        }

        // update OIUserMap
        delete mpOIUserMap;
        const nite::UserMap &niteUserMap = frameRef.getUserMap();
        mpOIUserMap = new OIUserMap(niteUserMap.getWidth(), niteUserMap.getHeight(),
                                    const_cast<short *>(niteUserMap.getPixels()));
    }
}


std::vector<openiss::JointType>& openiss::OINiTETrackerFrame::getSupportedJointType() {
    return supportedJointType;
}

void openiss::OINiTETrackerFrame::prepareSupportedJoints() {
    // the order must be maintained
    supportedJointType.push_back(JointType::JOINT_HEAD);
    supportedJointType.push_back(JointType::JOINT_NECK);
    supportedJointType.push_back(JointType::JOINT_LEFT_SHOULDER);
    supportedJointType.push_back(JointType::JOINT_RIGHT_SHOULDER);
    supportedJointType.push_back(JointType::JOINT_LEFT_ELBOW);
    supportedJointType.push_back(JointType::JOINT_RIGHT_ELBOW);
    supportedJointType.push_back(JointType::JOINT_LEFT_HAND);
    supportedJointType.push_back(JointType::JOINT_RIGHT_HAND);
    supportedJointType.push_back(JointType::JOINT_TORSO);
    supportedJointType.push_back(JointType::JOINT_LEFT_HIP);
    supportedJointType.push_back(JointType::JOINT_RIGHT_HIP);
    supportedJointType.push_back(JointType::JOINT_LEFT_KNEE);
    supportedJointType.push_back(JointType::JOINT_RIGHT_KNEE);
    supportedJointType.push_back(JointType::JOINT_LEFT_FOOT);
    supportedJointType.push_back(JointType::JOINT_RIGHT_FOOT);
}

