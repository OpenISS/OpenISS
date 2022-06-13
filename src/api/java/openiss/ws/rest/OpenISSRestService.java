package openiss.ws.rest;


import openiss.utils.OpenISSConfig;
import openiss.utils.PATCH;
import openiss.utils.legacy.OpenISSImageDriver;

import org.glassfish.jersey.media.multipart.*;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.io.*;
import java.nio.file.Files;

import java.awt.image.BufferedImage;
import java.net.ServerSocket;

@Path("/openiss")
public class OpenISSRestService {


    static String mixFlag = "default";
    static boolean cannyFlag = false;
    static boolean contourFlag = false;
    static OpenISSImageDriver driver;

    static {
        driver = new OpenISSImageDriver();
        String PROJECT_HOME = System.getProperty("user.dir");
        int arch = Integer.parseInt(System.getProperty("sun.arch.data.model"));
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        System.out.println(osArch + " OPENISS ARCH");
        System.out.println(osName + " OPENISS NAME");
        if (OpenISSConfig.USE_OPENCV) {
            if(osName.indexOf("win") >= 0) {
                System.out.println(arch + " windows");
                System.load(PROJECT_HOME+"\\lib\\opencv\\win\\x64\\opencv_java341.dll");
            }
            else if(osName.indexOf("mac") >= 0){
            	
                if(osArch.equals("aarch64") || osArch.equals("arm64") || osArch.equals("x86_64")){
                    System.out.println("Loading Native library" + PROJECT_HOME+"/lib/opencv/mac-aarch64/libopencv_java3416.dylib");
                    System.load(PROJECT_HOME+"/lib/opencv/mac-aarch64/libopencv_java3416.dylib");
                }
                else {
                    System.out.println("Loading Native library" + PROJECT_HOME+"/lib/opencv/mac/libopencv_java3412.dylib");
                    System.load(PROJECT_HOME+"/lib/opencv/mac/libopencv_java3412.dylib");
                }
            }
            else if(osName.indexOf("linux") >= 0) {
                System.out.println("Loading Native library" + PROJECT_HOME+ "/lib/opencv/linux/libopencv_java3413.so");
                System.load(PROJECT_HOME+"/lib/opencv/linux/libopencv_java3413.so");
            }
        }

    }

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @POST
    @Path("setCanny")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public static String setCanny() {
        OpenISSRestService.cannyFlag = true;
        return "Canny set to true";
    }

    @POST
    @Path("unsetCanny")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public static String unsetCanny() {
        OpenISSRestService.cannyFlag = false;
        return "Canny set to false";
    }

    @POST
    @Path("setContour")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public static String setContour() {
        OpenISSRestService.contourFlag = true;
        return "Contour set to true";
    }

    @POST
    @Path("unsetContour")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public static String unsetContour() {
        OpenISSRestService.contourFlag = false;
        return "Contour set to false";
    }

    @GET
    @Path("getUDPPort")
    @Produces(MediaType.TEXT_PLAIN)
        public static String getUDPPort() {
            try (ServerSocket socket = new ServerSocket(0)) {
                socket.setReuseAddress(true);
                int port = socket.getLocalPort();
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignore IOException on close()
                }
                return Integer.toString(port);
            } catch (IOException e) { e.printStackTrace(); }
            throw new IllegalStateException("Could not find a free TCP/IP port");
    }

    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Hello World from Jersey API!";
    }

    @GET
    @Path("/getStaticFrame/{frameId}")
    @Produces("image/*")
    public Response getStaticFrame(@PathParam(value = "frameId") String frameId) {
        ResponseBuilder response;
        byte[] image = new byte[0];

        image = driver.getStaticFrame("/FramesSimulation/f" + frameId + ".jpg");

        response = Response.ok(image, "image/jpeg");
        return response.build();
    }

    @GET
    @Path("/{type}")
    @Produces("image/*")
    public Response getImage(@PathParam(value = "type") String type) {

        ResponseBuilder response;
        byte[] image = new byte[0];
        // validity checks
        if (!type.equals("color") && !type.equals("depth")) {
            return Response.noContent().build();
        }

        if (type.equals("color")) {
            image = driver.getFrame("color");
        } else {
            image = driver.getFrame("depth");
        }
        response = Response.ok(pipelineImage(image, type), "image/jpeg");

        return response.build();
    }

    @PATCH
    @Path("/mix/{action}")
    @Produces("text/plain")
    /**
     * the GET images will be mixed with depth, rgb or canny
     */
    public String enableMix(@PathParam(value = "action") String action) {
        if (action.equals("depth") || action.equals("color") || action.equals("canny")) {
            mixFlag = action;
        }
        return getFlags();
    }


    @DELETE
    @Path("/mix")
    @Produces("text/plain")
    public String disableMix() {
        mixFlag = "default";
        return getFlags();
    }

    @PATCH
    @Path("/opencv/{type}")
    @Produces("text/plain")
    public String enableOpenCV(@PathParam(value = "type") String type) {

        // validity checks
        if (!OpenISSConfig.USE_OPENCV || (!type.equals("canny") && !type.equals("contour"))) {
            return "Service not supported";
        }

        if (type.equals("canny")) {
            cannyFlag = true;
        } else if (type.equals("contour")) {
            contourFlag = true;
        }
        return getFlags();
    }


    @DELETE
    @Path("/opencv/{type}")
    @Produces("text/plain")
    public String disableOpenCV(@PathParam(value = "type") String type) {

        // validity checksX
        if (!OpenISSConfig.USE_OPENCV || (!type.equals("canny") && !type.equals("contour"))) {
            return "Service not supported.";
        }
        if (type.equals("canny")) {
            cannyFlag = false;
        } else if (type.equals("contour")) {
            contourFlag = false;
        }
        return getFlags();
    }

    @GET
    @Path("/hsplit/{parts}/{part}")
    @Produces("image/*")
    public Response hSplitJPG(@PathParam(value = "parts") Integer parts, @PathParam(value = "part") Integer part) throws IOException {
        ResponseBuilder response;
        BufferedImage image;
        image = driver.horizontalJPGsplit("color", parts, part);
        response = Response.ok(image, "image/jpeg");
        return response.build();
    }




    @GET
    @Path("/reqmix")
    public Response requestMix(
            @QueryParam("addr") String addr, @QueryParam("port") String port) {
        byte[] image = driver.getFrame("depth");

//        String serverURL = "http://" + addr + ":" + port + "/rest/openiss/upload";
        String serverURL = "http://" + addr + "/rest/openiss/upload";

        System.out.println("Server URL: " + serverURL);

        String result = "http://" + addr + ":" + port + "/rest/openiss/mix/result";

        Client client = ClientBuilder.newBuilder().
                register(MultiPartFeature.class).build();
        WebTarget server = client.target(serverURL);

        MultiPart multiPart = new MultiPart();
        StreamDataBodyPart body = new StreamDataBodyPart("file", new ByteArrayInputStream(image));
        multiPart.bodyPart(body);

        Response response = server.request()
                .post(Entity.entity(multiPart, "multipart/form-data"));
        ResponseBuilder builder = Response.ok(response.getEntity(), "image/jpeg")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS");

        if (response.getStatus() == 200) {
            return builder.build();
        } else {
            return Response.noContent().build();
        }
    }

    boolean canMix = false;
    String mixImgName;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(FormDataMultiPart form) throws IOException {
        System.out.println("receive something ...");

        FormDataBodyPart filePart = form.getField("file");
        ContentDisposition headerOfFilePart =  filePart.getContentDisposition();
        InputStream fileInputStream = filePart.getValueAs(InputStream.class);
//        mixImgName = headerOfFilePart.getFileName();

        mixImgName = "wait_for_mix.jpg";
        writeToFile(fileInputStream, mixImgName);

        byte[] image = Files.readAllBytes(new File(mixImgName).toPath());
        ResponseBuilder response = Response.ok(pipelineImage(image, "usermix"), "image/jpeg");
        response.header("Content-Disposition", "inline")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS");
        return response.build();


//        // save the file to the server
//        writeToFile(fileInputStream, mixImgName);
//        String output = "File saved to server location using FormDataMultiPart : " + mixImgName;
//        return Response.status(200).entity(output).build();

    }

    // save uploaded file to new location
    private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
        try {
            OutputStream out;
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private String getFlags() {
        String flags = "Mix: " + String.valueOf(mixFlag) +
                "\nCanny: " + String.valueOf(cannyFlag) +
                "\nContour: " + String.valueOf(contourFlag);
        return flags;
    }

    private byte[] pipelineImage(byte[] image, String baseImage) {

        byte[] processedImage = image;

        if (mixFlag.equals("depth")) {
            //@TODO this needs to be fixed conditionally to work with static image and driver
            processedImage = driver.mixFrame(image, "depth", "+");
        } else if (mixFlag.equals("color")) {
            processedImage = driver.mixFrame(image, "color", "+");
        } else if (mixFlag.equals("canny")) {
            // todo: add docanny support
            // mix with do canny

            if(baseImage.equals("depth")) {
                processedImage = driver.mixFrame(driver.doCanny(image), "depth", "+");
            } else {
                processedImage = driver.mixFrame(driver.doCanny(image), "color", "+");
            }
        }


        if (cannyFlag) {
            System.out.println("Running driver.doCanny");
        	processedImage = driver.doCanny(processedImage);
        }

        if (contourFlag) {
            System.out.println("Running driver.contour");
        	processedImage = driver.contour(image);
        }

        if ("usermix".equals(baseImage)) {
            processedImage = driver.mixFrame(image, "color", "+");
        }

        return processedImage;
    }


}