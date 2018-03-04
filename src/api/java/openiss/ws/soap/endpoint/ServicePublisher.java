package openiss.ws.soap.endpoint;

import openiss.Kinect;
import openiss.ws.soap.service.OpenISSSOAPService;
import openiss.ws.soap.service.OpenISSSOAPServiceImpl;
import javax.xml.ws.Endpoint;
import java.io.File;
import java.util.concurrent.TimeUnit;


public class ServicePublisher {

    static int port = 9090;
    static String service = "openiss";
    static String url = "http://localhost:" + port + "/" + service;
    static String FAKENECT_PATH = System.getenv("FAKENECT_PATH");

    public static boolean USE_FAKENECT = true;
    public static boolean USE_FILESYSTEM = false;
    public static Kinect kinect;

    static {
        if(!USE_FILESYSTEM) {
            kinect = new Kinect();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("SOAP Service listening on " + url + "?wsdl");

        Endpoint.publish(url, new OpenISSSOAPServiceImpl());

        if (USE_FILESYSTEM) {
            OpenISSSOAPServiceImpl colorObject = new OpenISSSOAPServiceImpl();
            OpenISSSOAPServiceImpl depthObject = new OpenISSSOAPServiceImpl();

            File dir = new File(FAKENECT_PATH);
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                while(true){
                    for (File child : directoryListing) {

                        if(child.getName().endsWith(".ppm")){
                            TimeUnit.SECONDS.sleep(1);
                            colorObject.setColorFileName(child.getName());
                        }
                        if(child.getName().endsWith(".pgm")){
                            TimeUnit.SECONDS.sleep(1);
                            depthObject.setDepthFileName(child.getName());
                        }
                    }
                }
            } else {
                System.out.println("Error: no such directory");
            }
        }
        else {
            kinect.initVideo();
            kinect.initDepth();
        }
    }
}