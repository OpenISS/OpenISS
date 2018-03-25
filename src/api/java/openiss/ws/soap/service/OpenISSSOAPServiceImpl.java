package openiss.ws.soap.service;

import javax.jws.WebService;

import static openiss.ws.soap.endpoint.ServicePublisher.driver;

@WebService(endpointInterface="openiss.ws.soap.service.OpenISSSOAPService")
public class OpenISSSOAPServiceImpl implements OpenISSSOAPService{

    public byte[] getFrame(String type) {
        return driver.getFrame(type);
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
      return driver.mixFrame(image,type,op);
    }

}
