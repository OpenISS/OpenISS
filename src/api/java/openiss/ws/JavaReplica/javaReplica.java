package openiss.ws.JavaReplica;

import jersey.repackaged.com.google.common.io.ByteStreams;
import openiss.utils.OpenISSConfig;
import openiss.utils.OpenISSImageDriver;
import sun.misc.IOUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

// need to refactor
public class javaReplica { // receving client request
    public static void main(String[] args) {

        // obtain server stub
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/");
        client.property("accept", "image/*");
        // response if API is unresponsive
        Response.StatusType status = new Response.StatusType() {

            private int code = 666;
            private String msg = "API unresponsive";

            public Response.Status.Family getFamily() {
                return Response.Status.Family.INFORMATIONAL;
            }

            public String getReasonPhrase() {
                return msg;
            }

            public int getStatusCode() {
                return code;
            }

        };
        // unset canny and contour so the replica will process the image
        Response checkUnset;
        for (String unsetFlags : new String[]{"unsetCanny", "unsetContour"}) {
            try {
                checkUnset = target.path("openiss/" + unsetFlags)
                        .request(MediaType.TEXT_PLAIN)
                        .post(Entity.text("sending"));
            } catch (Exception e) {
                checkUnset = Response.status(status).build();
            }
            String responseText = checkUnset.getStatus() == 666 ?
                    checkUnset.getStatusInfo().getReasonPhrase() :
                    checkUnset.readEntity(String.class);
            System.out.println(responseText);
        }

        // listen to the multicast
        int multicastPort = 20000;
        MulticastSocket socket = null;
        InetAddress group = null;
        System.setProperty("java.net.preferIPv4Stack", "true");
        Response response = null;
        try {
            socket = new MulticastSocket(multicastPort);
            socket.setNetworkInterface(NetworkInterface.getByName("en0"));
            group = InetAddress.getByName("230.255.255.255");
            System.out.println("Joining Multicast Group");
            socket.joinGroup(group);

            DatagramPacket packet;
            while (true) {
                byte[] buf = new byte[1256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String serializedRequest = new String(packet.getData()).trim();
                String[] requestList = serializedRequest.split(",");
                String frameNumber = requestList[0];
                String transformationOperation = requestList[1];
                System.out.println(frameNumber + " " + transformationOperation);
                //get color from API according
                try {
                    response = target.path("openiss/color")
                            .request().get();
                } catch (Exception e) {
                    response = Response.status(status).build();
                }
                String responseText = response.getStatusInfo().getReasonPhrase();
                System.out.println(responseText);

                InputStream colorImgStream = response.readEntity(InputStream.class);
                OpenISSImageDriver driver = new OpenISSImageDriver();
                String PROJECT_HOME = System.getProperty("user.dir");
                int arch = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                String osName = System.getProperty("os.name").toLowerCase();
                if (OpenISSConfig.USE_OPENCV) {
                    if (osName.indexOf("win") >= 0) {
                        System.out.println(arch + " windows");
                        System.load(PROJECT_HOME + "\\lib\\opencv\\win\\x64\\opencv_java341.dll");
                    } else if (osName.indexOf("mac") >= 0) {
                        System.out.println("Loading Native library" + PROJECT_HOME + "/lib/opencv/mac/libopencv_java3412.dylib");
                        System.load(PROJECT_HOME + "/lib/opencv/mac/libopencv_java3412.dylib");
                    }
                }

                // Process according to instructions
                byte[] processedImage = new byte[1024];
                colorImgStream.read(processedImage);
                if (transformationOperation == "Canny") driver.doCanny(processedImage);
                colorImgStream = new ByteArrayInputStream(processedImage);
                OutputStream outputStream = new FileOutputStream(PROJECT_HOME + "/src/api/resources/Java/" +
                        responseText.replace(',', Character.MIN_VALUE) + ".png");
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = colorImgStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // set download SUCCES message to return
                System.out.println("downloaded processed image successfully at " + PROJECT_HOME + "/src/api/resources/Java/" +
                        responseText.replace(',', '_') + ".png");
                // release resources, if any
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.close();
            client.close();
        }
    }
}
