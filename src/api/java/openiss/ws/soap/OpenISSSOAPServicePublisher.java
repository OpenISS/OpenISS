package soen487.web.service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.ws.Endpoint;  

/**
 * Endpoint publisher
 *
 * @author zephi
 */
public class Publisher extends HttpServlet{  
    public void init(ServletConfig config) throws ServletException {
        Endpoint.publish("http://localhost:7779/ws/xmlread", new XMLReaderImpl());  
   }
//    public static void main(String[] args) {  
//       Endpoint.publish("http://localhost:7779/ws/xmlread", new XMLReaderImpl());  
//    }  
}  