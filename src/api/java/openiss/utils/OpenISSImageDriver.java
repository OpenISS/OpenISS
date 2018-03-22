package openiss.utils;

import openiss.Kinect;
import openiss.ws.soap.endpoint.ServicePublisher;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static openiss.ws.soap.endpoint.ServicePublisher.kinect;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.highgui.Highgui;
import org.opencv.core.Rect;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class OpenISSImageDriver {

    private ClassLoader classLoader = getClass().getClassLoader();

    /**
     * Retrives a frame from either a real Kinect or fakenect
     * @param type
     * @return jpeg image as a byte array
     */
    public byte[] getFrame(String type) {

        byte[] imageInBytes = new byte[0];
        byte[] jpgImageInByte = new byte[0];

        try {
            BufferedImage image;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // validity checks
            if (!type.equals("color") && !type.equals("depth")) {
                throw new IllegalArgumentException("Bad type for getFrame: " + type);
            }

            if (ServicePublisher.USE_FREENECT) {
                if (type.equals("color")) {
                    image = kinect.getVideoImage();
                }
                else {
                    image = kinect.getDepthImage();
                }
            }
            else {
                if (type.equals("color")) {
                    image = ImageIO.read(new File(classLoader.getResource("color_example.jpg").getFile()));
                }
                else {
                    image = ImageIO.read(new File(classLoader.getResource("depth_example.jpg").getFile()));
                }
            }

            ImageIO.write(image, "jpg", baos);
            baos.flush();
            jpgImageInByte = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jpgImageInByte;
    }

    /**
     * Mixes a jpg image with one from the kinect/fakenect
     * @param image jpg byte array from the client
     * @param type color or depth
     * @param op operand (only single operand handled as of now)
     * @return jpg image as a byte array
     */
    public byte[] mixFrame(byte[] image, String type, String op)
    {
        System.out.println("Mixing frame, type=" + type + ", op="+op);

        // check validity
        if (!type.equals("color") && !type.equals("depth")) {
            throw new IllegalArgumentException("Bad type for getFrame: " + type);
        }

        // weight for bleding, 0.5 = 50% of both images
        double weight = 0.5;

        // init images
        byte[] imageInBytes;

        BufferedImage image_1 = null;
        BufferedImage image_2 = null;

        InputStream bain = new ByteArrayInputStream(image);

        // convert client image to BufferedImage image_1
        try {
            image_1 = ImageIO.read(bain);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // convert kinect/fakenect image to BufferedImage image_2
        try {

            if (ServicePublisher.USE_FREENECT) {
                if (type.equals("color")) {
                    image_2 = kinect.getVideoImage();
                }
                else {
                    image_2 = kinect.getDepthImage();
                }
            }
            else {
                if (type.equals("color")) {
                    image_2 = ImageIO.read(new File(classLoader.getResource("color_example.jpg").getFile()));
                }
                else {
                    image_2 = ImageIO.read(new File(classLoader.getResource("depth_example.jpg").getFile()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // check height and width
        int width = image_1.getWidth();
        int height = image_2.getHeight();

        // check equal size
        if(width != image_2.getWidth() || height != image_2.getHeight()) {
            throw new IllegalArgumentException("dimensions are not equal.");
        }

        // create new mixed image and alpha weight
        BufferedImage mixed_image = new BufferedImage (width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = mixed_image.createGraphics();
        float alpha = (float)(1.0 - weight);

        // mix both images into a third one
        g.drawImage (image_1, null, 0, 0);
        g.setComposite (AlphaComposite.getInstance (AlphaComposite.SRC_OVER, alpha));
        g.drawImage (image_2, null, 0, 0);
        g.dispose();

        // transform mixed image in jpeg byte array
        byte[] imageInByte = new byte[0];
        try {
            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(mixed_image, "jpg", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();

            // fromByteToJpg(imageInByte);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Frame mixed. Sending jpg result to client");

        return imageInByte;
    }

    //TODO change function to take byte[] as input parameter and return variable
    public byte[] doCanny(byte[] image) {
    	try {

    		Mat mat = Imgcodecs.imdecode(new MatOfByte(image), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);

//    		InputStream bain = new ByteArrayInputStream(image);
//    		BufferedImage image_1;
//
//    		// convert client image to BufferedImage image_1
//            try {
//                image_1 = ImageIO.read(bain);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            Mat gray = new Mat();
            Mat draw = new Mat();
            Mat wide = new Mat();

            Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.Canny(gray, wide, 50, 150, 3, false);
            wide.convertTo(draw, CvType.CV_8U);
            mat.get(0, 0, image);

//            if (Imgcodecs.imwrite(filename, draw)) {
//                System.out.println("edge is detected .......");
//            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    	return image;
    }

    public byte[] contour(byte[] image) {
    	try {
    		Mat color = Imgcodecs.imdecode(new MatOfByte(image), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    		Mat gray = new Mat();
    		Mat binarized = new Mat();
            Mat draw = new Mat();
            Imgproc.cvtColor(color, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(gray, binarized, 100, 255, Imgproc.THRESH_BINARY);
            final List<MatOfPoint> points = new ArrayList<>();
            final Mat hierarchy = new Mat();
            Imgproc.findContours(binarized, points, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            binarized.convertTo(draw, CvType.CV_8U);
            color.get(0,0,image); // get all the pixels
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}

    	return image;
    }

}
