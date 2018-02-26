package api.java.openiss.ws.soap.endpoint;

import api.java.openiss.ws.soap.service.OpenISSSOAPServiceImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.xml.ws.Endpoint;  

/**
 * Endpoint publisher
 *
 * @author zephi
 */

@WebServlet(name = "OpenISSSOAPServicePublisher")
public class OpenISSSOAPServicePublisher extends HttpServlet{
    public void init(ServletConfig config) throws ServletException {
        Endpoint.publish("http://localhost:7779/ws/xmlread", new OpenISSSOAPServiceImpl());
   }
//    public static void main(String[] args) {

//       Endpoint.publish("http://localhost:7779/ws/xmlread", new OpenISSSOAPServiceImpl());
//    }
}  