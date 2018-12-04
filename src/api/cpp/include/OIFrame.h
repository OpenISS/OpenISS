//
// Created by Haotao Lai on 2018-08-09.
//
// Modified to a c header by Cameron Belcher 2018-12-04
//

#ifndef OPENISS_OIFRAME_H
#define OPENISS_OIFRAME_H

#include <vector>
#include <string>
#include "OIEnum.h"

using std::vector;

namespace openiss {

struct OISkeleton;

struct OIFrame {
public:
    virtual ~OIFrame() = default;

    virtual int getHeight() const = 0;
    virtual int getWidth() const = 0;
    virtual void save(std::string fileName) = 0;
    virtual void show(const char *winName) = 0;
    virtual void drawSkeleton(OISkeleton *pSkeleton, vector<openiss::JointType> &types) = 0;
};


struct OIAbstractDataFrame : public OIFrame {
public:
    ~OIAbstractDataFrame() override = default;
    virtual void *getData() = 0;
    virtual int getBytesPerPixel() = 0;
};

} // end of namespace
#endif //OPENISS_OIFRAME_H
