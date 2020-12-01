package openiss.ws.JavaReplica;

import openiss.ws.JavaReplica.UDP.udpListenerClass;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicBoolean;

// need to refactor
public class javaReplica { // receving client request
    int multicastPort = 2001;



    public static void main(String[] args) {

        // obtain server stub
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/");
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

        // no need to lauch UDP server; multicasts incoming from replica manager
        /*AtomicBoolean UDPnotLaunched = new AtomicBoolean(true);
        Runnable UDPstart = () -> {
            UDPnotLaunched.set(false);
            udpListenerClass.main();
        };

        Thread UDPlauncher = new Thread(UDPstart);
        UDPlauncher.start();
        while (UDPnotLaunched.get()) {
            ;*//*do nothing*//*
        }*/

        // listen to the multicast
        int multicastPort = 7001;
        MulticastSocket socket = null;
        InetAddress group = null;
        try {
            socket = new MulticastSocket(multicastPort);
            group = InetAddress.getByName("localhost");
            socket.joinGroup(group);

            DatagramPacket packet;
            while (true) {
                byte[] buf = new byte[1256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                //TODO unmarshall the packet; put in queue
                //process in API according to instructions
                Response response;
                try {
                    response = target.path("openiss/setCanny")
                            .request(MediaType.TEXT_PLAIN)
                            .post(Entity.text("sending"));
                } catch (Exception e) {
                   response = Response.status(status).build();
                }
                String responseText = response.getStatus() == 666 ?
                        response.getStatusInfo().getReasonPhrase() :
                        response.readEntity(String.class);
                System.out.println(responseText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
