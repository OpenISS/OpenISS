import imagehash
from PIL import Image
import sys
import os
import time

file_name = sys.argv[1]
frame = sys.argv[2]

while not os.path.exists(file_name):
    time.sleep(1)
checksum = imagehash.average_hash(Image.open(file_name))
print(str(checksum) +"," + str(frame))