package api.java.openiss.ws.soap.service;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

@WebService
@BindingType(value = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface OpenISSSOAPService {

    @WebMethod
    byte[] getFrame(@WebParam(name = "type") String type);

}
