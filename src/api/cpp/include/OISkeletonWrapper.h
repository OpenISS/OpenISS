/*
* Attempt to wrap OISkeleton for use in C code
* By Jacob Biederman 12-2018
* 
* Following http://blog.eikke.com/index.php/ikke/2005/11/03/using_c_classes_in_c.html
* Does not compile b/c of missing dependecy NiTE.h in OITracker.hpp.
* may have other errors.
*/
#ifndef OPENISS_SKELETON_WRAPPER
#define OPENISS_SKELETON_WRAPPER

#include "OIEnum.hpp"
#include "OITracker.hpp"
#include "OIFrame.hpp"

typedef void COISkeleton;

#ifdef __cplusplus
extern "C"{
#endif
COISkeleton *OISkeleton_new();
//not sure if these types will work or if include needs to be changed.
void OISkeleton_addJointByType(const COISkeleton *cSkeleton, JointType type, OISkeletonJoint *jointPtr);
//need to convert supportedJoints to Vector
void OISkeleton_drawToFrame(const COISkeleton *cSkeleton, OIFrame *displayFrame, JointType *supportedJoints);

void OISkeleton_mapWorld2Image(const COISkeleton *cSkeleton, OITracker* tracker);
void OISkeleton_setSkeletonState(const COISkeleton *cSkeleton, int isAvailable);
int getSkeletonState(const COISkeleton *cSkeleton) const;
#ifdef __cplusplus
}
#endif

#endif //OPENISS_SKELETON_WRAPPER