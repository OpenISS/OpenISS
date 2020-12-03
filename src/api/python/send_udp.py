from socket import AF_INET, SOCK_DGRAM, socket

import requests
import sys

host = "localhost"
seq_num = sys.argv[1]
method = sys.argv[2]
buf = 2048
# url = "http://localhost:8002/getUDPPort"
# response = requests.get(url)
# data = response.content
# port = int(data.decode('utf-8'))
port = 20000
addr = (host, port)

udp_string = seq_num + "," + method

udp_socket = socket(AF_INET, SOCK_DGRAM)
udp_socket.sendto(udp_string.encode(), addr)

print("Sending %s ..." % udp_string)

udp_socket.close()
