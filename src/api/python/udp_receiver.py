import flask
import numpy as np
import os
import requests
import sys
from cv2 import cv2 as cv
from socket import AF_INET, SOCK_DGRAM, INADDR_ANY, IPPROTO_IP, IP_ADD_MEMBERSHIP, SOL_SOCKET, SO_REUSEADDR, socket, inet_aton, error as socket_error
import struct
from threading import Thread
import imagehash
from PIL import Image

class Request():
    def __init__(self, frame, method):
        self.frame = frame
        self.method = method
        self.checksum = ""

    def update_checksum(self, checksum):
        self.checksum = checksum

    def get_frame(self):
        return self.frame
    
    def get_method(self):
        return self.method

    def get_checksum(self):
        return self.checksum

img_url = "http://localhost:8080/rest/openiss/color"
host = "localhost"
multicast_group = "230.255.255.255"
multicast_port = 20000
#sequencer_port = 20000
timeout = 3
buf = 1024
app = flask.Flask(__name__)
requests_awaiting = {}
requests_finished = []
success_req = {}
delivered_req = {}
fail_req = {}

# TODO : Figure out how to synchronize sequence count between sequencer and implementation
seq_count = 1

@app.route('/getUDPPort', methods=['GET'])
def getUDPPort():
    _, temp_port = serv.get_port()
    return str(temp_port)

@app.route('/getJob/<seq_num>.jpg', methods=['GET'])
def publishFrame(seq_num):
    file_path = "f" + str(seq_num) + ".jpg"
    if os.path.isfile(file_path):
        return flask.send_file(file_path, mimetype='image/jpg')
    else:
        return flask.send_file("images/color_fail.jpg", mimetype='image/jpg')

def getFrame():
    response = requests.get(img_url)
    result = response.content
    return np.frombuffer(result, dtype=np.uint8)

def deliverFrame(frame_num):
    checksum = requests_awaiting[frame_num].checksum
    addr = (multicast_group, multicast_port)
    udp_string = str(frame_num) + "," + str(checksum)
    udp_socket = socket(AF_INET,SOCK_DGRAM)
    udp_socket.sendto(udp_string.encode(), addr)
    print("Sending %s ..." % udp_string)
    udp_socket.close()

def processFrame(frame_num):
    if requests_awaiting[frame_num].get_method() == "canny":
        doCanny(frame_num)
    elif requests_awaiting[frame_num].get_method() == "contour":
        doContour(frame_num)
    else:
        print("Method called does not exist on web service! Skipping...")
        requests_awaiting.pop(frame_num, None)
    
def checkRequestsAwaiting():
    global seq_count
    while seq_count in requests_awaiting:
        deliverFrame(seq_count)
        requests_awaiting.pop(seq_count, None)
        requests_finished.append(seq_count)
        seq_count += 1

def addToSharedQueues(frame_num, method, replica_num):
    global success_req, seq_count
    if method == "success":
        if frame_num not in success_req:
            success_req[frame_num] = []
        success_req[frame_num].append(replica_num)
    elif method == "fail":
        if frame_num not in fail_req:
            fail_req[frame_num] = []
        fail_req[frame_num].append(replica_num)
    else:
        if frame_num not in delivered_req:
            delivered_req[frame_num] = []
        delivered_req[frame_num].append(replica_num)    

def doCanny(seq_num):
    x = getFrame()
    img = cv.imdecode(x, cv.IMREAD_UNCHANGED)
    if img is None:
        print("Error loading image")
        return
    img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
    edges = cv.Canny(img_gray, 50, 150, 3, L2gradient=False)
    edges = cv.cvtColor(edges, cv.COLOR_GRAY2BGR)
    print("Saving canny...")
    cv.imwrite("canny.jpg", edges)
    file_name = "f" + str(seq_num) + ".jpg"
    sys.stdout.flush()
    cv.imwrite(file_name, edges)
    checksum = imagehash.average_hash(Image.open(file_name))
    requests_awaiting[seq_num].checksum = checksum

def doContour(seq_num):
    x = getFrame()
    img = cv.imdecode(x, cv.IMREAD_UNCHANGED)
    if img is None:
        print("Error loading image")
        return
    img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
    _, img_thresh = cv.threshold(img_gray ,100, 255, cv.THRESH_BINARY)
    print("Saving contour...")
    img_thresh = cv.cvtColor(img_thresh, cv.COLOR_GRAY2BGR)
    cv.imwrite("contour.jpg", img_thresh)
    file_name = "f" + str(seq_num) + ".jpg"
    cv.imwrite(file_name, img_thresh)
    checksum = imagehash.average_hash(Image.open(file_name))
    requests_awaiting[seq_num].checksum = checksum

class UDPServer():
    def __init__(self):
        self._running = True
        self.sock = socket(AF_INET, SOCK_DGRAM)
        self.buf = buf
        self.timeout = timeout
        self.group = inet_aton(multicast_group) + inet_aton("0.0.0.0")
        self.sock.setsockopt(IPPROTO_IP, IP_ADD_MEMBERSHIP, self.group)
        self.sock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        self.sock.bind(("", multicast_port))
        
    def terminate(self):
        self._running = False
        self.sock.shutdown(0)
        self.sock.close()

    def is_running(self):
        return self._running

    def get_port(self):
        return self.sock.getsockname()

    def run(self):
        global seq_count
        while True:
            try:
                print("Waiting to receive data...")
                sys.stdout.flush()
                data,address = self.sock.recvfrom(self.buf)
                if data:
                    strings = data.decode('utf-8')
                    seq_num = int(strings.split(',')[0])
                    method = strings.split(',')[1]
                    print("Message:", method, seq_num, "Address: ", address)
                    if(method == "success" or method == "fail" or method == "delivered"):
                        replica_num = int(strings.split(',')[2])
                        addToSharedQueues(seq_num, method, replica_num)
                    elif(seq_num >= seq_count and seq_num not in requests_finished and seq_num not in requests_awaiting):
                        requests_awaiting[seq_num] = Request(seq_num, method)
                        processFrame(seq_num)
                        checkRequestsAwaiting()
                    else:
                        print("Packet with sequence number ", seq_num, " already received!")
                    sys.stdout.flush()
            except socket_error:
                self.sock.close()
                break

# Main execution
serv = UDPServer()
t = Thread(target=serv.run)
t.start()

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8002)
# If here, ctrl+c was called
serv.terminate()
t.join()
sys.exit()


