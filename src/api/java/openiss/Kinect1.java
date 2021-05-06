/**
 * Open Kinect1 for Processing
 * A Mac OS X Kinect1 implementation using open source drivers (libfreenect).
 * https://github.com/shiffman/OpenKinect-for-Processing
 * <p>
 * Copyright 2015 Daniel Shiffman
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 *
 * @author Daniel Shiffman
 * @modified July 3, 2015
 * @version 0.3a (3)
 */


package openiss;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import openiss.utils.OpenISSConfig;
import openiss.ws.soap.service.OpenISSSOAPServiceImpl;
import org.openkinect.freenect.Context;
import org.openkinect.freenect.DepthFormat;
import org.openkinect.freenect.DepthHandler;
import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;
import org.openkinect.freenect.Freenect;
import org.openkinect.freenect.VideoFormat;
import org.openkinect.freenect.VideoHandler;

import javax.imageio.ImageIO;
import openiss.utils.Utils;
import openiss.utils.OpenISSConfig.SensorType;

// TODO: Move Fakenect implementation that uses local files
public class Kinect1 implements Sensor {

    static byte[] color;
    static ShortBuffer depth;

    private static String colorFileName = "color_example.jpg";
    private static String depthFileName = "depth_example.jpg";
    private static String colorFailFileName = "color_fail.jpg";
    private static String depthFailFileName = "depth_fail.jpg";

    Method depthEventMethod;
    Method videoEventMethod;

    boolean irEnabled = false;
    boolean videoEnabled = false;

    boolean mirrorMode = false;

    BufferedImage depthImage;
    BufferedImage videoImage;

    ShortBuffer rawDepthBuffer;
    FloatBuffer rawDepthToWorldBuffer;

    int[] rawDepth;
    float[] rawDepthToWorld;

    Context context;
    Device device;

    public int width = 640;
    public int height = 480;

    boolean irMode = false;
    boolean colorDepthMode = false;

    int currentDeviceIndex = 0;

    boolean started = false;

    // We'll use a lookup table so that we don't have to repeat the math over and over
    float[] depthLookUp = new float[2048];

    //operating_system holds the name of the operating system
    private static String operating_system = System.getProperty("os.name").toLowerCase();

    static String FAKENECT_PATH = System.getenv("FAKENECT_PATH");

    private ClassLoader classLoader = getClass().getClassLoader();

    /**
     * Kinect1 constructor, usually called in the setup() method in your sketch to
     * initialize and start the library.
     *
     */

    public Kinect1() {


        // Skip windows until the driver is working
        if (operating_system.indexOf("win") >= 0) {
            System.err.println("Kinect1 is not currently supported on Windows Operating System.");
            return;
        }

        /**
         * Both Freenect and Fakenect are disabled
         */
        if (OpenISSConfig.SENSOR_TYPE != OpenISSConfig.SensorType.FREENECT && 
            OpenISSConfig.SENSOR_TYPE != OpenISSConfig.SensorType.FAKENECT) {
            return;
        }

        depthImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        videoImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        rawDepth = new int[width * height];

        rawDepthToWorld = new float[width * height * 3];
        rawDepthToWorldBuffer = FloatBuffer.allocate(width * height * 3);

        // Lookup table for all possible depth values (0 - 2047)
        for (int i = 0; i < depthLookUp.length; i++) {
            depthLookUp[i] = Utils.rawDepthToMeters(i);
        }

        context = Freenect.createContext();
        if (numDevices() < 1) {
            System.err.println("No Kinect1 devices found.");
        } else {
            System.out.println("Kinect1 device loaded");
        }
        //start(0);
    }

    /**
     * Static method to only obtain the number of connected Kinect1 devices
     * @return
     */
    public static int countDevices() {
        Context tmpContext = Freenect.createContext();
        return tmpContext.numDevices();
    }

    /**
     * Returns the number of Kinect1 devices detected
     *
     * @return number of Kinect1 devices detected
     */
    public int numDevices() {
        return context.numDevices();
    }


    /**
     * Set the active device
     *
     * @param n index of which device to select
     * startDevice -> activateDevice
     */
    public void activateDevice(int n) {
        currentDeviceIndex = n;
    }

    // Called internally
    private void start() {
        started = true;
        device = context.openDevice(currentDeviceIndex);
    }

    /**
     * Get the raw depth values from the Kinect1.
     *
     * @return the raw depth values (range: 0 - 2047) as int array
     */
    public int[] getRawDepth() {
        return rawDepth;
    }

    public FloatBuffer getDephToWorldPositions() {

        rawDepthToWorldBuffer.put(rawDepthToWorld, 0, width * height * 3);
        rawDepthToWorldBuffer.rewind();

        return rawDepthToWorldBuffer;
    }

    /**
     * Stop getting depth from Kinect1.
     *
     */
    public void stopDepth() {
        device.stopDepth();
    }

    /**
     * Stop getting RGB video from Kinect1.
     *
     */
    public void stopVideo() {
        device.stopVideo();
        videoEnabled = false;
    }

    @Override public void initSensor() {}

    /**
     * Start getting depth from Kinect1 (available as raw array or mapped to image)
     *
     */
    @Override public void initSensorDepth() {

        /**
         * Clients who have configured Fakenect but do not support the library
         * Currently only Windows
         */
        if (OpenISSConfig.SENSOR_TYPE == OpenISSConfig.SensorType.FAKENECT && !Freenect.LIB_IS_LOADED) {
            try {
                useFileSystemDepth();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        /**
         * Both Freenect and Fakenect are disabled
         */
        if (OpenISSConfig.SENSOR_TYPE != OpenISSConfig.SensorType.FREENECT && 
            OpenISSConfig.SENSOR_TYPE != OpenISSConfig.SensorType.FAKENECT) {
            return;
        }

        if (!started) {
            start();
        }

        if (device != null) {
            device.setDepthFormat(DepthFormat.D11BIT);
            final Kinect1 ref = this;
            device.startDepth(new DepthHandler() {
                public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
                    depth = frame.asShortBuffer();
                }
            });
        }
    }


    /**
     * Start getting RGB video from Kinect1.
     *
     */
    @Override public void initSensorVideo() {

        /**
         * Clients who have configured Fakenect but do not support the library
         * Currently only Windows
         */
        if (OpenISSConfig.SENSOR_TYPE == OpenISSConfig.SensorType.FAKENECT && !Freenect.LIB_IS_LOADED) {
            try {
                useFileSystemColor();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        /**
         * Both Freenect and Fakenect are disabled
         */
        if (OpenISSConfig.SENSOR_TYPE != OpenISSConfig.SensorType.FREENECT && 
            OpenISSConfig.SENSOR_TYPE != OpenISSConfig.SensorType.FAKENECT) {
            return;
        }

        if (!started) {
            start();
        }
        if (device != null && !videoEnabled) {
            videoEnabled = true;
            final Kinect1 ref = this;
            if (irMode) {
                System.out.println("setting video format to IR_8bit");
                device.setVideoFormat(VideoFormat.IR_8BIT);
            } else {
                System.out.println("settinv video format to RGB");
                device.setVideoFormat(VideoFormat.RGB);
            }
            device.startVideo(new VideoHandler() {
                public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
                    color = new byte[frame.remaining()];
                    frame.get(color);
                    frame.flip();
                }
            });
        }
    }

    /**
     * Set the tilt angle of the Kinect1.
     *
     * @param deg the angle (in degrees, range 0-30).
     */
    public void setTilt(float deg) {
        if (device != null) {
            device.setTiltAngle(deg);
        }
    }

    /**
     * Get the tilt angle of the Kinect1.
     *
     * @return the angle
     */
    public float getTilt() {
        if (device != null) {
            return (float) device.getTiltAngle();
        } else {
            return 0;
        }
    }

    /**
     * Enable mirror mode for all frames
     * @param mirror
     */
    public void enableMirror(boolean mirror) {
        mirrorMode = mirror;
    }

    /**
     * Enable IR image (instead of RGB video)
     *
     * @param b true to turn it on, false to turn it off
     */
    public void enableIR(boolean b) throws InterruptedException {
        // If nothing has changed let's not do anything
        if (irMode == b) {
            return;
        }

        irMode = b;

        if (videoEnabled) {
            stopVideo();
        }


        if (irMode) {
            device.setVideoFormat(VideoFormat.IR_8BIT);
        } else {
            device.setVideoFormat(VideoFormat.RGB);
        }
        if (!videoEnabled) {
            initSensorVideo();
        }


    }

    /**
     * Enable mapping depth values to color image (instead of grayscale)
     *
     * @param b true to turn it on, false to turn it off
     */
    public void enableColorDepth(boolean b) {
        colorDepthMode = b;
    }

    /**
     * Get the depth image (does not make a new object, use get() if you need a copy)
     *
     * @return reference to depth image
     */
    @Override public BufferedImage getSensorDepthImage() {
        byte[] imageInBytes;

        try {
            /**
             * Clients who have configured Fakenect but do not support the library
             * Currently only Windows
             * @return current image from FAKENECT_PATH recorded session
             */
            if (!Freenect.LIB_IS_LOADED && (OpenISSConfig.SENSOR_TYPE == OpenISSConfig.SensorType.FAKENECT)) {
                imageInBytes = Files.readAllBytes(new File(getFileName("depth")).toPath());
                ByteBuffer buf = ByteBuffer.wrap(imageInBytes);
                return Utils.processPGMImage(640, 480, buf.asShortBuffer());
            }

            /**
             * Clients who support the library and use Kinect1 or Fakenect
             * @return current image from Kinect1 Live Stream or FAKENECT_PATH recorded session
             */
            else if (Freenect.LIB_IS_LOADED && (OpenISSConfig.SENSOR_TYPE == OpenISSConfig.SensorType.FREENECT ||
                     OpenISSConfig.SENSOR_TYPE == SensorType.FAKENECT)) {
                return Utils.processPGMImage(640, 480, depth);
            }

            /**
             * Everything is false in your OpenISSConfig
             * @return depth_fail.jpg from resources
             */
            else {
                System.err.println("Falling back to static images as last resort since no Kinect1" +
								   " libraries are loaded");
                return ImageIO.read(new File(classLoader.getResource(depthFailFileName).getFile()));
            }
        }

        /**
         * Image not found (404)
         * @return empty image
         */ catch (Exception e) {
            System.out.println(e.getMessage());
            return Utils.processPGMImage(640, 480, new byte[0]);
        }
    }

    /**
     * Get the video image (does not make a new object, use get() if you need a copy)
     *
     * @return reference to video image
     */
    @Override public BufferedImage getSensorVideoImage() {
        byte[] imageInBytes = new byte[0];
        try {
            /**
             * Clients who have configured Fakenect but do not support the library. Currently
			 * only Windows
             * @return current image from FAKENECT_PATH recorded session
             */
            if (!Freenect.LIB_IS_LOADED && OpenISSConfig.SENSOR_TYPE == OpenISSConfig.SensorType.FAKENECT) {
                imageInBytes = Files.readAllBytes(new File(getFileName("color")).toPath());
                return Utils.processPPMImage(640, 480, imageInBytes);
            }

            /**
             * Clients who support the library and use Kinect1 or Fakenect
             * @return current image from Kinect1 Live Stream or FAKENECT_PATH recorded session
             */
            else if (Freenect.LIB_IS_LOADED && (OpenISSConfig.SENSOR_TYPE == OpenISSConfig.SensorType.FREENECT || 
                     OpenISSConfig.SENSOR_TYPE == SensorType.FAKENECT)) {
                return Utils.processPPMImage(640, 480, color);
            }

            /**
             * Everything is false in your OpenISSConfig
             * @return color_fail.jpg from resources
             */
            else {
                System.err.println("Falling back to static images as last resort since no Kinect1" +
								   " libraries are loaded");
                return ImageIO.read(new File(classLoader.getResource(colorFailFileName).getFile()));
            }

        }

        /**
         * Image not found (404)
         * @return empty image
         */ catch (Exception e) {
            System.out.println(e.getMessage());
            return Utils.processPPMImage(640, 480, new byte[0]);

        }
    }


    static void useFileSystemDepth() throws InterruptedException {
        OpenISSSOAPServiceImpl service = new OpenISSSOAPServiceImpl();

        // get files in fake recording directory
        File dir = new File(FAKENECT_PATH);
        File[] directoryFiles = dir.listFiles();

        // store file names in here (both ppm and pgm)
        // they are accessed using an offset
        ArrayList<String> fileNames = new ArrayList<>(770);


        if (directoryFiles != null) {


            new Thread(new Runnable() {

                @Override
                public void run() {

                    int pgmCount = 0;

                    // loop, count and populate arraylist of file names
                    for (int i = 0; i < directoryFiles.length; i++) {
                        if (directoryFiles[i].getName().endsWith(".pgm")) {
                            fileNames.add(directoryFiles[i].getName());
                            pgmCount++;
                        }
                    }

                    // sort array of names
                    Collections.sort(fileNames);


                    // offset for this recording

                    System.out.println("useFileSystemDepth Reading from filesystem..");
                    System.out.println("pgm=" + pgmCount);
                    System.out.println("useFileSystemDepth Starting loop...");

                    // loop forever
                    while (true) {
                        for (int i = 0; i < pgmCount; i++) {
                            setDepthFileName(FAKENECT_PATH + "/" + fileNames.get(i));
                            try {
                                TimeUnit.MILLISECONDS.sleep(150);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        System.out.println("useFileSystemDepth Re-Looping...");
                    }

                }
            }).start();

        }
    }

    static void useFileSystemColor() throws InterruptedException {
        OpenISSSOAPServiceImpl service = new OpenISSSOAPServiceImpl();


        // get files in fake recording directory
        File dir = new File(FAKENECT_PATH);
        File[] directoryFiles = dir.listFiles();

        // store file names in here (both ppm and pgm)
        // they are accessed using an offset
        ArrayList<String> fileNames = new ArrayList<>(770);


        if (directoryFiles != null) {


            new Thread(new Runnable() {

                @Override
                public void run() {

                    int ppmCount = 0;

                    // loop, count and populate arraylist of file names
                    for (int i = 0; i < directoryFiles.length; i++) {
                        if (directoryFiles[i].getName().endsWith(".ppm")) {
                            fileNames.add(directoryFiles[i].getName());
                            ppmCount++;
                        }
                    }

                    // sort array of names
                    Collections.sort(fileNames);

                    // offset for this recording
                    System.out.println("useFileSystemColor Reading from filesystem..");
                    System.out.println("ppm=" + ppmCount);
                    System.out.println("useFileSystemColor Starting loop...");

                    while (true) {
                        for (int i = 0; i < ppmCount; i++) {
                            setColorFileName(FAKENECT_PATH + "/" + fileNames.get(i));
                            try {
                                TimeUnit.MILLISECONDS.sleep(150);

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        System.out.println("useFileSystemColor Re-Looping...");
                    }

                }
            }).start();
        }
    }

    public String getFileName(String type) {
        if (type.equalsIgnoreCase("color")) {
            return colorFileName;
        } else {
            return depthFileName;
        }
    }

    public static void setColorFileName(String fileName) {
        colorFileName = fileName;
    }

    public static void setDepthFileName(String fileName) {
        depthFileName = fileName;
    }


}