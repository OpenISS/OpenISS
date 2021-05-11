package openiss.ws.soap.endpoint;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import openiss.utils.OpenISSImageDriver;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.file.Files;

public class Test2 {

    static String PROJECT_HOME = System.getProperty("user.dir");
    private ClassLoader classLoader = getClass().getClassLoader();


    static {
        String PROJECT_HOME = System.getProperty("user.dir");
        int arch = Integer.parseInt(System.getProperty("sun.arch.data.model"));
        String osName = System.getProperty("os.name").toLowerCase();

        if(osName.indexOf("win") >= 0) {
            System.out.println(arch + " windows");
            System.load(PROJECT_HOME+"\\lib\\opencv\\win\\x64\\opencv_java341.dll");
        }
        else if(osName.indexOf("mac") >= 0){
            System.out.println("Loading Native library" + PROJECT_HOME+"/lib/opencv/mac/libopencv_java341.dylib");
            System.load(PROJECT_HOME+"/lib/opencv/mac/libopencv_java341.dylib");
        }

    }

    public static void main(String[] args) {

//        System.out.println(Core.NATIVE_LIBRARY_NAME + ".dylib");
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        System.load("/usr/local/Cellar/opencv/3.4.1_2/lib/");

        String inputFile = OpenISSConfig.OUTPUT_FILE_DIR + "bsd.jpg";
        String outputFile = OpenISSConfig.OUTPUT_FILE_DIR + "bsdCanny2.jpg";


        try {
            byte[] imageInBytes = Files.readAllBytes(new File(inputFile).toPath());
            Mat color = Imgcodecs.imdecode(new MatOfByte(imageInBytes), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);

//        Mat color = Imgcodecs.imread(inputFile);

            Mat gray = new Mat();
            Mat draw = new Mat();
            Mat wide = new Mat();

            Imgproc.cvtColor(color, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.Canny(gray, wide, 50, 150, 3, false);
            wide.convertTo(draw, CvType.CV_8U);

            if (Imgcodecs.imwrite(outputFile, draw)) {
                System.out.println("edge is writen to ......." +outputFile);
            }

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }



//        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
//        System.out.println("mat = " + mat.dump());

    }
}
