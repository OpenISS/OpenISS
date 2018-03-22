package openiss.ws.soap.service;

import com.sun.javafx.runtime.SystemProperties;
import openiss.Kinect;
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

    private static String colorFileName = "src/api/java/openiss/ws/soap/service/color_example.jpg";
    private static String depthFileName = "src/api/java/openiss/ws/soap/service/depth_example.jpg";
    static String FAKENECT_PATH = System.getenv("FAKENECT_PATH");

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

            // file system routine
            if (ServicePublisher.USE_FILESYSTEM) {
                String colorSrc = FAKENECT_PATH + "/" + getFileName("color");
                String depthSrc = FAKENECT_PATH + "/" + getFileName("depth");
                String source;

                // set source for file
                if(type.equals("color")) {
                    source = colorSrc;
                } else {
                    source = depthSrc;
                }

                // read bytes at the current path
                File frame = new File(source);
                imageInBytes = Files.readAllBytes(frame.toPath());
                image = Kinect.processPPMImage(640, 480, imageInBytes);
            }
            else if (ServicePublisher.USE_FREENECT) {
                if (type.equals("color")) {
                    image = kinect.getVideoImage();
                }
                else {
                    image = kinect.getDepthImage();
                }
            }
            else {
                if (type.equals("color")) {
                    image = ImageIO.read(new File(colorFileName));
                }
                else {
                    image = ImageIO.read(new File(depthFileName));
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


            if (ServicePublisher.USE_FILESYSTEM) {
                String colorSrc = FAKENECT_PATH + "/" + getFileName("color");
                String depthSrc = FAKENECT_PATH + "/" + getFileName("depth");
                String source;
                if(type.equals("color")) {
                    source = colorSrc;
                } else {
                    source = depthSrc;
                }

                File frame = new File(source);
                imageInBytes = Files.readAllBytes(frame.toPath());
                image_2 = Kinect.processPPMImage(640, 480, imageInBytes);
            }
            else if (ServicePublisher.USE_FREENECT) {
                if (type.equals("color")) {
                    image_2 = kinect.getVideoImage();
                }
                else {
                    image_2 = kinect.getDepthImage();
                }
            }
            else {
                if (type.equals("color")) {
                    image_2 = ImageIO.read(new File(colorFileName));
                }
                else {
                    image_2 = ImageIO.read(new File(depthFileName));
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
