package openiss.utils;

public class OpenISSConfig {
    public static boolean USE_FREENECT = false; // Freenect library
    public static boolean USE_FAKENECT = false; // Requires FAKENECT_PATH with recorded session
    public static boolean USE_STATIC_IMAGES = true; // Uses two static images - color_example.jpg and depth_example.jpg
    public static boolean USE_OPENCV = true; // You should disable this if you cannot install OPENCV
    public static final String OUTPUT_FILE_DIR = "/Users/kosta/Desktop/";
}
