curl --request PATCH http://localhost:8080/rest/openiss/mix/depth
#curl --request PATCH http://localhost:8080/rest/openiss/opencv/canny
#curl --request PATCH http://localhost:8080/rest/openiss/opencv/contour

for i in $(seq 1 100)
do
    curl -get http://localhost:8080/rest/openiss/depth > depthimage$i.jpg
    curl -get http://localhost:8080/rest/openiss/color > colorimage$i.jpg
done