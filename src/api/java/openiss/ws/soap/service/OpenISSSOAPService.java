package api.java.openiss.ws.soap.service;

import javax.jws.WebMethod;  
import javax.jws.WebService;  
import javax.jws.soap.SOAPBinding;  
import javax.jws.soap.SOAPBinding.Style;  

/**
 * Service Endpoint Interface
 *
 * @author zephi
 */

@WebService  
@SOAPBinding(style = Style.DOCUMENT)  
public interface OpenISSSOAPService {
    @WebMethod String fetchXML(String url, String reqType);  
    @WebMethod void doCanny(String filename);
}  
