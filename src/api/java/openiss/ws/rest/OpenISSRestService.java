package openiss.ws.rest;

import openiss.Kinect;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.MediaType;
import java.awt.datatransfer.MimeTypeParseException;
import java.io.File;

@Path("/openiss")
public class OpenISSRestService {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
//        Kinect kinect = new Kinect();
//        kinect.initVideo();
//        BufferedImage someimage = kinect.getVideoImage();
        return "Hello World from Jersey API!";
    }

    private static String colorFileName = "src/api/java/openiss/ws/soap/service/color_example.jpg";
    private static String depthFileName = "src/api/java/openiss/ws/soap/service/depth_example.jpg";

    @GET
    @Path("getImage/{type}")
    @Produces("image/*")
    public Response getImage(@PathParam(value = "type") String type) {

        File src;

        // validity checks
        if (!type.equals("color") && !type.equals("depth")) {
            return Response.noContent().build();
        }

        if(type.equals("color")) {
            src = new File(colorFileName);
        } else {
            src = new File(depthFileName);
        }

        String mt = new MimetypesFileTypeMap().getContentType(src);
        ResponseBuilder response = Response.ok(src, mt);
        return response.build();

    }


}
