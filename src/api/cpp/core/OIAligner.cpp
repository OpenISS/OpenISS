//
// Created by Haotao Lai on 2018-10-12.
//


#include <algorithm>
#include "OIAligner.h"

template<class GET_DEPTH, class TRANSFER_PIXEL>
void openiss::OIAligner::alignImages(
        const openiss::Intrinsic &depthIntrinsic, const openiss::Intrinsic &colorIntrinsic,
        const openiss::Extrinsic &extrinsic,
        GET_DEPTH getDepth, TRANSFER_PIXEL transferPixel
) {
    for (int depthY = 0; depthY < depthIntrinsic.height; ++depthY) {
        int depthPixelIdx = depthY * depthIntrinsic.width;
        for (int depthX = 0; depthX < depthIntrinsic.width; ++depthX, ++depthPixelIdx) {
            if (float depth = getDepth(depthPixelIdx)) {
                Point3f depthPoint, otherPoint;
                Point2f otherPixel, depthPixel;
                depthPixel.x = depthX * 1.0f;
                depthPixel.y = depthY * 1.0f;
                deprojectFromPixelToPoint(depthIntrinsic, depthPixel, depth, depthPoint);
                transformPointToPoint(extrinsic, depthPoint, otherPoint);
                projectFromPointToPixel(colorIntrinsic, otherPoint, otherPixel);

                const int otherX = static_cast<int>(otherPixel.x);
                const int otherY = static_cast<int>(otherPixel.y);

                if (otherX < 0 || otherX >= depthIntrinsic.width || otherY < 0 || otherY >= depthIntrinsic.height) {
                    continue;
                }

                int newIdx = otherY * colorIntrinsic.width + otherX;
                transferPixel(depthPixelIdx, newIdx);
            }
        }
    }
}

void openiss::OIAligner::alignDepth2Color (
        byte *alignedDataBuf, const uint16_t *depthData, float depthScale,
        const openiss::Intrinsic &depthIntrinsic, const openiss::Intrinsic &colorIntrinsic,
        const openiss::Extrinsic &extrinsic
) {
    // since the alignedDataBuf size was calculated with the unit byte, and the depth data
    // is stored as u_int16 so we need to cast the buffer to proper type then fill the data
    auto alignedData = (uint16_t *) alignedDataBuf;
    alignImages(depthIntrinsic, colorIntrinsic, extrinsic,
                // get depth lambda
                [depthData, depthScale](int depthPixelIdx) {
                    return depthScale * depthData[depthPixelIdx];
                },
                // transfer pixel lambda
                [alignedData, depthData](int depthPixelIdx, int otherPixelIdx) {
                    if (alignedData[otherPixelIdx]) {
                        alignedData[otherPixelIdx] = std::min((int) alignedData[otherPixelIdx],
                                                                  (int) depthData[depthPixelIdx]);
                    }
                    else {
                        alignedData[otherPixelIdx] = depthData[depthPixelIdx];
                    }
                });
}

void openiss::OIAligner::deprojectFromPixelToPoint(
        const openiss::Intrinsic &intrinsic, openiss::Point2f &pixel, float depth,
        openiss::Point3f &point
) {
    // make use of pinhole camera theory and intrinsic matrix
    float x = (pixel.x - intrinsic.cx) / intrinsic.fx;
    float y = (pixel.y - intrinsic.cy) / intrinsic.fy;
    point.x = depth * x;
    point.y = depth * y;
    point.z = depth;
}

void openiss::OIAligner::projectFromPointToPixel(
        const openiss::Intrinsic &intrinsic, openiss::Point3f &point,
        openiss::Point2f &pixel
) {
    // make use of pinhole camera theory and intrinsic matrix
    float x = point.x / point.z;
    float y = point.y / point.z;
    pixel.x = x * intrinsic.fx + intrinsic.cx;
    pixel.y = y * intrinsic.fy + intrinsic.cy;
}

void openiss::OIAligner::transformPointToPoint(
        const openiss::Extrinsic &extrinsic,
        openiss::Point3f &src, openiss::Point3f &dst
) {
    dst.x = extrinsic.rotation[0] * src.x + extrinsic.rotation[3] * src.y
            + extrinsic.rotation[6] * src.z + extrinsic.translation[0];
    dst.y = extrinsic.rotation[1] * src.x + extrinsic.rotation[4] * src.y
            + extrinsic.rotation[7] * src.z + extrinsic.translation[1];
    dst.z = extrinsic.rotation[2] * src.x + extrinsic.rotation[5] * src.y
            + extrinsic.rotation[8] * src.z + extrinsic.translation[2];
}

void openiss::OIAligner::deprojectImageToPointCloud(
        OIDevice *pDevice, OIAbstractDataFrame *depthFrame,
        byte *alignedDataBuf, int len
) {
    Intrinsic depthInstr = pDevice->getIntrinsic(StreamType::DEPTH_STREAM);
    Intrinsic colorInstr = pDevice->getIntrinsic(StreamType::COLOR_STREAM);
    Extrinsic extrinsic = pDevice->getExtrinsic(StreamType::DEPTH_STREAM, StreamType::COLOR_STREAM);
    float depthScale = pDevice->getDepthScale();
    auto depthData = depthFrame->getData();

    // fill the buffer with initial value
    memset(alignedDataBuf, 0, static_cast<size_t>(len));

    // perform the alignment
    alignDepth2Color(alignedDataBuf, reinterpret_cast<const uint16_t*>(depthData),
                     depthScale, depthInstr, colorInstr, extrinsic);
}
