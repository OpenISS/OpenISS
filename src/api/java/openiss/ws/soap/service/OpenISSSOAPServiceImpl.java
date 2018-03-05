package openiss.ws.soap.service;

import openiss.Kinect;
import openiss.ws.soap.endpoint.ServicePublisher;

import javax.imageio.ImageIO;
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

import static openiss.ws.soap.endpoint.ServicePublisher.kinect;

@WebService(endpointInterface="openiss.ws.soap.service.OpenISSSOAPService")
public class OpenISSSOAPServiceImpl implements OpenISSSOAPService{


    private static String colorFileName = "src/api/java/openiss/ws/soap/service/image_example.jpg";
    private static String depthFileName = "src/api/java/openiss/ws/soap/service/image_example.jpg";
    static String FAKENECT_PATH = System.getenv("FAKENECT_PATH");

    public String getFileName(String type) {

        if(type.equalsIgnoreCase("color")){
            return colorFileName;
        }
        else{
            return depthFileName;

        }

    }

    public void setColorFileName(String fileName) {

        this.colorFileName = fileName;

    }

    public void setDepthFileName(String fileName) {

        this.depthFileName = fileName;

    }



    public byte[] getFrame(String type) {

        byte[] ppmImageInByte = new byte[0];
        byte[] pgmImageInByte = new byte[0];
        byte[] jpgImageInByte = new byte[0];

        BufferedImage originalImage = null;

        try {

            String colorSrc = FAKENECT_PATH + "/" + getFileName("color");
            String depthSrc = FAKENECT_PATH + "/" + getFileName("depth");

            BufferedImage image;
            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //Kinect kinect = new Kinect();

            if (!type.equals("color") && !type.equals("depth")) {
                throw new IllegalArgumentException("Bad type for getFrame: " + type);
            }

            if (ServicePublisher.USE_FILESYSTEM) {
                File colorInitialFile = new File(colorSrc);
                File depthInitialFile = new File(depthSrc);
                ppmImageInByte = Files.readAllBytes(colorInitialFile.toPath());
                pgmImageInByte = Files.readAllBytes(depthInitialFile.toPath());

                if(type.equals("color")){
                    image = Kinect.processPPMImage(640, 480, ppmImageInByte);

                }else{
                    image = Kinect.processPPMImage(640, 480, pgmImageInByte);

                }
            }
            else {
                if (type.equals("color")) {
                    image = kinect.getVideoImage();
                }
                else {
                    image = kinect.getDepthImage();
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
     * @return jpg byte array
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
        byte[] ppmImageInByte;
        byte[] pgmImageInByte = new byte[0];

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


            if (ServicePublisher.USE_FILESYSTEM) {

                String colorSrc = FAKENECT_PATH + "/" + getFileName("color");
                String depthSrc = FAKENECT_PATH + "/" + getFileName("depth");

                File colorInitialFile = new File(colorSrc);
                File depthInitialFile = new File(depthSrc);
                ppmImageInByte = Files.readAllBytes(colorInitialFile.toPath());
                pgmImageInByte = Files.readAllBytes(depthInitialFile.toPath());

                if(type.equals("color")){
                    image_2 = Kinect.processPPMImage(640, 480, ppmImageInByte);

                }else{
                    image_2 = Kinect.processPPMImage(640, 480, pgmImageInByte);

                }

            } else {
                if (type.equals("color")) {
                    image_2 = kinect.getVideoImage();
                } else {
                    image_2 = kinect.getDepthImage();
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


    // helper for getting bytes of an image
    public byte[] getBytes(String imageName) {
        byte[] imageInByte = new byte[0];

        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(new File(
                    imageName));
            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageInByte;
    }

    // creates a jpeg from a byte array
    // technically no use for that since we unmarshall client side in Node.js
    public void fromByteToJpg(byte[] imageBytes) {
        InputStream in = new ByteArrayInputStream(imageBytes);

        try {
            BufferedImage bImageFromConvert = ImageIO.read(in);
            ImageIO.write(bImageFromConvert, "jpg", new File(
                    "new_image_example.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
