package openiss.ws.soap.service;

import javax.jws.WebService;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static openiss.ws.soap.endpoint.ServicePublisher.driver;

@WebService(endpointInterface="openiss.ws.soap.service.OpenISSSOAPService")
public class OpenISSSOAPServiceImpl implements OpenISSSOAPService{

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

    /**
     *
     * @param filename
     */
    @Override
    public void doCanny(String filename) {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            Mat color = Imgcodecs.imread(filename);

            Mat gray = new Mat();
            Mat draw = new Mat();
            Mat wide = new Mat();

            Imgproc.cvtColor(color, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.Canny(gray, wide, 50, 150, 3, false);
            wide.convertTo(draw, CvType.CV_8U);

            if (Imgcodecs.imwrite(filename, draw)) {
                System.out.println("edge is detected .......");
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

}
