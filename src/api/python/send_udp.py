from socket import AF_INET, SOCK_DGRAM, socket
import sys
import time
import requests
from cv2 import cv2

host = "localhost"
method = sys.argv[1]
seq_num = sys.argv[2]
buf=2048
url = "http://localhost:8002/getUDPPort"
response = requests.get(url)
data = response.content
port = int(data.decode('utf-8'))
addr = (host,port)

udp_string = method + "," + seq_num

udp_socket = socket(AF_INET,SOCK_DGRAM)
udp_socket.sendto(udp_string.encode(), addr)

print("Sending %s ..." % udp_string)

udp_socket.close()