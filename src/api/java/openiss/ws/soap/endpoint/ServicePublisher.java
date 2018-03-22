package openiss.ws.soap.endpoint;

import openiss.Kinect;
import openiss.utils.OpenISSConfig;
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

    private static String colorFileName = "src/api/java/openiss/ws/soap/service/color_example.jpg";
    private  static String depthFileName = "src/api/java/openiss/ws/soap/service/depth_example.jpg";
    public static Kinect kinect;

    static {
        if(OpenISSConfig.USE_FREENECT) {
            kinect = new Kinect();
            try {
                kinect.initVideo();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                kinect.initDepth();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("SOAP Service listening on " + url + "?wsdl");

        OpenISSSOAPServiceImpl service = new OpenISSSOAPServiceImpl();
        Endpoint.publish(url, service);

        if (OpenISSConfig.USE_FAKENECT) {

            // get files in fake recording directory
            File dir = new File(FAKENECT_PATH);
            File[] directoryFiles = dir.listFiles();

            // store file names in here (both ppm and pgm)
            // they are accessed using an offset
            ArrayList<String> fileNames = new ArrayList<>(770);

            int ppmCount = 0;
            int pgmCount = 0;

            if(directoryFiles != null) {

                // loop, count and populate arraylist of file names
                for (int i = 0; i < directoryFiles.length; i++) {
                    if(directoryFiles[i].getName().endsWith(".ppm")) {
                        fileNames.add(directoryFiles[i].getName());
                        ppmCount++;
                    } else if (directoryFiles[i].getName().endsWith(".pgm")) {
                        fileNames.add(directoryFiles[i].getName());
                        pgmCount++;
                    }
                }

                // sort array of names
                Collections.sort(fileNames);


                // offset for this recording
                int offset = (ppmCount > pgmCount) ? ppmCount : pgmCount;
                System.out.println("Reading from filesystem..");
                System.out.println("ppm="+ppmCount);
                System.out.println("pgm="+pgmCount);
                System.out.println("total="+(ppmCount+pgmCount));
                System.out.println("offset=" + offset);
                System.out.println("Starting loop...");


                // loop forever
                while(true) {
                    for(int i = 0; i < pgmCount; i++) {
                        service.setDepthFileName(fileNames.get(i));
                        service.setColorFileName(fileNames.get(i + offset));

                        TimeUnit.MILLISECONDS.sleep(150);
                    }
                    System.out.println("Looping..");
                }
            }
        }

        else {
//        else if (OpenISSConfig.USE_STATIC_IMAGES) {
            service.setDepthFileName(depthFileName);
            service.setColorFileName(colorFileName);
        }
    }
}