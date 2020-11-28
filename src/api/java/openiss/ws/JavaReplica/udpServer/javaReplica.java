package openiss.ws.JavaReplica.udpServer;

//import openiss.ws.rest.OpenISSRestService;

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

public class javaReplica {
    private Client client;
    static private final String replicaID = "java";

    public javaReplica(){
        client = ClientBuilder.newClient();
    }

    public Response post(){
        WebTarget target = client.target("http://localhost:8080/rest/openiss/");
        return target.path("setCanny")
                .request(MediaType.TEXT_PLAIN)
                .post(Entity.entity(Response.class, MediaType.TEXT_PLAIN));
    }

    public static void main(String[] args) {
        // need holdback queue
        // need execution queue
        AtomicBoolean UDPnotLaunched = new AtomicBoolean(true);
        Runnable UDPstart = () -> {
            UDPnotLaunched.set(false);
            udpListener.main();
        };

        Thread UDPlauncher = new Thread(UDPstart);
        UDPlauncher.start();
        while (UDPnotLaunched.get()) { ;/*do nothing*/ }

        //Command line menu
        Scanner scanner = new Scanner(System.in);
        String Choice;
        do {
            System.out.println("Testing UDP communication using the set unset Canny in the API");
            System.out.println("1 to set canny");
            System.out.println("2 to unset canny");
            Choice = scanner.nextLine();
            runChoice("noClientYet", Choice);
        } while (!Choice.equals("3") && !Choice.equals("end"));
    }

    private static void runChoice(String clientID, String choice) {
        switch (choice){
            case "1":
                util.logInterServerReq(replicaID, "RestAPI", clientID,"setCanny");
                String setCannyReply = requestReplyUDP(8081, replicaID, "RestAPI", clientID,"setCanny")[0];
                util.logInterServerReply("RestAPI", replicaID, clientID, "setCanny", setCannyReply);
        }
    }
    public static String[] requestReplyUDP(Integer storePort, String... clientRequest) {
        try (DatagramSocket sendUDP_store = new DatagramSocket()) {
            if(storePort == -1 ) return new String[]{"Wrong store address"};
            //reference of the original socket
            String serialRequest = String.join(":", clientRequest);
            byte[] message = serialRequest.getBytes(); //message to be passed is stored in byte array
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, serialRequest.length(), aHost, storePort);
            sendUDP_store.send(request);//request sent out

            byte[] buffer = new byte[1000];//it will be populated by what receive method returns
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);//reply packet ready but not populated.
            //Client waits until the reply is received-----------------------------------------------------------------------
            sendUDP_store.receive(reply);//reply received and will populate reply packet now.
            String serialReply = new String(reply.getData()).trim();
            String[] replyMsg = serialReply.split(":");
            //print reply message after converting it to a string from bytes
            sendUDP_store.close();
            return replyMsg;
        } catch (SocketException e) {
            return new String[]{"Socket: " + e.getMessage()};
        } catch (IOException e) {
            e.printStackTrace();
            return new String[]{"IO: " + e.getMessage()};
        }
    }

    static class util {

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
