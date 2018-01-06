#ifndef KINECT1_H
#define KINECT1_H

#include "libfreenect.h"

void depth_cb(freenect_device *dev, void *v_depth, uint32_t timestamp);
void rgb_cb(freenect_device *dev, void *rgb, uint32_t timestamp);
/*void *freenect_threadfunc(void *arg);*/
void kinect1_receive_rgb_depth_frames();
void kinect1_keyPressed(unsigned char key, int x, int y);

#endif