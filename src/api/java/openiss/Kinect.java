/**
 * Open Kinect for Processing
 * A Mac OS X Kinect implementation using open source drivers (libfreenect).
 * https://github.com/shiffman/OpenKinect-for-Processing
 *
 * Copyright 2015 Daniel Shiffman
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Daniel Shiffman
 * @modified    July 3, 2015
 * @version     0.3a (3)
 */


package openiss;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javassist.bytecode.ByteArray;
import openiss.utils.OpenISSConfig;
import openiss.ws.soap.service.OpenISSSOAPServiceImpl;
import org.openkinect.freenect.Context;
import org.openkinect.freenect.DepthFormat;
import org.openkinect.freenect.DepthHandler;
import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;
import org.openkinect.freenect.Freenect;
import org.openkinect.freenect.VideoFormat;
import org.openkinect.freenect.VideoHandler;

import javax.imageio.ImageIO;

public class Kinect {

	static byte[] color;
	static ShortBuffer depth;

	private static String colorFileName = "color_example.jpg";
	private  static String depthFileName = "depth_example.jpg";
	private static String colorFailFileName = "color_fail.jpg";
	private  static String depthFailFileName = "depth_fail.jpg";

	Method depthEventMethod;
	Method videoEventMethod;

	boolean irEnabled = false;
	boolean videoEnabled = false;
	
	boolean mirrorMode  = false;

	BufferedImage depthImage;
	BufferedImage videoImage;
	
	ShortBuffer rawDepthBuffer;
	FloatBuffer rawDepthToWorldBuffer;
	
	int [] 		rawDepth;
	float []  	rawDepthToWorld;

	Context context;
	Device device;

	public int width = 640;
	public int height = 480;

	boolean irMode = false;
	boolean colorDepthMode = false;
	
	int currentDeviceIndex = 0;

	boolean started = false;
	
	// We'll use a lookup table so that we don't have to repeat the math over and over
	float[] depthLookUp = new float[2048];

	//operating_system holds the name of the operating system
	private static String operating_system = System.getProperty("os.name").toLowerCase();

	static String FAKENECT_PATH = System.getenv("FAKENECT_PATH");

	private ClassLoader classLoader = getClass().getClassLoader();
	
	/**
	 * Kinect constructor, usually called in the setup() method in your sketch to
	 * initialize and start the library.
	 * 
	 */
	
	public Kinect() {


		// Skip windows until the driver is working
		if (operating_system.indexOf("win") >= 0) {
			System.err.println("Kinect is not currently supported on Windows Operating System.");
			return;
		}

        /**
         * Both Freenect and Fakenect are disabled
         */
        if (!OpenISSConfig.USE_FREENECT && !OpenISSConfig.USE_FAKENECT) {
            return;
        }

		depthImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		videoImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		
		rawDepth   = new int[width * height];
		
		rawDepthToWorld = new float[width * height * 3];
		rawDepthToWorldBuffer  = FloatBuffer.allocate(width * height * 3);

		// Lookup table for all possible depth values (0 - 2047)
		for (int i = 0; i < depthLookUp.length; i++) {
			depthLookUp[i] = rawDepthToMeters(i);
		}
		  
		context = Freenect.createContext();
		if(numDevices() < 1) {
			System.err.println("No Kinect devices found.");
		}
		else {
            System.out.println("Kinect device loaded");
        }
		//start(0);
	}
	
	/**
	 * Static method to only obtain the number of connected Kinect devices
	 * @return
	 */
	public static int countDevices(){
		Context tmpContext = Freenect.createContext();
		return tmpContext.numDevices();
	}

	/**
	 * Returns the number of Kinect devices detected
	 * 
	 * @return number of Kinect devices detected
	 */
	public int numDevices() {
		return context.numDevices();		
	}


	/**
	 * Set the active device
	 * 
	 * @param n index of which device to select
	 * startDevice -> activateDevice
	 */
	public void activateDevice(int n) {
		currentDeviceIndex = n;
	}
 
	// Called internally
	private void start() {
		started = true;
		device = context.openDevice(currentDeviceIndex);
	}

	/**
	 * Get the raw depth values from the Kinect.
	 * 
	 * @return the raw depth values (range: 0 - 2047) as int array
	 */
	public int[] getRawDepth() {
		return rawDepth;
	}
	
	public FloatBuffer getDephToWorldPositions(){
		
		rawDepthToWorldBuffer.put(rawDepthToWorld, 0, width * height * 3);
		rawDepthToWorldBuffer.rewind();
    	
		return rawDepthToWorldBuffer;
	}

	/**
	 * Stop getting depth from Kinect.
	 * 
	 */
	public void stopDepth() {
		device.stopDepth();
	}

	/**
	 * Stop getting RGB video from Kinect.
	 * 
	 */
	public void stopVideo() {
		device.stopVideo();
		videoEnabled = false;
	}


	/**
	 * Start getting depth from Kinect (available as raw array or mapped to image)
	 * 
	 */
	public void initDepth() {

		/**
		 * Clients who have configured Fakenect but do not support the library
		 * Currently only Windows
		 */
		if(OpenISSConfig.USE_FAKENECT && !Freenect.LIB_IS_LOADED ){
			try {
				useFileSystemDepth();
				return;
			}
			 catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}

		/**
		 * Both Freenect and Fakenect are disabled
		 */
		if (!OpenISSConfig.USE_FREENECT && !OpenISSConfig.USE_FAKENECT) {
			return;
		}

		if (!started) {
			start();
		}

		if (device != null) {
			device.setDepthFormat(DepthFormat.D11BIT);
			final Kinect ref = this;
			device.startDepth(new DepthHandler() {
				public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
					depth = frame.asShortBuffer();
				}
			});
		}
	}


	/**
	 * Start getting RGB video from Kinect.
	 * 
	 */
	public void initVideo() {

		/**
		 * Clients who have configured Fakenect but do not support the library
		 * Currently only Windows
		 */
		if(OpenISSConfig.USE_FAKENECT && !Freenect.LIB_IS_LOADED ){
			try {
				useFileSystemColor();
				return;
			}
			catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}

		/**
		 * Both Freenect and Fakenect are disabled
		 */
		if (!OpenISSConfig.USE_FREENECT && !OpenISSConfig.USE_FAKENECT) {
			return;
		}

		if (!started) {
			start();
		}
		if (device != null && !videoEnabled) {
			videoEnabled = true;
			final Kinect ref = this;
			if (irMode) {
				System.out.println("setting video format to IR_8bit");
				device.setVideoFormat(VideoFormat.IR_8BIT);
			} else {
				System.out.println("settinv video format to RGB");
				device.setVideoFormat(VideoFormat.RGB);
			}
			device.startVideo(new VideoHandler() {
				public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
					color = new byte[frame.remaining()];
					frame.get(color);
					frame.flip();
				}
			});
		}
	}

	/**
	 * Set the tilt angle of the Kinect.
	 * 
	 * @param deg the angle (in degrees, range 0-30).
	 */
	public void setTilt(float deg) {
		if (device != null) {
			device.setTiltAngle(deg);
		}
	}

	/**
	 * Get the tilt angle of the Kinect.
	 * 
	 * @return the angle
	 */
	public float getTilt() {
		if (device != null) {
			return (float) device.getTiltAngle();
		} else {
			return 0;
		}
	}
	
	/**
	 * Enable mirror mode for all frames
	 * @param mirror
	 */
	public void enableMirror(boolean mirror){
		mirrorMode = mirror;
	}

	/**
	 * Enable IR image (instead of RGB video)
	 * 
	 * @param b true to turn it on, false to turn it off
	 */
	public void enableIR(boolean b) throws InterruptedException {
		// If nothing has changed let's not do anything
		if (irMode == b) {
			return;
		}
		
		irMode = b;
		
		if (videoEnabled) {
			stopVideo();
		}
		
		
		if (irMode) {
			device.setVideoFormat(VideoFormat.IR_8BIT);
		} else {
			device.setVideoFormat(VideoFormat.RGB);
		}
		if  (!videoEnabled) {
			initVideo();
		}
		
		
	}

	/**
	 * Enable mapping depth values to color image (instead of grayscale)
	 * 
	 * @param b true to turn it on, false to turn it off
	 */	
	public void enableColorDepth(boolean b) {
		colorDepthMode = b;
	}

	/**
	 * Get the depth image (does not make a new object, use get() if you need a copy)
	 * 
	 * @return reference to depth image 
	 */	
	public BufferedImage getDepthImage(){
		byte[] imageInBytes;

		try {

			/**
			 * Clients who do not have Kinect and have not configured Fakenect
			 * @return depth_example.jpg from resources
			 */
			if (OpenISSConfig.USE_STATIC_IMAGES) {
				return  ImageIO.read(new File(classLoader.getResource(depthFileName).getFile()));
			}

			/**
			 * Clients who have configured Fakenect but do not support the library
			 * Currently only Windows
			 * @return current image from FAKENECT_PATH recorded session
			 */
			else if (!Freenect.LIB_IS_LOADED && OpenISSConfig.USE_FAKENECT) {
				imageInBytes = Files.readAllBytes(new File(getFileName("depth")).toPath());
				ByteBuffer buf = ByteBuffer.wrap(imageInBytes);
				return processPGMImage(640, 480, buf.asShortBuffer());
			}

			/**
			 * Clients who support the library and use Kinect or Fakenect
			 * @return current image from Kinect Live Stream or FAKENECT_PATH recorded session
			 */
			else if( Freenect.LIB_IS_LOADED && (OpenISSConfig.USE_FREENECT || OpenISSConfig.USE_FAKENECT)) {
				return processPGMImage(640, 480, depth);
			}

			/**
			 * Everything is false in your OpenISSConfig
			 * @return depth_fail.jpg from resources
			 */
			else {
				System.err.println("Falling back to static images as last resort since no Kinect libraries are loaded");
				return  ImageIO.read(new File(classLoader.getResource(depthFailFileName).getFile()));
			}
		}

		/**
		 * Image not found (404)
		 * @return empty image
		 */
		catch (Exception e){
			System.out.println(e.getMessage());
			return processPGMImage(640, 480, new byte[0]);
		}
	}
	
	/**
	 * Get the video image (does not make a new object, use get() if you need a copy)
	 * 
	 * @return reference to video image 
	 */		
	public BufferedImage getVideoImage() {
		byte[] imageInBytes = new byte[0];
		try {

			/**
			 * Clients who do not have Kinect and have not configured Fakenect
			 * @return color_example.jpg from resources
			 */
			if (OpenISSConfig.USE_STATIC_IMAGES) {
				return  ImageIO.read(new File(classLoader.getResource(colorFileName).getFile()));
			}

			/**
			 * Clients who have configured Fakenect but do not support the library. Currently only Windows
			 * @return current image from FAKENECT_PATH recorded session
			 */
			else if (!Freenect.LIB_IS_LOADED && OpenISSConfig.USE_FAKENECT ) {
				imageInBytes = Files.readAllBytes(new File(getFileName("color")).toPath());
				return processPPMImage(640, 480, imageInBytes);
			}

			/**
			 * Clients who support the library and use Kinect or Fakenect
			 * @return current image from Kinect Live Stream or FAKENECT_PATH recorded session
			 */
			else if( Freenect.LIB_IS_LOADED && (OpenISSConfig.USE_FREENECT || OpenISSConfig.USE_FAKENECT)) {
				return processPPMImage(640, 480, color);
			}

			/**
			 * Everything is false in your OpenISSConfig
			 * @return color_fail.jpg from resources
			 */
			else {
				System.err.println("Falling back to static images as last resort since no Kinect libraries are loaded");
				return  ImageIO.read(new File(classLoader.getResource(colorFailFileName).getFile()));
			}

		}

		/**
		 * Image not found (404)
		 * @return empty image
		 */
		catch (Exception e){
			System.out.println(e.getMessage());
			return processPPMImage(640, 480, new byte[0]);

		}
	}

	/**
	 * Get a static image (does not make a new object, use get() if you need a copy)
	 * 
	 * @return reference to static image 
	 */		
	public BufferedImage getStaticFrame(String fileName) {
		try {

			/**
			 * Clients who want to test static frames
			 * @return FramesSimulation/f1.jpg from resources
			 */

			return  ImageIO.read(new File(classLoader.getResource(fileName).getFile()));

		}

		/**
		 * Image not found (404)
		 * @return empty image
		 */
		catch (Exception e){
			System.out.println("File not found in resources: " + fileName);
			System.out.println(e.getMessage());
			return processPPMImage(640, 480, new byte[0]);

		}
	}	
	
	
	// These functions come from: http://graphics.stanford.edu/~mdfisher/Kinect.html
	private float rawDepthToMeters(int depthValue) {
	  if (depthValue < 2047) {
	    return (float)(1.0 / ((double)(depthValue) * -0.0030711016 + 3.3309495161));
	  }
	  return 0.0f;
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



	static public BufferedImage processPGMImage(int width, int height, ShortBuffer data) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int red, green, blue, pixel;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {

				int offset = x + y*width;
				short depth = data.get(offset);
				image.setRGB(x, y, depth2rgb(depth));
			}
		}

		return image;
	}

    static public BufferedImage processPGMImage(int width, int height, byte[] data) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int red, green, blue, pixel;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                int offset = x + y*width;
//                short depth = data.get(offset);
                short depth = Kinect.bytesToShort(data);
                image.setRGB(x, y, depth2rgb(depth));
            }
        }

        return image;
    }

    public static short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    static int depth2rgb(short depth) {
		int r,g,b;

		float v = depth / 2047f;
		v = (float) Math.pow(v, 3)* 6;
		v = v*6*256;

		int pval = Math.round(v);
		int lb = pval & 0xff;
		switch (pval>>8) {
			case 0:
				b = 255;
				g = 255-lb;
				r = 255-lb;
				break;
			case 1:
				b = 255;
				g = lb;
				r = 0;
				break;
			case 2:
				b = 255-lb;
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
				g = 255-lb;
				r = 255;
				break;
			case 5:
				b = 0;
				g = 0;
				r = 255-lb;
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


	static void useFileSystemDepth() throws InterruptedException {
		OpenISSSOAPServiceImpl service = new OpenISSSOAPServiceImpl();

		// get files in fake recording directory
		File dir = new File(FAKENECT_PATH);
		File[] directoryFiles = dir.listFiles();

		// store file names in here (both ppm and pgm)
		// they are accessed using an offset
		ArrayList<String> fileNames = new ArrayList<>(770);


		if(directoryFiles != null) {


			new Thread(new Runnable() {

				@Override
				public void run() {

					int pgmCount = 0;

					// loop, count and populate arraylist of file names
					for (int i = 0; i < directoryFiles.length; i++) {
						if (directoryFiles[i].getName().endsWith(".pgm")) {
							fileNames.add(directoryFiles[i].getName());
							pgmCount++;
						}
					}

					// sort array of names
					Collections.sort(fileNames);


					// offset for this recording

					System.out.println("useFileSystemDepth Reading from filesystem..");
					System.out.println("pgm="+pgmCount);
					System.out.println("useFileSystemDepth Starting loop...");

					// loop forever
					while(true) {
						for(int i = 0; i < pgmCount; i++) {
							setDepthFileName(FAKENECT_PATH + "/" + fileNames.get(i));
							try {
								TimeUnit.MILLISECONDS.sleep(150);
							}
							catch (Exception e) {
								System.out.println(e.getMessage());
							}
						}
						System.out.println("useFileSystemDepth Re-Looping...");
					}

				}
			}).start();

		}
	}

	static void useFileSystemColor() throws InterruptedException {
		OpenISSSOAPServiceImpl service = new OpenISSSOAPServiceImpl();


		// get files in fake recording directory
		File dir = new File(FAKENECT_PATH);
		File[] directoryFiles = dir.listFiles();

		// store file names in here (both ppm and pgm)
		// they are accessed using an offset
		ArrayList<String> fileNames = new ArrayList<>(770);


		if(directoryFiles != null) {


			new Thread(new Runnable() {

				@Override
				public void run() {

					int ppmCount = 0;

					// loop, count and populate arraylist of file names
					for (int i = 0; i < directoryFiles.length; i++) {
						if(directoryFiles[i].getName().endsWith(".ppm")) {
							fileNames.add(directoryFiles[i].getName());
							ppmCount++;
						}
					}

					// sort array of names
					Collections.sort(fileNames);

					// offset for this recording
					System.out.println("useFileSystemColor Reading from filesystem..");
					System.out.println("ppm="+ppmCount);
					System.out.println("useFileSystemColor Starting loop...");

					while(true) {
						for(int i = 0; i < ppmCount; i++) {
							setColorFileName(FAKENECT_PATH + "/" + fileNames.get(i));
							try {
								TimeUnit.MILLISECONDS.sleep(150);

							}
							catch (Exception e){
								System.out.println(e.getMessage());
							}
						}
						System.out.println("useFileSystemColor Re-Looping...");
					}

				}
			}).start();
		}
	}

	public String getFileName(String type) {
		if(type.equalsIgnoreCase("color")){
			return colorFileName;
		} else {
			return depthFileName;
		}
	}

	public static void setColorFileName(String fileName) {
		colorFileName = fileName;
	}

	public static void setDepthFileName(String fileName) {
		depthFileName = fileName;
	}


}