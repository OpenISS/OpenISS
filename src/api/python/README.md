# OpenISS WebServices

This is a python REST implementation for OpenISS

Currently only supports canny and contour using static images

## Usage

Install all required packages

```
pip install -r requirements.txt 
```

1) Make sure openISS api is running by checking http://localhost:8080/rest/openiss/color

2) Run udp_receiver.py

```
python udp_receiver.py
```

3) In another window  run send_udp with the function you want, with a hardcoded sequence number

```
python send_udp.py contour 1
```
or
```
python send_udp.py canny 2
```

### Note:
The implementation currently holds a hardcoded counter and implements it by 1 on every request. If you send a sequence number under that, it will not be processed.

To test the diff of the images, do the following:

```
pip install diffimg
python -m diffimg canny_java.jpg canny.jpg -r -d
```
More info on the module here: https://github.com/nicolashahn/diffimg

### To test with node js script:

1) Run udp_receiver.py twice on two separate windows 

2) Run the script app.js and check if the udp receivers are getting the message
```
node app.js
```
