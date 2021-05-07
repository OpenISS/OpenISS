package openiss.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import openiss.ws.soap.service.OpenISSSOAPServiceImpl;

public class Utils {

    // These functions come from: http://graphics.stanford.edu/~mdfisher/Kinect.html
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

    static public BufferedImage processPGMImage(int width, int height, ShortBuffer data) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int red, green, blue, pixel;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int offset = x + y * width;
                short depth = data.get(offset);
                image.setRGB(x, y, depth2rgb(depth));
            }
        }

        return image;
    }

    static public BufferedImage processPGMImage(int width, int height, byte[] data) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int red, green, blue, pixel;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int offset = x + y * width;
//                short depth = data.get(offset);
                short depth = Utils.bytesToShort(data);
                image.setRGB(x, y, depth2rgb(depth));
            }
        }

        return image;
    }

    static int depth2rgb(short depth) {
        int r, g, b;

        float v = depth / 2047f;
        v = (float) Math.pow(v, 3) * 6;
        v = v * 6 * 256;

        int pval = Math.round(v);
        int lb = pval & 0xff;
        switch (pval >> 8) {
            case 0:
                b = 255;
                g = 255 - lb;
                r = 255 - lb;
                break;
            case 1:
                b = 255;
                g = lb;
                r = 0;
                break;
            case 2:
                b = 255 - lb;
                g = 255;
                r = 0;
                break;
            case 3:
                b = 0;
                g = 255;
                r = lb;
                break;
            case 4:
                b = 0;
                g = 255 - lb;
                r = 255;
                break;
            case 5:
                b = 0;
                g = 0;
                r = 255 - lb;
                break;
            default:
                r = 0;
                g = 0;
                b = 0;
                break;
        }

        int pixel = (0xFF) << 24
                    | (b & 0xFF) << 16
                    | (g & 0xFF) << 8
                    | (r & 0xFF) << 0;

        return pixel;
    }

    static int depth2intensity(short depth) {
        int d = Math.round((1 - (depth / 2047f)) * 255f);
        int pixel = (0xFF) << 24
                    | (d & 0xFF) << 16
                    | (d & 0xFF) << 8
                    | (d & 0xFF) << 0;

        return pixel;
    }

    public static short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public static float rawDepthToMeters(int depthValue) {
        if (depthValue < 2047) {
            return (float) (1.0 / ((double) (depthValue) * -0.0030711016 + 3.3309495161));
        }
        return 0.0f;
    }
}