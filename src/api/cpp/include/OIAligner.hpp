//
// Created by Haotao Lai on 2018-10-12.
//

#ifndef OPENISS_ALIGNER_H
#define OPENISS_ALIGNER_H

#include <cstdint>
#include "../include/OIType.hpp"
#include "../include/OIDevice.hpp"
#include "../include/OpenISS.hpp"

typedef unsigned char byte;

namespace openiss {

class OIAligner {
public:

    void deprojectImageToPointCloud(
            OIDevice *pDevice, OIAbstractDataFrame *depthFrame,
            byte *alignedDataBuf, int len
    );
    void projectPointCloudToImage();

    // transform a pint from one 3D coordinate to another
    void transformPointToPoint(const Extrinsic &extrinsic, Point3f &src, Point3f &dst);

    // Given pixel coordinates and depth in an image compute the corresponding
    // point in 3D space relative to the same camera
    void deprojectFromPixelToPoint(const Intrinsic &intrinsic, Point2f &pixel, float depth, Point3f &point);

    // Given a point in 3D space, compute the corresponding pixel coordinates
    // in an image produced by the same camera
    void projectFromPointToPixel(const Intrinsic &intrinsic, Point3f &point, Point2f &pixel);
    ~OIAligner() = default;

private:

    template<class GET_DEPTH, class TRANSFER_PIXEL>
    void alignImages(
            const openiss::Intrinsic &depthIntrinsic, const openiss::Intrinsic &colorIntrinsic,
            const openiss::Extrinsic &extrinsic,
            GET_DEPTH getDepth,
            TRANSFER_PIXEL transferPixel
    );
    void alignDepth2Color (
            byte *alignedDataBuf, const uint16_t *depthData, float depthScale,
            const openiss::Intrinsic &depthIntrinsic, const openiss::Intrinsic &colorIntrinsic,
            const openiss::Extrinsic &extrinsic
    );
};

} // end of namespace
#endif //OPENISS_ALIGNER_H
