import imagehash
from PIL import Image
import sys

file_name = sys.argv[1]
frame = sys.argv[2]

checksum = imagehash.average_hash(Image.open(file_name))
print(str(checksum) +"," + str(frame))