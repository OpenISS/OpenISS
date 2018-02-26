package soen487.servlets;

import java.io.IOException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import soen487.web.service.XMLReader;

@WebServlet(name = "OpenISSSOAPClient", urlPatterns = {"/OpenISSSOAPClient"})
public class OpenISSSOAPClient extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uri = request.getParameter("url");
        String type = request.getParameter("requestType");
        String fileIn = request.getParameter("fileIn");

//        URL url = new URL("http://localhost:7779/ws/xmlread?wsdl");
        URL url = new URL(null, "http://localhost:7779/ws/xmlread?wsdl", new sun.net.www.protocol.http.Handler());

        QName qname = new QName("http://service.web.soen487/", "XMLReaderImplService");
        Service service = Service.create(url, qname);
        XMLReader xmlRead = service.getPort(XMLReader.class);
        if(!fileIn.isEmpty() && !fileIn.equals("")){
            xmlRead.doCanny(fileIn);
        }
        String result = xmlRead.fetchXML(uri, type);
        request.setAttribute("result", result);
        request.getRequestDispatcher("/WEB-INF/views/XMLOutput.jsp").forward(request, response);
    }

}
