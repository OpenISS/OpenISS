import flask
import numpy as np
import os
import requests
import sys
from cv2 import cv2 as cv
from socket import AF_INET, SOCK_DGRAM, socket, error as socket_error
from threading import Thread

img_url = "http://localhost:8080/rest/openiss/color"
host = "localhost"
timeout = 3
buf = 2048
app = flask.Flask(__name__)
requests_awaiting = {}
requests_finished = []
# TODO : Figure out how to synchronize sequence count between sequencer and implementation
seq_count = 1

@app.route('/getUDPPort', methods=['GET'])
def getUDPPort():
    _, temp_port = serv.get_port()
    return str(temp_port)

@app.route('/getFrame/2', methods=['GET'])
def publishFrame():
    if os.path.isfile("result.jpg"):
        return flask.send_file("result.jpg", mimetype='image/jpg')
    else:
        return flask.send_file("images/color_fail.jpg", mimetype='image/jpg')

def checkRequestsAwaiting():
    global seq_count
    while seq_count in requests_awaiting:
        if requests_awaiting[seq_count] == "canny":
            doCanny()
        elif requests_awaiting[seq_count] == "contour":
            doContour()
        else:
            print("Method called does not exist on web service! Skipping...")
        requests_awaiting.pop(seq_count, None)
        requests_finished.append(seq_count)
        seq_count += 1

def doCanny():
    response = requests.get(img_url)
    result = response.content
    x = np.frombuffer(result, dtype=np.uint8)
    img = cv.imdecode(x, cv.IMREAD_UNCHANGED)
    if img is None:
        print("Error loading image")
        return
    img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
    edges = cv.Canny(img_gray, 50, 150, 3, L2gradient=False)
    print("Saving canny...")
    cv.imwrite("canny.jpg", edges)
    cv.imwrite("result.jpg", edges)

def doContour():
    response = requests.get(img_url)
    result = response.content
    x = np.frombuffer(result, dtype=np.uint8)
    img = cv.imdecode(x, cv.IMREAD_UNCHANGED)
    if img is None:
        print("Error loading image")
        return
    img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
    _, img_thresh = cv.threshold(img_gray ,100, 255, cv.THRESH_BINARY)
    print("Saving contour...")
    cv.imwrite("contour.jpg", img_thresh)
    cv.imwrite("result.jpg", img_thresh)

class UDPServer():
    def __init__(self):
        self._running = True
        self.sock = socket(AF_INET, SOCK_DGRAM)
        self.sock.bind((host, 0))
        self.buf = buf
        self.timeout = timeout
        
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
                data,address = self.sock.recvfrom(self.buf)
                if data:
                    strings = data.decode('utf-8')
                    method = strings.split(',')[0]
                    seq_num = int(strings.split(',')[1])
                    print("Message:", method, seq_num, "Address: ", address)
                    if(seq_num > seq_count and seq_num not in requests_finished and seq_num not in requests_awaiting):
                        requests_awaiting[seq_num] = method
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


