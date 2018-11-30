//
// Created by Haotao Lai on 2018-08-17.
//

#ifndef OPENISS_OIUSER_H
#define OPENISS_OIUSER_H

#include "OISkeleton.hpp"
#include "OIType.hpp"

namespace openiss {

//class Point3f {
//public:
//    explicit Point3f(float x=0.0f, float y=0.0f, float z=0.0f);
//    float x, y, z;
//};

/**
 * OIUserMap provides a same resolution frame as the depth
 * frame, where the background with 0 in that pixel and user
 * id in a specific user's position
 */
class OIUserMap {
public:
    OIUserMap(int width, int height);
    OIUserMap(int width, int height, short *map);
    ~OIUserMap();
    OIUserMap(const OIUserMap &userMap);
    OIUserMap &operator=(const OIUserMap &otherMap);

    int getHeight() const;
    int getWidth() const;
    short *getPixels() const;

private:
    int mHeight;
    int mWidth;
    short *mpUserArray;

    void _copy(const OIUserMap &userMap);
};

/**
 * OIUserData contains all the data of a single user
 * within a frame
 */
class OIUserData {
public:
    explicit OIUserData(long id = 0);
    ~OIUserData();

    OISkeleton *getSkeleton() const;
    long getID() const;
    Point3f *getBoundingBox();
    Point3f *getCenterOfMass();

private:
    long mUserId;
    OISkeleton *mpSkeleton;
    Point3f boundingBox[2];
    Point3f centerPoint;

    // todo: eigenvector to represent the unique user
};


}
#endif //OPENISS_OIUSER_H
