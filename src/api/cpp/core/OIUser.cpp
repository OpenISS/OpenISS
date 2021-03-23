#include <utility>

//
// Created by Haotao Lai on 2018-08-17.
//
#include "../include/OIUser.hpp"

// ////////////////////////////////////////////////////////////////////////////
// OIUserData class definition
// ////////////////////////////////////////////////////////////////////////////
openiss::OIUserData::OIUserData(long id)
  : mUserId(id) {
    mpSkeleton = new OISkeleton;
}

openiss::OIUserData::~OIUserData() {
    delete mpSkeleton;
}

openiss::OISkeleton *openiss::OIUserData::getSkeleton() const {
    return mpSkeleton;
}

long openiss::OIUserData::getID() const {
    return mUserId;
}

openiss::Point3f *openiss::OIUserData::getBoundingBox() {
    return boundingBox;
}

openiss::Point3f *openiss::OIUserData::getCenterOfMass() {
    return &centerPoint;
}

// ////////////////////////////////////////////////////////////////////////////
// OIUserMap class definition
// ////////////////////////////////////////////////////////////////////////////
static void copyPtrArray(const short* src, int len, short* dst) {
    dst = new short[len];
    for (int i = 0; i < len; i++) {
        dst[i] = src[i];
    }
}

void openiss::OIUserMap::_copy(const OIUserMap &otherUserMap) {
    mWidth = otherUserMap.getWidth();
    mHeight = otherUserMap.getHeight();
    copyPtrArray(otherUserMap.getPixels(), mWidth * mHeight, mpUserArray);
}

openiss::OIUserMap::OIUserMap(int width, int height)
    : mWidth(width)
    , mHeight(height)
    , mpUserArray(nullptr)
{ }

openiss::OIUserMap::OIUserMap(int width, int height, short* map)
        : mWidth(width)
        , mHeight(height)
        , mpUserArray(nullptr) {
    copyPtrArray(map, width * height, mpUserArray);
}

openiss::OIUserMap::OIUserMap(const OIUserMap &otherUserMap) {
    _copy(otherUserMap);
}

openiss::OIUserMap::~OIUserMap() {
    delete[] mpUserArray;
}

openiss::OIUserMap &openiss::OIUserMap::operator=(const OIUserMap &otherMap) {
    _copy(otherMap);
    return *this;
}

int openiss::OIUserMap::getHeight() const {
    return mHeight;
}

int openiss::OIUserMap::getWidth() const {
    return mWidth;
}

short* openiss::OIUserMap::getPixels() const {
    return mpUserArray;
}

//// ////////////////////////////////////////////////////////////////////////////
//// Point3f class definition
//// ////////////////////////////////////////////////////////////////////////////
//openiss::Point3f::Point3f(float x, float y, float z)
//  : x(x)
//  , y(y)
//  , z(z)
//{ }

