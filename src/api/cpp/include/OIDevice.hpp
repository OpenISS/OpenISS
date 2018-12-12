//
// Created by Haotao Lai on 2018-08-08.
//

#ifndef OPENISS_OIDEVICE_H
#define OPENISS_OIDEVICE_H

#include <vector>
#include <memory>
#include "OIEnum.hpp"
#include "OIType.hpp"

namespace openiss {

class OIFrame;
class OIDevice;

/**
 * Abstraction of actual depth camera device
 */
class OIDevice {
public:

    virtual ~OIDevice() = default;

    virtual void *rawDevice() = 0;
    virtual void init() = 0;
    virtual void open() = 0;
    virtual void close() = 0;

    virtual bool enableColor() = 0;
    virtual bool enableDepth() = 0;
    virtual bool enableRegistered() = 0;
    virtual bool enable() = 0;

    virtual Intrinsic getIntrinsic(StreamType streamType) = 0;
    virtual Extrinsic getExtrinsic(StreamType from, StreamType to) = 0;
    virtual float getDepthScale() = 0;

    // need to manage the heap-allocated memory of the frame
    virtual OIFrame *readFrame(StreamType frameType) = 0;

};

/**
 * This class provides different kind of device
 * object based on the passing paramenter of the
 * create() method.
 * 
 * Also, it will responsible for managing the memory 
 * of those device objects it created.
 */
class OIDevFactory {
public:
    std::shared_ptr<openiss::OIDevice> create(const char *devName);

private:
    bool mIsOpenNIInit = false;
};

} // end of namespace
#endif //OPENISS_OIDEVICE_H
