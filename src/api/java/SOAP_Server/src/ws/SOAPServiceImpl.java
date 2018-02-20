package ws;
import javax.jws.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@WebService(endpointInterface="ws.SOAPService")
public class SOAPServiceImpl implements SOAPService {

	@Override
	public byte[] getFrame() {
	
        return null;

    }
	
    public byte[] getBytes(String imageName) {
        byte[] imageInByte = new byte[0];

        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(new File(
                    imageName));

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
    

    public void fromByteToJpg(byte[] imageBytes) {
        InputStream in = new ByteArrayInputStream(imageBytes);

        //added implementation
        try {
            BufferedImage bImageFromConvert = ImageIO.read(in);
            File outputfile = new File("src/new-image.jpg");
            

            ImageIO.write(bImageFromConvert, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

}



