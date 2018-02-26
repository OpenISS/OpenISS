package api.java.openiss.ws.soap.endpoint;

import api.java.openiss.ws.soap.service.OpenISSSOAPServiceImpl;
import javax.xml.ws.Endpoint;

public class OpenISSSOAPServicePublisher {


    public static void main(String[] args) {
        Endpoint.publish("http://localhost:7779/ws/xmlread", new OpenISSSOAPServiceImpl());
        System.out.println("SOAP Service listening on http://localhost:7779/ws/xmlread?wsdl");
    }
}  