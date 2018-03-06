package openiss.ws.soap.service;
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

    @WebMethod
    String getFileName(@WebParam(name = "type") String type);

    @WebMethod
    byte[] mixFrame(@WebParam(name = "image") byte[] image,
                    @WebParam(name = "type") String type,
                    @WebParam(name = "op") String op);

	@WebMethod
    void doCanny(@WebParam(name = "fileName") String filename);
    
}
