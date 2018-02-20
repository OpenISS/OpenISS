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
    

    public void fromByteToJpg(byte[] imageBytes) {
        InputStream in = new ByteArrayInputStream(imageBytes);

        //Implementation
       
    }

}



