package openiss.ws.rest;

import openiss.Kinect;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.image.BufferedImage;
import org.openkinect.freenect.*;

@Path("/openiss")
public class OpenISSRestService {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        Kinect kinect = new Kinect();
        kinect.initVideo();
//        BufferedImage someimage = kinect.getVideoImage();
        return "Got it!";
    }
}
