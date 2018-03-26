package openiss.ws.soap.endpoint;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.opencv.core.*;

public class Test2 {

    static String PROJECT_HOME = System.getProperty("user.dir");


    static {
        String opencvpath = PROJECT_HOME + "/lib/java/";
        System.out.println(System.getProperty("java.library.path"));
        System.out.println(Core.NATIVE_LIBRARY_NAME);
//
//
//
//        NativeLibrary.addSearchPath("opencv_java341", opencvpath);
//        NativeLibrary instance;
//        instance = NativeLibrary.getInstance("opencv_java341");
//        Native.register(instance);
//        System.load(opencvpath + "libopencv_java341.dylib");
//        System.load("/usr/local/Cellar/opencv/3.4.1_2/share/OpenCV/java/libopencv_java341.dylib");

    }

    public static void main(String[] args) {

//        System.out.println(Core.NATIVE_LIBRARY_NAME + ".dylib");
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        System.load("/usr/local/Cellar/opencv/3.4.1_2/lib/");

        System.loadLibrary("opencv_java341");
        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());

    }
}
