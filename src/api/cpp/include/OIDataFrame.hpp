//
// Created by Haotao Lai on 2018-10-24.
//

#ifndef OPENISS_OIDEPTHDATAFRAME_H
#define OPENISS_OIDEPTHDATAFRAME_H

#include <stdint.h>
#include "../include/OIFrame.hpp"
#include "../include/OIType.hpp"

namespace openiss {

class OIDataFrame : public OIAbstractDataFrame {
public:
    ~OIDataFrame() override;
    explicit OIDataFrame(FrameType type, void *data, int bpp, int width, int height);

    void *getData() override;
    int getBytesPerPixel() override;
    int getHeight() const override;
    int getWidth() const override;
    void save(std::string fileName) override;
    void show(const char *winName) override;
    void drawSkeleton(OISkeleton *pSkeleton, vector<JointType> &types) override;

private:
    FrameType type;
    int bpp;
    int width, height;
    void *mpData;
    OIFrame *displayCVImpl;

    bool hasDispImg;
    void createDisplayImg();
};

}
#endif //OPENISS_OIDEPTHDATAFRAME_H
