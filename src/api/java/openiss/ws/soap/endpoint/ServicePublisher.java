package openiss.ws.soap.endpoint;

import openiss.Kinect;
import openiss.utils.OpenISSConfig;
import openiss.utils.OpenISSImageDriver;
import openiss.ws.soap.service.OpenISSSOAPService;
import openiss.ws.soap.service.OpenISSSOAPServiceImpl;
import javax.xml.ws.Endpoint;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class ServicePublisher {

    static int port = 9090;
    static String service = "openiss";
    static String url = "http://localhost:" + port + "/" + service;
    static String FAKENECT_PATH = System.getenv("FAKENECT_PATH");
    public static Kinect kinect;



    public static OpenISSImageDriver driver;

    static {
        driver = new OpenISSImageDriver();
        kinect = new Kinect();
    }



    public static void main(String[] args) throws Exception {
        System.out.println("SOAP Service listening on " + url + "?wsdl");

        OpenISSSOAPServiceImpl service = new OpenISSSOAPServiceImpl();
        Endpoint.publish(url, service);
    }
}