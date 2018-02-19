package api.java.openiss.ws.soap.endpoint;

import api.java.openiss.ws.soap.service.OpenISSSOAPServiceImpl;

import javax.xml.ws.Endpoint;


public class ServicePublisher {

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:9090/openiss", new OpenISSSOAPServiceImpl());
    }
}
