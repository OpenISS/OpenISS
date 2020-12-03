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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// need to refactor
public class javaReplica { // receving client request
    static final protected String PROJECT_HOME = System.getProperty("user.dir");

    public static void main(String[] args) {
        Map<Integer, String> holdBack = Collections.synchronizedMap(new HashMap<>());
        Map<Integer, String> processingQ = Collections.synchronizedMap(new HashMap<>());
        AtomicInteger received = new AtomicInteger(0);
        AtomicInteger awaitsProcessing = new AtomicInteger(0);
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
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            socket = new MulticastSocket(multicastPort);

            // Workaround for MacOS
            if(osName.indexOf("mac") >= 0){
                socket.setNetworkInterface(NetworkInterface.getByName("en0"));
            }
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
                if(!transformationOperation.equals("canny") && !transformationOperation.equals("contour")) continue;
                System.out.println(frameNumber + " " + transformationOperation + " received :_: current order is " + (received.get() + 1));
                Integer frNum = null;
                try {
                    frNum = Integer.parseInt(frameNumber);
                } catch (Exception e) {
                    System.out.println("Wrongly formatted request; first element not integer");
                    continue;
                }
		if (!transformationOperation.equals("processing")
                if (frNum == received.get() + 1) {
                    // kaaoshek like algorithim no need to keep the message backup asssuming local network reliable
                    sendToProcessingSynchronized(frNum, transformationOperation, processingQ, received);
                } else if (frNum > received.get() + 1) {
                    holdBack.put(frNum, transformationOperation); // no need to process this request yet
                    System.out.println(frNum + " " + transformationOperation + " placed in hold back queue");
                    // check the holdback queue to see if the correct request came in
                    Integer correctFrame = received.get() + 1;
                    String operationCorrect = holdBack.get(correctFrame);
                    if (operationCorrect != null) {
                        sendToProcessingSynchronized(correctFrame, operationCorrect, processingQ, received);
                        holdBack.remove(correctFrame); // release resources
                    }
                } // if frameNumber <= processed correctly ignores redundant operation that processed already
                System.out.print("testing");
                // empty processing queue
                while (processingQ.size() > 0) {
                    frNum = awaitsProcessing.get() + 1;
                    transformationOperation = processingQ.get(frNum);
                    System.out.println("Processing " + frNum + " " + transformationOperation);
                    //get color from API according
                    try {
                        response = target.path("openiss/getStaticFrame/" + frNum)
                                .request().get();
                    } catch (Exception e) {
                        response = Response.status(status).build();
                    }
                    if (response.getStatus() == 666) {
                        String responseText = response.getStatusInfo().getReasonPhrase();
                        System.out.println(responseText);
                        response.close();
                        break;
                    }
                    byte[] processedImgByteArray = toByteArray(response.readEntity(InputStream.class));
                    OpenISSImageDriver driver = new OpenISSImageDriver();
                    int arch = Integer.parseInt(System.getProperty("sun.arch.data.model"));

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
                    String imgPath = PROJECT_HOME + "/src/api/resources/Java/" + "f" + frNum + ".jpg";
                    File imgFile = new File(imgPath);
                    ImageIO.write(processedImgBuff, "jpg", imgFile);
                    // set download SUCCES message to return
                    System.out.println("downloaded processed image successfully at " + imgPath);
                    // replies to the sequencer using same adress same host
                    replyUDPpacket(packet, String.valueOf(frNum), "processed", "2");
                    processingQ.remove(frNum);
                    awaitsProcessing.incrementAndGet();
                    Integer nextFrame = awaitsProcessing.get() + 1;
                    String nextOperation = holdBack.get(nextFrame);
                    if(nextOperation != null)
                        sendToProcessingSynchronized(nextFrame, nextOperation, processingQ, received);
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        } finally {
            response.close();
            client.close();
        }

    }

    public static synchronized void sendToProcessingSynchronized(Integer frame, String command,
                                                                 Map<Integer, String> processingQ, AtomicInteger received) {
        System.out.println("sending " + frame + " " + command + " to processing queue");
        processingQ.put(frame, command);
        received.incrementAndGet();
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

    public static DatagramPacket replyUDPpacket(DatagramPacket request, String... UDPresponse) {
        byte[] replyBuff;
        String serialResp = String.join(",", UDPresponse);
        replyBuff = serialResp.getBytes();
        return new DatagramPacket(replyBuff, serialResp.length(), request.getAddress(), request.getPort());
    }
}
