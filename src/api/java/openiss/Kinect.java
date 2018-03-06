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
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;

import org.openkinect.freenect.Context;
import org.openkinect.freenect.DepthFormat;
import org.openkinect.freenect.DepthHandler;
import org.openkinect.freenect.Device;
import org.openkinect.freenect.FrameMode;
import org.openkinect.freenect.Freenect;
import org.openkinect.freenect.VideoFormat;
import org.openkinect.freenect.VideoHandler;

public class Kinect {

	static byte[] color;
	static byte[] depth;

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



	
	/**
	 * Kinect constructor, usually called in the setup() method in your sketch to
	 * initialize and start the library.
	 * 
	 */
	
	public Kinect() {

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

		if (!started) {
			start();
		}

		if (device != null) {
			device.setDepthFormat(DepthFormat.D11BIT);
			final Kinect ref = this;
			device.startDepth(new DepthHandler() {
				public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {

					depth = new byte[frame.remaining()];
					frame.get(depth);
					frame.flip();

				}
			});
		}
	}


	/**
	 * Start getting RGB video from Kinect.
	 * 
	 */
	public void initVideo() {
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
	 * @param boolean mirror
	 */
	public void enableMirror(boolean mirror){
		mirrorMode = mirror;
	}

	/**
	 * Enable IR image (instead of RGB video)
	 * 
	 * @param b true to turn it on, false to turn it off
	 */
	public void enableIR(boolean b) {
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
	public BufferedImage getDepthImage() {

		return processPPMImage(640, 480, depth);

	}
	
	/**
	 * Get the video image (does not make a new object, use get() if you need a copy)
	 * 
	 * @return reference to video image 
	 */		
	public BufferedImage getVideoImage() {

		return processPPMImage(640, 480, color);

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
	

}