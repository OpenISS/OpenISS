package openiss.ws.JavaReplica;

import openiss.utils.OpenISSConfig;
import openiss.utils.OpenISSImageDriver;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

// need to refactor
public class javaReplica { // receving client request
    static final protected String PROJECT_HOME = System.getProperty("user.dir");

    public static void main(String[] args) {

        // obtain server stub
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/rest/");
        client.property("accept", "image/*");
        // response if API is unresponsive
        Response.StatusType status = new Response.StatusType() {

            private int code = 666;
            private String msg = "API unresponsive";

            public Response.Status.Family getFamily() {
                return Response.Status.Family.INFORMATIONAL;
            }

            public String getReasonPhrase() {
                return msg;
            }

            public int getStatusCode() {
                return code;
            }

        };
        // unset canny and contour so the replica will process the image
        Response checkUnset;
        for (String unsetFlags : new String[]{"unsetCanny", "unsetContour"}) {
            try {
                checkUnset = target.path("openiss/" + unsetFlags)
                        .request(MediaType.TEXT_PLAIN)
                        .post(Entity.text("sending"));
            } catch (Exception e) {
                checkUnset = Response.status(status).build();
            }
            String responseText = checkUnset.getStatus() == 666 ?
                    checkUnset.getStatusInfo().getReasonPhrase() :
                    checkUnset.readEntity(String.class);
            System.out.println(responseText);
        }

        // listen to the multicast
        int multicastPort = 20000;
        MulticastSocket socket = null;
        InetAddress group = null;
        System.setProperty("java.net.preferIPv4Stack", "true");
        Response response = null;
        try {
            socket = new MulticastSocket(multicastPort);
            socket.setNetworkInterface(NetworkInterface.getByName("en0"));
            group = InetAddress.getByName("230.255.255.255");
            System.out.println("Joining Multicast Group");
            socket.joinGroup(group);

            DatagramPacket packet;
            while (true) {
                byte[] buf = new byte[1256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String serializedRequest = new String(packet.getData()).trim();
                String[] requestList = serializedRequest.split(",");
                String frameNumber = requestList[0];
                String transformationOperation = requestList[1];
                System.out.println(frameNumber + " " + transformationOperation);
                //get color from API according
                try {
                    response = target.path("openiss/color")
                            .request().get();
                } catch (Exception e) {
                    response = Response.status(status).build();
                }
                String responseText = response.getStatusInfo().getReasonPhrase();
                System.out.println(responseText);

                byte[] processedImgByteArray = toByteArray(response.readEntity(InputStream.class));
                OpenISSImageDriver driver = new OpenISSImageDriver();
                int arch = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                String osName = System.getProperty("os.name").toLowerCase();
                if (OpenISSConfig.USE_OPENCV) {
                    if (osName.indexOf("win") >= 0) {
                        /*System.out.println(arch + " windows");*/
                        System.load(PROJECT_HOME + "\\lib\\opencv\\win\\x64\\opencv_java341.dll");
                    } else if (osName.indexOf("mac") >= 0) {
                        /*System.out.println("Loading Native library" + PROJECT_HOME + "/lib/opencv/mac/libopencv_java3412.dylib");*/
                        System.load(PROJECT_HOME + "/lib/opencv/mac/libopencv_java3412.dylib");
                    }
                }

                // Process according to instructions
                if (transformationOperation.equals("canny"))
                    processedImgByteArray = driver.doCanny(processedImgByteArray);
                if (transformationOperation.equals("contour"))
                    processedImgByteArray = driver.contour(processedImgByteArray);
                InputStream processedImgInputStream = new ByteArrayInputStream(processedImgByteArray);
                BufferedImage processedImgBuff = ImageIO.read(processedImgInputStream);
                String imgPath = PROJECT_HOME + "/src/api/resources/Java/" + frameNumber + "_" + transformationOperation + ".jpg";
                File imgFile = new File(imgPath);
                ImageIO.write(processedImgBuff, "jpg", imgFile);
                // set download SUCCES message to return
                System.out.println("downloaded processed image successfully at " + imgPath);
                ScriptPython.imgPath = imgPath;
                System.out.println(ScriptPython.runScript());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.close();
            client.close();
        }
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 5];
        int len;

        // read bytes from the input stream and store them in buffer
        while ((len = in.read(buffer)) != -1) {
            // write bytes from the buffer into output stream
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }

    public static String[] requestReplyUDP(Integer udpPort, String... clientRequest) {
        try (DatagramSocket sendUDP_store = new DatagramSocket()) {
            if (udpPort == -1) return new String[]{"Wrong store address"};
            //reference of the original socket
            String serialRequest = String.join(",", clientRequest);
            byte[] message = serialRequest.getBytes(); //message to be passed is stored in byte array
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, serialRequest.length(), aHost, udpPort);
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

    public static class ScriptPython {
        static String imgPath;

        public static String runScript() {
            String line, lines = "";
            try {
                Process process;
                process = Runtime.getRuntime().exec(new String[]{PROJECT_HOME + "/src/api/java/openiss/ws/JavaReplica/checkSum.py", imgPath});
                InputStream stdout = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));
                try {
                    while ((line = reader.readLine()) != null) {
                        lines = lines + line;
                    }
                } catch (IOException e) {
                    System.out.println("Exception in reading output" + e.toString());
                }
            } catch (Exception e) {
                System.out.println("Exception Raised" + e.toString());
            }
            return lines;
        }
    }
}
