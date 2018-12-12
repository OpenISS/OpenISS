//
// Created by Haotao Lai on 2018-10-12.
//

#ifndef OPENISS_OITYPE_H
#define OPENISS_OITYPE_H

namespace openiss {

typedef struct DistortionMode {

} DistortionMode;

typedef struct Intrinsic {
    int width{0};
    int height{0};
    float cx{0};
    float cy{0};
    float fx{0};
    float fy{0};
    float coeffs[5]{};

    Intrinsic() = default;
    Intrinsic(const Intrinsic& tmp) {
        width = tmp.width;
        height = tmp.height;
        cx = tmp.cx;
        cy = tmp.cy;
        fx = tmp.fx;
        fy = tmp.fy;
        for (int i = 0; i < 5; i++) coeffs[i] = tmp.coeffs[i];
    }
} Intrinsic;

typedef struct Extrinsic {
    // extrinsic matrix represents as follow

    // r0 r3 r6 | t0
    // r1 r4 r7 | t1
    // r2 r5 r9 | t2

    float rotation[9]{0};
    float translation[3]{0};

    Extrinsic() = default;
    Extrinsic(const Extrinsic& tmp) {
        for (int i = 0; i < 9; i++) rotation[i] = tmp.rotation[i];
        for (int i = 0; i < 3; i++) translation[i] = tmp.translation[i];
    }
} Extrinsic;

typedef struct Point2f {
    float x{0};
    float y{0};
} Point2f;

typedef struct Point3f {
    float x{0};
    float y{0};
    float z{0};
} Point3f;

} // end of namespace
#endif //OPENISS_OITYPE_H
