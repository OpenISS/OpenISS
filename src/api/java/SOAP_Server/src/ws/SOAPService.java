package ws;
import javax.jws.*;

@WebService
public interface SOAPService {
	
	@WebMethod
	byte[] getFrame();
	@WebMethod
	byte[] getBytes(String imageName);
	@WebMethod
	public void fromByteToJpg(byte[] imageBytes);
}
