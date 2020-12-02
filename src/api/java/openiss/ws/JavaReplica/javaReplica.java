package openiss.ws.JavaReplica;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
        for(String unsetFlags : new String[]{"unsetCanny", "unsetContour"}){
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
                String frame = requestList[0];
                String transformationOperation = requestList[1];
                System.out.println(frame + " " + transformationOperation);
                //process in API according to instructions
                Response response;
                try {
                    response = target.path("openiss/color")
                            .request().get();
                } catch (Exception e) {
                   response = Response.status(status).build();
                }
                String responseText = response.getStatusInfo().getReasonPhrase();
                System.out.println(responseText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
