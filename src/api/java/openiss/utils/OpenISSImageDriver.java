package openiss.utils;

import openiss.Kinect1;
import openiss.Kinect2;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//import org.opencv.highgui.Highgui;

public class OpenISSImageDriver {

//    static Kinect1 kinect;
    static Kinect2 kinect;

    static {
        kinect = new Kinect2();
//        kinect = new Kinect1();
        System.out.println("initVideo");

        kinect.initVideo();
        kinect.initDepth();

        kinect.initDevice();
    }

    /**
     * Retrives a frame from either a real Kinect1 or fakenect
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

            if (type.equals("color")) {
                image = kinect.getVideoImage();
            }
            else {
                image = kinect.getDepthImage();
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
        if (type.equals("color")) {
            image_2 = kinect.getVideoImage();
        }
        else {
            image_2 = kinect.getDepthImage();
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

            Mat color = Imgcodecs.imdecode(new MatOfByte(image), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);

            Mat gray = new Mat();
            Mat draw = new Mat();
            Mat wide = new Mat();

            Imgproc.cvtColor(color, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.Canny(gray, wide, 50, 150, 3, false);
            wide.convertTo(draw, CvType.CV_8U);

            // Encoding the image
            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", draw, matOfByte);

            return matOfByte.toArray();

        } catch (Exception e) {
        	e.printStackTrace();
        }
    	return image;
    }

    public byte[] contour(byte[] image) {
    	try {
    		Mat color = Imgcodecs.imdecode(new MatOfByte(image), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    		Mat gray = new Mat();
    		Mat binarized = new Mat();
            Mat draw = new Mat();
            Imgproc.cvtColor(color, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(gray, binarized, 100, 255, Imgproc.THRESH_BINARY);
            final List<MatOfPoint> points = new ArrayList<>();
            final Mat hierarchy = new Mat();
            Imgproc.findContours(binarized, points, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            binarized.convertTo(draw, CvType.CV_8U);
            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", draw, matOfByte);

            return matOfByte.toArray();
        }
    	catch (Exception e) {
    		e.printStackTrace();
    	}

        return image;
    }

}
