package api.java.openiss.ws.soap.endpoint;

import api.java.openiss.ws.soap.service.OpenISSSOAPServiceImpl;

import javax.xml.ws.Endpoint;


public class ServicePublisher {

    static String url = "http://localhost:9090/openiss";

    public static void main(String[] args) {
        System.out.println("SOAP Service listening on " + url + "?wsdl");
        Endpoint.publish(url, new OpenISSSOAPServiceImpl());
    }
}
