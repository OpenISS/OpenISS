package openiss.ws.JavaReplica.udpServer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class udpListener {
    //not required for UDP listener to delete
    private static Client client = ClientBuilder.newClient();

    public static Response post(){
        WebTarget target = client.target("http://localhost:8080/rest/");
        Response response =  target.path("openiss/setCanny")
                .request(MediaType.TEXT_PLAIN)
                .post(Entity.text("sending"));
        return response;
    }

    public static void main() {
        Integer storePort = 8081;
        try (DatagramSocket aSocket = new DatagramSocket(storePort)) {
            while (true) {// non-terminating loop as the server is always in listening mode.
                byte[] buffer = new byte[1000];// to stored the received data from the client.
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);

                // Server waits for the request to come
                aSocket.receive(request);// request received
                String serialReq = new String(request.getData()).trim();
                String[] Req = serialReq.split(":");
                String fromServer, toServer, forClient, method;
                fromServer = Req[0];
                toServer = Req[1];
                forClient = Req[2];
                method = Req[3];
                switch (method) {
                    case "setCanny":
                        Response setCannyResponse = post();
                        System.out.println(setCannyResponse);
                        aSocket.send(replyUDPpacket(request, setCannyResponse.toString()));
                        break;
                    default:
                        break;
                }
            }
        } catch (SocketException e) {
            System.out.println(String.join(",","null", "null", "null", "SocketException", e.getMessage()));
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(String.join(",","null", "null", "null", "IOException", e.getMessage()));
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(String.join(",","null", "null", "null", "Exception", e.getMessage()));
            e.printStackTrace();
        }
    }
    public static DatagramPacket replyUDPpacket(DatagramPacket request, String... UDPresponse){
        byte[] replyBuff;
        String serialResp = String.join(":", UDPresponse);
        replyBuff = serialResp.getBytes();
        return new DatagramPacket(replyBuff, serialResp.length(), request.getAddress(), request.getPort());
    }
}
