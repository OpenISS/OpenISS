/*
* Attempt to wrap OISkeleton for use in C code
* By Jacob Biederman 12-2018
* 
* Following http://blog.eikke.com/index.php/ikke/2005/11/03/using_c_classes_in_c.html
* Does not compile b/c of missing dependecy NiTE.h in OITracker.hpp.
* may have other errors.
*/

#include "../include/OISkeletonWrapper.h"
#include "../OISkeleton.hpp"

extern "C" {

COISkeleton * OISkeleton_new() {
    OISkeleton *skeleton = new OISkeleton();
    return (COISkeleton *)skeleton;
}

void OISkeleton_addJointByType(const COISkeleton *cSkeleton, JointType type, OISkeletonJoint *jointPtr){
    OISkeleton *skeleton = (OISkeleton *)cSkeleton;
    //This will probably cause problems but I don't know how else to do it.
    shared_ptr<OISkeletonJoint> jptr(jointPtr)
    skeleton->addJointByType(type, jptr);
}

void OISkeleton__drawToFrame(const COISkeleton *cSkeleton, OIFrame *displayFrame, JointType *supportedJoints){
// see https://stackoverflow.com/questions/8777603/what-is-the-simplest-way-to-convert-array-to-vector
    OISkeleton *skeleton = (OISkeleton *)cSkeleton;
    std::vector<JointType> vSupportedJoints(supportedJoints, supportedJoints + sizeof supportedJoints / sizeof supportedJoints[0]);
    skeleton->drawToFrame(displayFrame, vSupportedJoints);
}

void OISkeleton_mapWorld2Image(const COISkeleton *cSkeleton, OITracker* tracker){
    OISkeleton *skeleton = (OISkeleton *)cSkeleton;
    skeleton->mapWorld2Image(tracker);
}

void OISkeleton_setSkeletonState(const COISkeleton *cSkeleton, int isAvailable){
    OISkeleton *skeleton = (OISkeleton *)cSkeleton;
    skeleton->setSkeletonState((bool)isAvailable);
}

int getSkeletonState(const COISkeleton *cSkeleton) const{
    OISkeleton *skeleton = (OISkeleton *)cSkeleton;
    return (int)skeleton->getSkeletonState();
}
}
