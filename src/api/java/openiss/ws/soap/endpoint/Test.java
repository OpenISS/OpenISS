package api.java.openiss.ws.soap.endpoint;

import org.openkinect.freenect.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
//import org.openkinect.Context;
//import org.openkinect.Device;
//import org.openkinect.Acceleration;
//import org.openkinect.Image;
//import org.openkinect.LEDStatus;
public class Test {
    //  static Context ctx;
//  static Device dev;
    public static void main(String[] args) {
        final int width = 640, height = 480;
        final BufferedImage color = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        final BufferedImage depth = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        FloatBuffer rawDepthToWorldBuffer;
//        Context context = Context.getContext();
        Context context = Freenect.createContext();
        //ctx.setLogHandler(new Jdk14LogHandler());
        //ctx.setLogLevel(LogLevel.SPEW);
        if (context.numDevices() > 0) {
//          dev = ctx.openDevice(0);
            System.out.println("Connected to device");
            Device device = context.openDevice(0);
            final Object lock = new Object();
            final long start = System.nanoTime();
            device.setVideoFormat(VideoFormat.RGB);
            System.out.println("Set video format");
            device.startVideo(new VideoHandler() {
                int frameCount = 0;
                ShortBuffer rawDepthBuffer;

                @Override
                public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
                    System.out.println("showing frame" + frameCount);
                    frameCount++;
                    if (frameCount == 4) {
                        System.out.println("frame: " + frame);
//                        System.out.println("mode: " + mode.getFrameSize());
                        synchronized (lock) {
                            lock.notify();
//                            rawDepthBuffer = frame.asShortBuffer();
//                            System.out.println(rawDepthBuffer);
                            System.out.println("frame: " + frame);
//                            System.out.println("mode: " + mode.getVideoFormat());
                            try {
//                                String v = new String(frame.array(), "UTF-8");
//                                System.out.println(v);
                                File file = new File("filename");
                                FileChannel wChannel = new FileOutputStream(file, false).getChannel();
                                wChannel.write(frame);
                                wChannel.close();
//                                device.color(null);
//                                ImageIO.write(frame, "jpg", new File("kinect.color.jpg"));
//                                byte[] b = new byte[frame.remaining()];
//                                frame.get(b, 0, b.length);
//
//                                InputStream in = new ByteArrayInputStream(b);
//                                BufferedImage bImageFromConvert = ImageIO.read(in);
//
//                                ImageIO.write(bImageFromConvert, "jpg", new File(
//                                        "new-darksouls.jpg"));
                            }
                            catch (IOException ie){
                                System.out.println("Got IOException");
                                System.exit(1);
                            }
//                            String converted = new String(frame.array(), "UTF-8");
//                            System.out.println(converted);
                            System.out.format("Got %d video frames in %4.2fs%n", frameCount,
                                    (((double) System.nanoTime() - start) / 1000000000));
//                            String v = new String( frame, Charset.forName("UTF-8") );
//                            System.out.println(v);
//                            ImageIO.write(frame, "jpg", new File(home + File.separator + "kinect.color.jpg"));
                        }
                    }
                }
            });
        } else {
            System.err.println("WARNING: No kinects detected, hardware tests will be implicitly passed.");
        }
    }
}