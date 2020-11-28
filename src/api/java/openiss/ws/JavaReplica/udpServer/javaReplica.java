package openiss.ws.JavaReplica.udpServer;

import openiss.ws.rest.OpenISSRestService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicBoolean;

public class javaReplica {
    private Client client;

    public javaReplica(){
        client = ClientBuilder.newClient();
    }

    public Response post(){
        WebTarget target = client.target("http://localhost:8080/rest/openiss/");
        return target.path("setCanny")
                .request(MediaType.TEXT_PLAIN)
                .post(Entity.entity(new OpenISSRestService(), MediaType.TEXT_PLAIN));
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
    }
}
