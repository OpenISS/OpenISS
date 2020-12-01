package openiss.ws.JavaReplica.UDP;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class javaReplicaUDPserver {
    static private final String replicaID = "javaUDP";
    private static WebTarget target;
    private static Client client;
    // response if API is unresponsive
    static Response.StatusType status = new Response.StatusType() {

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


    public static Response postSetCanny() {
        Response response;
        try {
            response = target.path("openiss/setCanny")
                    .request(MediaType.TEXT_PLAIN)
                    .post(Entity.text("sending"));
        } catch (Exception e) {
            return Response.status(status).build();
        }
        return response;
    }



    public static void main(String[] args) {
        // need holdback queue
        // need processing queue

        client = ClientBuilder.newClient();
        target = client.target("http://localhost:8080/rest/");

        //images temp
        AtomicBoolean UDPnotLaunched = new AtomicBoolean(true);
        Runnable UDPstart = () -> {
            UDPnotLaunched.set(false);
            udpListenerClass.main();
        };

        Thread UDPlauncher = new Thread(UDPstart);
        UDPlauncher.start();
        while (UDPnotLaunched.get()) {
            ;/*do nothing*/
        }

        //Command line menu
        Scanner scanner = new Scanner(System.in);
        String Choice;
        do {
            System.out.println("Testing UDP communication using the set unset Canny in the API");
            System.out.println("1 to set canny");
            System.out.println("2 to unset canny");
            Choice = scanner.nextLine();
            runChoice("noClientYet", Choice);
            continue;
        } while (!Choice.equals("3") && !Choice.equals("end"));
    }

    private static void runChoice(String clientID, String choice) {
        switch (choice) {
            case "1":
//                util.logInterServerReq(replicaID, "RestAPI", clientID, "setCanny");
//                String setCannyReply = requestReplyUDP(8081, replicaID, "RestAPI", clientID, "setCanny")[0];
//                util.logInterServerReply("RestAPI", replicaID, clientID, "setCanny", setCannyReply);
                util.logClientSideRequest("setCanny");
                String response = postSetCanny().readEntity(String.class);
                util.logReturnClientSideResponse(response, "NoClientYet");
        }
    }

    static class util {
        static void logClientSideRequest(String fct, String... req) {
            System.out.println("Client Side request: " + fct + Arrays.toString(req));
        }

        static String logReturnClientSideResponse(String resp, String clientId) {
            System.out.println("Response to " + clientId + ": " + resp);
            return resp;
        }

        static void logInterServerReq(String fromServer, String toServer, String forClient, String method,
                                      String... params) {
            System.out.println("from " + fromServer + " to server " + toServer + " request for " + forClient +
                    ": " + method + Arrays.toString(params));
        }

        static String[] logInterServerReply(String fromServer, String toServer, String forClient, String method,
                                            String... params) {
            System.out.println("from " + fromServer + " to server " + toServer + " reply for " + forClient +
                    ": " + method + Arrays.toString(params));
            return params;
        }

    }
}
