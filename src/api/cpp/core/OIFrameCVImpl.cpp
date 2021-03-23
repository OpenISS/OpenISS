//
// Created by Haotao Lai on 2018-08-09.
//

#include <iostream>
#include <cstring>
#include "OIFrameCVImpl.h"

openiss::OIFrameCVImpl::OIFrameCVImpl(const cv::Mat& img) {
    mImg = img;
}

int openiss::OIFrameCVImpl::getHeight() const {
    return mImg.rows;
}

int openiss::OIFrameCVImpl::getWidth() const {
    return mImg.cols;
}

void openiss::OIFrameCVImpl::show(const char* winName){
    cv::imshow(winName, mImg);
}

void openiss::OIFrameCVImpl::save(std::string fileName) {
    std::string path = "/Users/ERIC_LAI/CLionProjects/Eric_Thesis_Testing/calibration/";
    vector<int> param;
    param.push_back(CV_IMWRITE_JPEG_QUALITY);
    param.push_back(95);
    cv::imwrite(path + "img_" + fileName + ".jpg", mImg, param);
}

void openiss::OIFrameCVImpl::drawSkeleton(openiss::OISkeleton *pSkeleton, vector<JointType> &types) {
    if (pSkeleton->getSkeletonState()) {
        shared_ptr<OISkeletonJoint> aJoints[15];

        aJoints[0] = pSkeleton->getJointByType(openiss::JOINT_HEAD);
        aJoints[1] = pSkeleton->getJointByType(openiss::JOINT_NECK);
        aJoints[2] = pSkeleton->getJointByType(openiss::JOINT_LEFT_SHOULDER);
        aJoints[3] = pSkeleton->getJointByType(openiss::JOINT_RIGHT_SHOULDER);
        aJoints[4] = pSkeleton->getJointByType(openiss::JOINT_LEFT_ELBOW);
        aJoints[5] = pSkeleton->getJointByType(openiss::JOINT_RIGHT_ELBOW);
        aJoints[6] = pSkeleton->getJointByType(openiss::JOINT_LEFT_HAND);
        aJoints[7] = pSkeleton->getJointByType(openiss::JOINT_RIGHT_HAND);
        aJoints[8] = pSkeleton->getJointByType(openiss::JOINT_TORSO);
        aJoints[9] = pSkeleton->getJointByType(openiss::JOINT_LEFT_HIP);
        aJoints[10] = pSkeleton->getJointByType(openiss::JOINT_RIGHT_HIP);
        aJoints[11] = pSkeleton->getJointByType(openiss::JOINT_LEFT_KNEE);
        aJoints[12] = pSkeleton->getJointByType(openiss::JOINT_RIGHT_KNEE);
        aJoints[13] = pSkeleton->getJointByType(openiss::JOINT_LEFT_FOOT);
        aJoints[14] = pSkeleton->getJointByType(openiss::JOINT_RIGHT_FOOT);

        cv::Point2f aPoint[15];
        for (int s = 0; s < 15; ++s) {
            aPoint[s].x = aJoints[s]->row;
            aPoint[s].y = aJoints[s]->col;
        }

        cv::line(mImg, aPoint[0], aPoint[1], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[1], aPoint[2], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[1], aPoint[3], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[2], aPoint[4], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[3], aPoint[5], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[4], aPoint[6], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[5], aPoint[7], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[1], aPoint[8], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[8], aPoint[9], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[8], aPoint[10], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[9], aPoint[11], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[10], aPoint[12], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[11], aPoint[13], cv::Scalar(255, 0, 0), 3);
        cv::line(mImg, aPoint[12], aPoint[14], cv::Scalar(255, 0, 0), 3);

        for (auto &s : aPoint) {
            cv::circle(mImg, s, 3, cv::Scalar(0, 0, 255), 2);
        }
    }
}