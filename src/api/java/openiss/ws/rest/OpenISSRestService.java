package openiss.ws.rest;

import openiss.Kinect;
import openiss.utils.PATCH;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.MediaType;
import java.awt.datatransfer.MimeTypeParseException;
import java.io.File;

@Path("/openiss")
public class OpenISSRestService {


    static boolean mixFlag = false;
    static boolean cannyFlag = false;
    static boolean contourFlag = false;

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
    @Path("/{type}")
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

    @PATCH
    @Path("/mix")
    public Response enableMix() {
        mixFlag = true;
        System.out.println("Mix enabled");
        return Response.accepted().build();
    }


    @DELETE
    @Path("/mix")
    public Response disableMix() {
        mixFlag = false;
        return Response.accepted().build();
    }

    @PATCH
    @Path("/opencv/{type}")
    public Response enableOpenCV(@PathParam(value = "type") String type) {

        // validity checks
        if (!type.equals("canny") && !type.equals("contour")) {
            return Response.noContent().build();
        }

        if (type.equals("canny")) {
            cannyFlag = true;
        } else if(type.equals("contour")) {
            contourFlag = true;
        }

        return Response.accepted().build();
    }


    @DELETE
    @Path("/opencv/{type}")
    public Response disableOpenCV(@PathParam(value = "type") String type) {

        // validity checks
        if (!type.equals("canny") && !type.equals("contour")) {
            return Response.noContent().build();
        }

        if (type.equals("canny")) {
            cannyFlag = false;
        } else if(type.equals("contour")) {
            contourFlag = false;
        }
        return Response.accepted().build();
    }



}
