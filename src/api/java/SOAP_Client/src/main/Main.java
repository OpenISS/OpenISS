package main;
import ws.*;

public class Main {

	public static void main(String[] args) {
		try {
			SOAPServiceImplService soapService = new SOAPServiceImplServiceLocator();
			SOAPService soap = soapService.getSOAPServiceImplPort();


			byte[] imageBytes = soap.getBytes("src/image_example.jpg");
			soap.fromByteToJpg(imageBytes);
		}
		catch(Exception e) {
			System.out.println("Error");
		}

	}

}
