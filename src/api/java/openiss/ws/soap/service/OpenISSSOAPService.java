package api.java.openiss.ws.soap.service;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface OpenISSSOAPService {

    @WebMethod
    byte[] getFrame(String type);
}
