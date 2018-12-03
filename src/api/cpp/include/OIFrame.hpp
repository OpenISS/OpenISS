//
// Created by Haotao Lai on 2018-08-09.
//

#ifndef OPENISS_OIFRAME_H
#define OPENISS_OIFRAME_H

#include <vector>
#include <string>
#include "OIEnum.hpp"

using std::vector;

namespace openiss {

class OISkeleton;

class OIFrame {
public:
    virtual ~OIFrame() = default;

    virtual int getHeight() const = 0;
    virtual int getWidth() const = 0;
    virtual void save(std::string fileName) = 0;
    virtual void show(const char *winName) = 0;
    virtual void drawSkeleton(OISkeleton *pSkeleton, vector<openiss::JointType> &types) = 0;
};


class OIAbstractDataFrame : public OIFrame {
public:
    ~OIAbstractDataFrame() override = default;
    virtual void *getData() = 0;
    virtual int getBytesPerPixel() = 0;
};

} // end of namespace
#endif //OPENISS_OIFRAME_H
