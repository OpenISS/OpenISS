import sys
import imagehash
from PIL import Image

file_name = sys.argv[0]
# file_name = "1_canny.jpg"
checksum = imagehash.average_hash(Image.open(file_name))
# print(checksum)
sys.stdout.write(str(checksum))
sys.stdout.flush()
sys.exit(1)
