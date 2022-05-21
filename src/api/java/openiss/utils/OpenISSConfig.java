package openiss.utils;

public class OpenISSConfig {
    public enum SensorType {
        FREENECT, // Freenect library
        FREENECT2, // Freenect2 library
        FAKENECT, // Requires FAKENECT_PATH with recorded session
        STATIC_SENSOR // Uses two static images - color_example.jpg and depth_example.jpg
    }
    public static SensorType SENSOR_TYPE = SensorType.STATIC_SENSOR;
    public static boolean USE_OPENCV = true; // You should disable this if you cannot install OPENCV
    public static final String OUTPUT_FILE_DIR = "/Users/kosta/Desktop/";
}
