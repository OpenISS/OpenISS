package openiss.ws.soap.service;

import com.sun.javafx.runtime.SystemProperties;
import openiss.Kinect;
import openiss.utils.OpenISSConfig;
import openiss.ws.soap.endpoint.ServicePublisher;

import javax.imageio.ImageIO;
import javax.jws.WebService;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static openiss.ws.soap.endpoint.ServicePublisher.kinect;

@WebService(endpointInterface="openiss.ws.soap.service.OpenISSSOAPService")
public class OpenISSSOAPServiceImpl implements OpenISSSOAPService{

    static openiss.utils.OpenISSImageDriver driver;

    static {
        driver = new openiss.utils.OpenISSImageDriver();
    }


    private static String colorFileName = "src/api/java/openiss/ws/soap/service/color_fail.jpg";
    private static String depthFileName = "src/api/java/openiss/ws/soap/service/depth_fail.jpg";
    static String FAKENECT_PATH = System.getenv("FAKENECT_PATH");

    public byte[] getFrame(String type) {

       return driver.getFrame(type);
    }

    /**
     * Mixes a jpg image with one from the kinect/fakenect
     * @param image jpg byte array from the client
     * @param type color or depth
     * @param op operand (only single operand handled as of now)
     * @return jpg byte array
     */
    public byte[] mixFrame(byte[] image, String type, String op)
    {
      return driver.mixFrame(image,type,op);
    }

    public String getFileName(String type) {
        if(type.equalsIgnoreCase("color")){
            return colorFileName;
        } else {
            return depthFileName;
        }
    }

    public void setColorFileName(String fileName) {
        colorFileName = fileName;
    }

    public void setDepthFileName(String fileName) {
        depthFileName = fileName;
    }

}
