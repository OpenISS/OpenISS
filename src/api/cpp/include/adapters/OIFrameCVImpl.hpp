//
// Created by Haotao Lai on 2018-08-09.
//

#ifndef OPENISS_OIFRAMEIMPL_H
#define OPENISS_OIFRAMEIMPL_H

#include "../include/OIFrame.hpp"
#include "../include/OISkeleton.hpp"
#include <opencv2/core/mat.hpp>
#include <opencv/cv.hpp>

namespace openiss {

class OIFrameCVImpl : public OIFrame {

public:
    explicit OIFrameCVImpl(const cv::Mat& img);

    int getHeight() const override;
    int getWidth()  const override;

    void save(std::string fileName) override;
    void show(const char* winName) override;
    void drawSkeleton(OISkeleton *pSkeleton, vector<JointType> &types) override;

private:
    cv::Mat mImg;
};

} // end of namespace
#endif //ERIC_THESIS_TESTING_OIFRAMEIMPL_H
