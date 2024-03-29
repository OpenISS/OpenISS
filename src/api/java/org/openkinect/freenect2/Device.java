package org.openkinect.freenect2;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/*
openKinect2 library for Processing
Copyright (c) 2014 Thomas Sanchez Lengeling

* Redistribution and use in source and binary forms, with or
* without modification, are permitted provided that the following
* conditions are met:
*
* Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in
* the documentation and/or other materials provided with the
* distribution.
*

openKinect2 library for Processing is free software: you can redistribute it and/or
modify it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

openKinect2 for Processing is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with openKinect2  for Processing.  If not, see
<http://www.gnu.org/licenses/>.
*/
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class Device {

    static String PROJECT_HOME = System.getProperty("user.dir");

    static {
        //load OS an arch difference
        int arch = Integer.parseInt(System.getProperty("sun.arch.data.model"));
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
//            System.out.println(arch + " windows");
//            System.loadLibrary("v2/msvc/turbojpeg");
//            System.loadLibrary("v2/msvc/libusb-1.0");
//            System.loadLibrary("v2/msvc/libJNILibfreenect2");
        } else if (osName.contains("mac")) {
            System.out.println(arch + " mac");

//            LibraryPath libPath = new LibraryPath();
//            String path = libPath.getDir() + "/v2/mac/";
            // System.out.println(System.getProperty("java.library.path"));

            String path = PROJECT_HOME + "/lib/v2/mac/";
            System.out.println("Found path v2: " + path);

            System.load(path + "libturbojpeg.dylib");
            System.load(path + "libJNILibfreenect2.dylib");
        } else if (osName.contains("linux")) {
            System.out.println(arch + " linux");
//            LibraryPath libPath = new LibraryPath();
//            String path = libPath.getDir() + "/v2/linux/";
            // System.out.println(System.getProperty("java.library.path"));

            String path = PROJECT_HOME + "/lib/v2/linux/";
            System.out.println("Found path v2: " + path);

            System.load(path + "libfreenect2.so");
            System.load(path + "libturbojpeg.so.0");
        } else {
            System.err.println("not compatible with the current OS or is a 32 bit system");
        }
    }

    public int depthWidth = 512;
    public int depthHeight = 424;

    public int colorWidth = 1920;
    public int colorHeight = 1080;

//    private PApplet parent;
    private long ptr;

    //version control for openKinect2
    private String version = "";

    PImage depthImg;
    PImage irImg;
    PImage colorImg;
    PImage undistortedImg;
    PImage registeredImg;

//    BufferedImage depthImg;
//    BufferedImage irImg;
//    BufferedImage colorImg;
//    BufferedImage undistortedImg;
//    BufferedImage registeredImg;

    FloatBuffer depthPositions;

    IntBuffer depthColorBuffer;
    IntBuffer irColorBuffer;
    IntBuffer registeredColorBuffer;

    /**
     * Constructor for openKinect 2
     *
     * // @param _p  parent
     */
//    public Device(PApplet _p) {
//        parent = _p;

    public Device() {
        ptr = jniInit();

        depthImg = new PImage(depthWidth, depthHeight, PImage.ALPHA);
        irImg = new PImage(depthWidth, depthHeight, PImage.ALPHA);
        colorImg = new PImage(colorWidth, colorHeight, PImage.ARGB);
        undistortedImg = new PImage(depthWidth, depthHeight, PImage.ALPHA);
        registeredImg = new PImage(depthWidth, depthHeight, PImage.ARGB);

//        depthImg = new BufferedImage(depthWidth, depthHeight, BufferedImage.TYPE_3BYTE_BGR);
//        irImg = new BufferedImage(depthWidth, depthHeight, BufferedImage.TYPE_3BYTE_BGR);
//        colorImg = new BufferedImage(colorWidth, colorHeight, BufferedImage.TYPE_3BYTE_BGR);
//        undistortedImg = new BufferedImage(depthWidth, depthHeight, BufferedImage.TYPE_3BYTE_BGR);
//        registeredImg = new BufferedImage(depthWidth, depthHeight, BufferedImage.TYPE_3BYTE_BGR);

        //Buffers for openGL calls
        depthPositions = FloatBuffer.allocate(depthWidth * depthHeight * 3);

        //colors
        depthColorBuffer = IntBuffer.allocate(depthWidth * depthHeight);
        irColorBuffer = IntBuffer.allocate(depthWidth * depthHeight);
        registeredColorBuffer = IntBuffer.allocate(depthWidth * depthHeight);

        depthImg.loadPixels();
        irImg.loadPixels();
        colorImg.loadPixels();
        undistortedImg.loadPixels();
        registeredImg.loadPixels();

        for (int i = 0; i < depthWidth; i++) {
            for (int j = 0; j < depthHeight; j++) {
                int pos = i + j * depthWidth;
                depthImg.pixels[pos] = color(0);
                irImg.pixels[pos] = color(0);
                undistortedImg.pixels[pos] = color(0);
                registeredImg.pixels[pos] = color(0);
            }
        }

        depthImg.updatePixels();
        irImg.updatePixels();
        undistortedImg.updatePixels();
        registeredImg.updatePixels();


        for (int i = 0; i < colorWidth; i++) {
            for (int j = 0; j < colorHeight; j++) {
                int pos = i + j * colorWidth;
                colorImg.pixels[pos] = color(0);
            }
        }

        colorImg.updatePixels();


        //System.out.println(version());
    }

    // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
    // some methods grab from:
    // https://github.com/processing/processing/blob/master/core/src/processing/core/PApplet.java

    public static final int color(int gray) {
        if (gray > 255) gray = 255; else if (gray < 0) gray = 0;
        return 0xff000000 | (gray << 16) | (gray << 8) | gray;
    }
    // / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /

    /**
     * Open and initialize the a default Device
     */
    public void initDevice() {
        jniOpen();

    }

    /**
     * Open and initialize a specific Device index [0, numDevices - 1]
     *
     * @param index
     */
    public void initDevice(int index) {
        jniOpenM(index);
    }


    /**
     * Close Device
     */
    public void stopDevice() {
        jniStop();
    }

    /**
     * Process Depth Image (512 x 424)
     *
     * @return PImage
     */
    public BufferedImage getDepthImage() {
        int[] depthRawData = jniGetDepthData();
        arrayCopy(depthRawData, 0, depthImg.pixels, 0, depthImg.width * depthImg.height);
        depthColorBuffer.put(depthRawData, 0, depthWidth * depthHeight);
        depthColorBuffer.rewind();
        depthImg.updatePixels();
        return (BufferedImage) depthImg.getNative();
    }

    /**
     * Process Infrared Image (512 x 424)
     *
     * @return PImage
     */
    public BufferedImage getIrImage() {
        int[] irRawData = jniGetIrData();
        arrayCopy(irRawData, 0, irImg.pixels, 0, irImg.width * irImg.height);
        irColorBuffer.put(irRawData, 0, depthWidth * depthHeight);
        irColorBuffer.rewind();
        irImg.updatePixels();
        return (BufferedImage) irImg.getNative();
    }


    /**
     * Process Color Image (1920 x 1080)
     *
     * @return PImage
     */
    public BufferedImage getVideoImage() {
        int[] colorRawData = jniGetColorData();
        arrayCopy(colorRawData, 0, colorImg.pixels, 0, colorImg.width * colorImg.height);
        colorImg.updatePixels();
        return (BufferedImage) colorImg.getNative();
    }

    /**
     * Process the undistored Image  (512 x 442) Image output
     *
     * @return PImage
     */
    public PImage getUndistoredImage() {
        int[] undistoredData = jniGetUndistorted();
        arrayCopy(undistoredData,
                0, undistortedImg.pixels,
                0, undistortedImg.width * undistortedImg.height);
        undistortedImg.updatePixels();
        return undistortedImg;
    }

    /**
     * Process the registered Image RGB and Depth mapping (512 x 442) Image output
     *
     * @return PImage
     */
    public PImage getRegisteredImage() {
        int[] registeredData = jniGetRegistered();
        arrayCopy(registeredData,
                0, registeredImg.pixels,
                0, registeredImg.width * registeredImg.height);
        registeredColorBuffer.put(registeredImg.pixels, 0, depthWidth * depthHeight);
        registeredColorBuffer.rewind();
        registeredImg.updatePixels();
        return registeredImg;
    }

    static public void arrayCopy(Object src, int srcPosition,
                                 Object dst, int dstPosition,
                                 int length) {
        System.arraycopy(src, srcPosition, dst, dstPosition, length);
    }

    /**
     * Get depth camera positions based on the Kinect v2 hardware
     *
     * @return float array of 512 x 424 x 3 elements
     */
    public float[] getDepthToCameraPositions() {
        return jniGetDepthCameraPositions();
    }

    /**
     * Get depth camera positions as a float buffer data structure
     *
     * @return FloatBuffer 512 x 424 x 3 floats
     */
    public FloatBuffer getDepthBufferPositions() {
        float[] pcRawData = jniGetDepthCameraPositions();
        depthPositions.put(pcRawData, 0, depthWidth * depthHeight * 3);
        depthPositions.rewind();

        return depthPositions;
    }

    /**
     * get the IR color data as an Int Buffer data structure
     *
     * @return IntBuffer Ir color Data
     */
    public IntBuffer getIrColorBuffer() {
        return irColorBuffer;
    }

    /**
     * get the color map to depth data as an Int Buffer data structure
     *
     * @return IntBuffer registered color data
     */
    public IntBuffer getRegisteredColorBuffer() {
        return registeredColorBuffer;
    }

    /**
     * get the depth color data as an Int Buffer data structure
     *
     * @return IntBuffer depth color data
     */
    public IntBuffer getDepthColorBuffer() {
        return depthColorBuffer;
    }

    /**
     * get the raw depth data 512 x 424
     *
     * @return array of ints from 0 - 4500
     */
    public int[] getRawDepth() {
        return jniGetRawDepthData();
    }

    /**
     * Get the number of Devices connected to the computer
     *
     * @return int
     */
    public int getNumKinects() {
        return jniGetNumDevices();
    }

    /**
     * Get the Serial Number
     *
     * @return
     */
    public String getDefaulSerialNum() {
        if (jniGetNumDevices() > 0)
            return jniGetSerialDevice(0);
        return "123456789";
    }


    /**
     * Get the Serial Number
     *
     * @return
     */
    public String getSerialNum(int index) {
        if (jniGetNumDevices() > 0 && jniGetNumDevices() < index)
            return jniGetSerialDevice(index);
        return "123456789";
    }

    /**
     * Print Number of Kinect v2 connected and Information about the Devices
     */
    public void printDevices() {
        jniEumerateDevices();
    }

    /**
     * Enable the depth capture of the Kinect 2
     */
    public void initDepth() {
        jniEnableDepth(true);
    }

    /**
     * Enable the Video capture of the Kinect 2
     */
    public void initVideo() {
        jniEnableVideo(true);
    }

    /**
     * Enable the IR capture of the Kinect 2
     */
    public void initIR() {
        jniEnableIR(true);
    }

    /**
     * Enable the Video + Depth mapping of the Kinect 2
     */
    public void initRegistered() {
        jniEnableRegistered(true);
    }


    /**
     * Enable all the capture frames of the kinect 2
     */
    public void init() {
        initDepth();
        initVideo();
        initIR();
        initRegistered();
    }


    //JNI Functions
    private native long jniInit();

    private native void jniOpen();

    private native void jniOpenM(int index);

    private native void jniStop();

    private native void jniEumerateDevices();

    //Multiple Kinect Funtions
    private native void jniOpenSerial(String serialNumber);

    private native int jniGetNumDevices();

    private native String jniGetSerialDevice(int index);

    //Get data functions
    private native int[] jniGetDepthData();

    private native int[] jniGetRawDepthData();

    private native int[] jniGetIrData();

    private native int[] jniGetColorData();

    private native int[] jniGetUndistorted();

    private native int[] jniGetRegistered();

    private native float[] jniGetDepthCameraPositions();


    //Enables functions
    private native void jniEnableVideo(boolean enable);

    private native void jniEnableDepth(boolean enable);

    private native void jniEnableIR(boolean enable);

    private native void jniEnableRegistered(boolean enable);

}