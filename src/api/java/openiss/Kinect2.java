package openiss;

import org.openkinect.freenect2.Device;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/*
openKinect2 library for Processing
Copyright (c) 2014 Thomas Sanchez Lengeling

* Redistribution and use in source and binary forms, with or
* without modification, are permitted provided that the following
* conditions are met:
*
* Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in
* the documentation and/or other materials provided with the
* distribution.
*

openKinect2 library for Processing is free software: you can redistribute it and/or
modify it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

openKinect2 for Processing is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with openKinect2 library for Processing.  If not, see
<http://www.gnu.org/licenses/>.
*/

public class Kinect2 extends Device implements Sensor {


//    public Kinect2(PApplet _p) {
//        super(_p);
//        parent = _p;
//        parent.registerMethod("dispose", this);
//    }

    public Kinect2() {
        super();
    }

    public void dispose() {
        System.out.println("EXIT");
        stopDevice();
    }

    public static void main(String[] args) {
        Kinect2 kinect2 = new Kinect2();
        kinect2.init();
        kinect2.initDevice();

        JFrame frame = new JFrame();
        Test test = new Test(kinect2);
        frame.getContentPane().add(test);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setVisible(true);

        while (true) {

            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            test.display();
        }
    }

    public static class Test extends JPanel {

        Kinect2 kinect2;
        int count = 0;

        public Test(Kinect2 kinect2) {
            this.kinect2 = kinect2;
        }

        public void display() {
            this.repaint();
        }


        public void paint(Graphics g) {
            super.paint(g);
//            Image img = kinect2.getVideoImage();
            Image img = kinect2.getDepthImage();
            g.drawImage(img, 0, 0, this);
//            g.drawString("hello world" + count++, 20, 20);
        }
    }

    @Override public BufferedImage getSensorVideoImage() {
        return this.getVideoImage();
    }
    @Override public BufferedImage getSensorDepthImage() {
        return this.getDepthImage();
    }
    @Override public void initSensorVideo() {
        this.initVideo();
    }
    @Override public void initSensorDepth() {
        this.initDepth();
    }
    @Override public void initSensor() {
        this.initDevice();
    }

}
