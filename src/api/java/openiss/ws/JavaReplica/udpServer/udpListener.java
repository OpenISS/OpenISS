package openiss.ws.JavaReplica.udpServer;

import openiss.ws.rest.OpenISSRestService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class udpListener {
    public static void main(String[] args) {
        try (DatagramSocket aSocket = new DatagramSocket(8081) {
            while (true)
            {// non-terminating loop as the server is always in listening mode.
                byte[] buffer = new byte[1000];// to stored the received data from the client.
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                // Server waits for the request to come
                aSocket.receive(request);// request received
                String serialReq = new String(request.getData()).trim();
            }
        }
    }
}
