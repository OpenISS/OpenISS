package openiss.ws.soap.service;

import openiss.Kinect;
import openiss.ws.soap.endpoint.ServicePublisher;

import javax.imageio.ImageIO;
import javax.jws.WebService;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

import static openiss.ws.soap.endpoint.ServicePublisher.kinect;

@WebService(endpointInterface="openiss.ws.soap.service.OpenISSSOAPService")
public class OpenISSSOAPServiceImpl implements OpenISSSOAPService{

    private static String fileName = "src/api/java/openiss/ws/soap/service/image_example.jpg";
    static String FAKENECT_PATH = System.getenv("FAKENECT_PATH");

    public String getFileName(String type) {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFrame(String type) {

        byte[] ppmImageInByte = new byte[0];
        byte[] jpgImageInByte = new byte[0];

        BufferedImage originalImage = null;
        try {

            String src = FAKENECT_PATH + "/" + getFileName("color");
            BufferedImage image;
            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //Kinect kinect = new Kinect();

            if (!type.equals("color") && !type.equals("depth")) {
                throw new IllegalArgumentException("Bad type for getFrame: " + type);
            }

            if (ServicePublisher.USE_FILESYSTEM) {
                File initialFile = new File(src);
                ppmImageInByte = Files.readAllBytes(initialFile.toPath());
                image = Kinect.processPPMImage(640, 480, ppmImageInByte);
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

}
