package api.java.openiss.ws.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Service Implementation
 *
 * @author zephi
 */
@WebService(endpointInterface = "api.java.openiss.ws.soap.XMLReader")
public class XMLReaderImpl implements XMLReader {

    @Override
    public String fetchXML(String uri, String reqType) {
        URL url;
        try {
            if (!uri.isEmpty() && !uri.equals("")) {
                url = new URL(uri);
            } else if ("RSS".equals(reqType)) {
                url = new URL("http://www.ledevoir.com/rss/edition_complete.xml");
            } else if ("NN".equals(reqType)) {
                url = new URL("https://raw.githubusercontent.com/smokhov/atsm/master/examples/ws/XML/TestNN/samples/test1.xml");
            } else if ("MARFCAT-IN".equals(reqType)) {
                url = new URL("http://users.encs.concordia.ca/~s487_4/project/marfcat-in.xml");
            } else if ("MARFCAT-OUT".equals(reqType)) {
                url = new URL("http://users.encs.concordia.ca/~s487_4/project/marfcat-out.xml");
            } else if ("WSDL".equals(reqType)) {
                url = new URL("https://raw.githubusercontent.com/smokhov/atsm/master/examples/ws/soap/faultmessage/faultSample.wsdl");
            } else {
                return "Error encountered";
            }

            HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
            myConnection.setRequestMethod("GET");
            myConnection.setRequestProperty("Accept", "application/xml");
            InputStream temp = myConnection.getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length;
            while ((length = temp.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString("UTF-8");

        } catch (MalformedURLException ex) {
            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, e);
        }
        return "Exception encountered.";
    }

    @Override
    public void doCanny(String filename) {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            Mat color = Imgcodecs.imread(filename);

            Mat gray = new Mat();
            Mat draw = new Mat();
            Mat wide = new Mat();

            Imgproc.cvtColor(color, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.Canny(gray, wide, 50, 150, 3, false);
            wide.convertTo(draw, CvType.CV_8U);

            if (Imgcodecs.imwrite(filename, draw)) {
                System.out.println("edge is detected .......");
            }
        } catch (Exception e) {

        }
    }
}
