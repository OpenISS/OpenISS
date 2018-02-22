package api.java.openiss.ws.soap.service;

import javax.imageio.ImageIO;
import javax.jws.WebService;
import java.awt.image.BufferedImage;
import java.io.*;

@WebService(endpointInterface="api.java.openiss.ws.soap.service.OpenISSSOAPService")
public class OpenISSSOAPServiceImpl implements OpenISSSOAPService{

    public byte[] getFrame(String type) {

        // todo:
        // get frame from API
        // convert to byte
        // return byte array
        System.out.println("Requested type = " + type);

        return ("koko lala: " + type).getBytes();

//        return null;

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

    // demo of the functions
    public static void main(String[] args) {
        OpenISSSOAPServiceImpl o = new OpenISSSOAPServiceImpl();
        byte[] imageBytes = o.getBytes("src/api/java/openiss/ws/soap/service/image_example.jpg");
        o.fromByteToJpg(imageBytes);
    }

}
