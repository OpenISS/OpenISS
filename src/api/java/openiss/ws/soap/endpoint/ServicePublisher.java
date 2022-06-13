package openiss.ws.soap.endpoint;

import openiss.utils.legacy.OpenISSImageDriver;
import openiss.ws.soap.service.OpenISSSOAPServiceImpl;
import javax.xml.ws.Endpoint;

public class ServicePublisher {

    static int port = Config.portNumber;
    static String service = "openiss";
    static String url = "http://localhost:" + port + "/" + service;
    public static OpenISSImageDriver driver;

    static {
        driver = new OpenISSImageDriver();
    }

    public static void main(String[] args) {
        System.out.println("SOAP Service listening on " + url + "?wsdl");
        OpenISSSOAPServiceImpl service = new OpenISSSOAPServiceImpl();
        Endpoint.publish(url, service);
    }
}