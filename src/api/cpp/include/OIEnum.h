//
// Created by Haotao Lai on 2018-08-24.
//
// Modified to a c header by Cameron Belcher 2018-12-04
//
#ifndef OPENISS_ENUM_H
#define OPENISS_ENUM_H

struct OpenISS_OIEnum
{
enum StreamType {
    COLOR_STREAM,
    DEPTH_STREAM,
    IR_STREAM,
    REGISTERED_STREAM
};


enum JointType {
    JOINT_HEAD,
    JOINT_NECK,

    JOINT_LEFT_SHOULDER,
    JOINT_RIGHT_SHOULDER,
    JOINT_LEFT_ELBOW,
    JOINT_RIGHT_ELBOW,
    JOINT_LEFT_HAND,
    JOINT_RIGHT_HAND,

    JOINT_TORSO,

    JOINT_LEFT_HIP,
    JOINT_RIGHT_HIP,
    JOINT_LEFT_KNEE,
    JOINT_RIGHT_KNEE,
    JOINT_LEFT_FOOT,
    JOINT_RIGHT_FOOT,
};

enum FrameType {
    DEPTH_FRAME, COLOR_FRAME, IR_FRAME
};

}; // end of struct

#endif //OPENISS_ENUM_H
