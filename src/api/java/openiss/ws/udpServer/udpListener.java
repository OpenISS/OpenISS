package openiss.ws.udpServer;

import openiss.ws.rest.OpenISSRestService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class udpListener {
    private Client client;

    public udpListener(){
        client = ClientBuilder.newClient();
    }

    public Response post(){
        WebTarget target = client.target("http://localhost:8080/rest/openiss/");
        Response response = target.path("setCanny")
                                  .request(MediaType.TEXT_PLAIN)
                                  .post(Entity.entity(new OpenISSRestService(), MediaType.TEXT_PLAIN));
        return response;
    }
}
