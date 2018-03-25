package openiss.ws.soap.endpoint;

import openiss.utils.OpenISSImageDriver;
import openiss.ws.soap.service.OpenISSSOAPServiceImpl;
import javax.xml.ws.Endpoint;

public class ServicePublisher {

    static int port = 9090;
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