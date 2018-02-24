package api.java.openiss.ws.soap.endpoint;

import api.java.openiss.ws.soap.service.OpenISSSOAPServiceImpl;

import javax.xml.ws.Endpoint;


public class ServicePublisher {

    static int port = 9090;
    static String service = "openiss";
    static String url = "http://0.0.0.0:" + port + "/" + service;

    public static void main(String[] args) {
        System.out.println("SOAP Service listening on " + url + "?wsdl");
        Endpoint.publish(url, new OpenISSSOAPServiceImpl());
    }
}
