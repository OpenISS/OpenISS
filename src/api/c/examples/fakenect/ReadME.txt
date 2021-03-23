before doing anything with this folder cd into this folder in your terminal and run the following commands
	ldd freenect-glview
	export LD_LIBRARY_PATH=/usr/local/lib
	ldd freenect-glview

To get 'make rungl' and 'make runglpcl' to work first do 'make runfakenect'

'make runfakenect' will create a folder called sessions in this directory and will record data from the kinect and put that data into the sessions folder

From there you can run 'make rungl' and 'make runglpcl' which will run the 'freenect-glview' and 'freenect-glpclview' commands on ./sessions


