package openiss;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import openiss.utils.OpenISSConfig;
import openiss.ws.soap.service.OpenISSSOAPServiceImpl;

import openiss.utils.Utils;
import openiss.utils.OpenISSConfig.SensorType;

// This class will be used when USE_STATIC_IMAGES is enabled
public class StaticSensor implements Sensor {

    public static String colorFileName = "color_example.jpg";
    public static String depthFileName = "depth_example.jpg";
    public static String colorFailFileName = "color_fail.jpg";
    public static String depthFailFileName = "depth_fail.jpg";

    public ClassLoader classLoader = getClass().getClassLoader();

    @Override public void initSensor() {}
    @Override public void initSensorDepth() {}
    @Override public void initSensorVideo() {}

    @Override public BufferedImage getSensorVideoImage() {
        try {
            /**
             * Clients who do not have Kinect1 and have not configured Fakenect
             * @return color_example.jpg from resources
             */
            if(OpenISSConfig.SENSOR_TYPE == OpenISSConfig.SensorType.STATIC_SENSOR) {
                return ImageIO.read(new File(classLoader.getResource(colorFileName).getFile()));
            }
            else {
                return ImageIO.read(new File(classLoader.getResource(colorFailFileName).getFile()));
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return Utils.processPPMImage(640, 480, new byte[0]);
        }
    }

    @Override public BufferedImage getSensorDepthImage() {
        try {
            /**
             * Clients who do not have Kinect1 and have not configured Fakenect
             * @return depth_example.jpg from resources
             */
            if(OpenISSConfig.SENSOR_TYPE == OpenISSConfig.SensorType.STATIC_SENSOR) {
                return ImageIO.read(new File(classLoader.getResource(depthFileName).getFile()));
            }
            else {
                return ImageIO.read(new File(classLoader.getResource(depthFailFileName).getFile()));
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return Utils.processPGMImage(640, 480, new byte[0]);
        }
    }

    public static String getFileName(String type) {
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