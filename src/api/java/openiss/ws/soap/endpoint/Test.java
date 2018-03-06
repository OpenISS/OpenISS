package openiss.ws.soap.endpoint;

import org.openkinect.freenect.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
//import java.nio.IntBuffer;
//import java.nio.charset.Charset;
import java.io.IOException;
//import java.io.FileOutputStream;
//import java.nio.channels.FileChannel;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;


public class Test{
    public static void main(String[] args) {
        final int width = 640, height = 480;
        final BufferedImage color = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        final BufferedImage depth = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        FloatBuffer rawDepthToWorldBuffer;
        Context context = Freenect.createContext();

        if (context.numDevices() > 0) {
            System.out.println("Connected to device");
            Device device = context.openDevice(0);
            final Object lock = new Object();
            final long start = System.nanoTime();
            device.setVideoFormat(VideoFormat.RGB);
            System.out.println("Set video format");

            //DEPTH
            device.startDepth(new DepthHandler() {
                int frameCount = 0;
                ShortBuffer rawDepthBuffer;
                @Override
                public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
                    System.out.println("showing frame" + frameCount);
                    frameCount++;
                    if (frameCount == 4) {
                        System.out.println("frame: " + frame);
                        synchronized (lock) {
                            lock.notify();
                            System.out.println("frame: " + frame);
                            try {
                                byte[] temp = new byte[frame.remaining()];
                                frame.get(temp);
                                ImageIO.write(processPPMImage(640, 480, temp), "jpg", new File("kinect.color.jpg"));
                            }
                            catch (IOException ie){
                                System.out.println("Got IOException");
                                System.exit(1);
                            }

                            System.out.format("Got %d video frames in %4.2fs%n", frameCount,
                                    (((double) System.nanoTime() - start) / 1000000000));
                        }
                    }
                }
            });

            //VIDEO
            device.startVideo(new VideoHandler() {
                int frameCount = 0;
                ShortBuffer rawDepthBuffer;

                @Override
                public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
                    System.out.println("showing frame" + frameCount);
                    frameCount++;
                    if (frameCount == 4) {
                        System.out.println("frame: " + frame);
                        synchronized (lock) {
                            lock.notify();
                            System.out.println("frame: " + frame);
                            try {

                                byte[] temp = new byte[frame.remaining()];
                                frame.get(temp);
                                ImageIO.write(processPPMImage(640, 480, temp), "jpg", new File("kinect.color.jpg"));
                            }
                            catch (IOException ie){
                                System.out.println("Got IOException");
                                System.exit(1);
                            }

                            System.out.format("Got %d video frames in %4.2fs%n", frameCount,
                                    (((double) System.nanoTime() - start) / 1000000000));
                        }
                    }
                }
            });
        } else {
            System.err.println("WARNING: No kinect detected, hardware tests will be implicitly passed.");
        }
    }

    static public BufferedImage processPPMImage(int width, int height, byte[] data) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int red, green, blue, k = 0, pixel;
        for (int y = 0; y < height; y++) {
            for (int x = 0; (x < width) && ((k + 3) < data.length); x++) {
                red = data[k++] & 0xFF;
                green = data[k++] & 0xFF;
                blue = data[k++] & 0xFF;
                pixel = 0xFF000000 + (red << 16) + (green << 8) + blue;
                image.setRGB(x, y, pixel);
            }
        }
        return image;
    }



}



