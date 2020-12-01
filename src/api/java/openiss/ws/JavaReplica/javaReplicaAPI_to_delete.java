package openiss.ws.JavaReplica;

import openiss.utils.OpenISSConfig;
import openiss.utils.OpenISSImageDriver;
import openiss.utils.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

@Path("/openissJava")
public class javaReplicaAPI_to_delete {
    static private final String replicaID = "java";
    // group member p implementation from slide 16 of group communication lecture having fixed group g =1
    // on initialisation received = 0
    int received = 0;
    boolean isMainReplica = false;
    // multcast message m to group b
    static void castSequencer(String m, String forClient){
        int sequencerPort = 7002;
        util.logInterServerUnicast(replicaID, "sequencerUDP", forClient, "multicast_TO_Message", m);
    }
//    static void multicast_TO_Message(String m, String forClient) {
//        int sequencerPort = 7002;
//        int pythonPort = 8002;
//        int nodejsPort = 9002;
//
//        HashMap<Integer, String> toMulticastTargets = new HashMap<>();
//        toMulticastTargets.put(sequencerPort, "sequencer");
//        toMulticastTargets.put(pythonPort, "pyhtonUDP");
//        toMulticastTargets.put(nodejsPort, "nodejsUDP");
//        for (int port : toMulticastTargets.keySet()) {
//            String destinationUDP = toMulticastTargets.get(port);
//            util.logInterServerUnicast(replicaID, destinationUDP, forClient, "multicast_TO_Message", m);
//            requestReplyUDP(port, replicaID, destinationUDP, forClient, "multicast_TO_Message", m);
//        }
//    }


    static String mixFlag = "default";
    static boolean cannyFlag = false;
    static boolean contourFlag = false;
    static OpenISSImageDriver driver;

    static {
        driver = new OpenISSImageDriver();
        String PROJECT_HOME = System.getProperty("user.dir");
        int arch = Integer.parseInt(System.getProperty("sun.arch.data.model"));
        String osName = System.getProperty("os.name").toLowerCase();

        if (OpenISSConfig.USE_OPENCV) {
            if(osName.indexOf("win") >= 0) {
                System.out.println(arch + " windows");
                System.load(PROJECT_HOME+"\\lib\\opencv\\win\\x64\\opencv_java341.dll");
            }
            else if(osName.indexOf("mac") >= 0){
                System.out.println("Loading Native library" + PROJECT_HOME+"/lib/opencv/mac/libopencv_java3412.dylib");
                System.load(PROJECT_HOME+"/lib/opencv/mac/libopencv_java3412.dylib");
            }
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

        public static void logInterServerUnicast(String fromServer, String toServer, String forClient, String method,
                                                 String... params) {
            System.out.println("from " + fromServer + " to server " + toServer + " UNICAST for " + forClient +
                    ": " + method + Arrays.toString(params));
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
        javaReplicaAPI_to_delete.cannyFlag = true;
        return "Canny set to true";
    }

    @POST
    @Path("unsetCanny")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public static String unsetCanny() {
        javaReplicaAPI_to_delete.cannyFlag = false;
        return "Canny set to false";
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

        return processedImage;

    }

    public static String[] requestReplyUDP(Integer storePort, String... clientRequest) {
        try (DatagramSocket sendUDP_store = new DatagramSocket()) {
            if (storePort == -1) return new String[]{"Wrong store address"};
            //reference of the original socket
            String serialRequest = String.join(":", clientRequest);
            byte[] message = serialRequest.getBytes(); //message to be passed is stored in byte array
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, serialRequest.length(), aHost, storePort);
            try {
                sendUDP_store.send(request);//request sent out
            } catch (Exception e) {
                return new String[]{"Sorry UDP server momentarly non-responsive"};
            }

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
}