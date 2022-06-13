package openiss.legacy;

import java.awt.image.BufferedImage;

// Interface used to simplify driver code
public interface Sensor {
    public BufferedImage getSensorVideoImage();
    public BufferedImage getSensorDepthImage();
    public void initSensorVideo();
    public void initSensorDepth();
    public void initSensor();
}