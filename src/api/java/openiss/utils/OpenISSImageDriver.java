package openiss.utils;

import openiss.Kinect;
import openiss.ws.soap.endpoint.ServicePublisher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static openiss.ws.soap.endpoint.ServicePublisher.kinect;

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

}
