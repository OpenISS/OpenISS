package openiss.ws.soap.service;

import javax.imageio.ImageIO;
import javax.jws.WebService;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

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

        System.out.println("getFrame: " + type);

        byte[] ppmImageInByte = new byte[0];
        byte[] jpgImageInByte = new byte[0];

        BufferedImage originalImage = null;
        try {

            String src = FAKENECT_PATH + "/" + getFileName("color");

            File initialFile = new File(src);

            ppmImageInByte = Files.readAllBytes(initialFile.toPath());

            BufferedImage image = processPPMImage(640, 480, ppmImageInByte);

            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            jpgImageInByte = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jpgImageInByte;
    }

    static private BufferedImage processPPMImage(int width, int height, byte[] data){
        BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        int red,green,blue,k=0,pixel;
        for(int y=0;y<height;y++){
            for(int x=0;(x<width)&&((k+3)<data.length);x++){
                red=data[k++] & 0xFF;
                green=data[k++] & 0xFF;
                blue=data[k++] & 0xFF;
                pixel=0xFF000000+(red<<16)+(green<<8)+blue;
                image.setRGB(x,y,pixel);
            }
        }
        return image;
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
        byte[] imageBytes = o.getBytes(fileName);
        o.fromByteToJpg(imageBytes);
    }

}
