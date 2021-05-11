# Javascript APIs

This folder contains three separate project folders that are used for different purposes. Below is a short description of what each one does.

## opencv-replica

This project contains the implementation of the Javascript replica used for the three replica HA-FT solution and its frontend.

More info [here](opencv-replica/README.md)

## sequencer

This project contains the implementation of the sequencer used for the three replicas. It receives frames from three different implementations, compares the checksum of the images and sends the image to the frontend if more than 2 match.

More info [here](sequencer/README.md)

## ws-client

The ws-client project provides a simple frontend to test the Java openiss backend. It provides a simple UI to switch between color and depth and use OpenCV for image manipulation.

More info [here](ws-client/README.md)