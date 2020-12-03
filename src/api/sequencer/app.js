const http = require('http')
const fs = require('fs')
const PORT = 20000;
const REPLICA_PORT = 20000;
const MULTICAST_ADDR = "230.255.255.255";
const io = require('socket.io-client');
const spawn = require("child_process").spawn;
var requestStart = new Date();

let SEQ_NUM = 1
let success = {};
let fail = {};
let delivered = {};
let checksums = {};

const dgram = require("dgram");
const process = require("process");
const { stringify } = require('querystring');

const socket = dgram.createSocket({ type: "udp4", reuseAddr: true });
ioClient = io.connect("http://localhost:3000")

socket.bind(PORT);

socket.on("listening", function() {
    socket.addMembership(MULTICAST_ADDR);
    const address = socket.address();
    console.log(
        `UDP socket listening on ${address.address}:${address.port} pid: ${
            process.pid
        }`
    );
});

socket.on("message", function(message, rinfo) {
    console.info(`Message from: ${rinfo.address}:${rinfo.port} - ${message}`);
    var responseString = message.toString().split(",");

    frame = responseString[0];
    method = responseString[1];
    replica = responseString[2];

    if(method == "delivered"){
        // var responseTime = new Date() - requestStart;
        // if(responseTime > 1000){
        //     fail[frame].push(replica);
        //     return;
        // }
        // Initialize hash maps
        if(!(frame in delivered)){
            delivered[frame] = [];
        }
        if(!(frame in fail)){
            fail[frame] = [];
        }
        if(!(frame in success)){
            success[frame] = [];
        }
        if(!(frame in checksums)){
            checksums[frame] = [];
        }
        delivered[frame].push(replica);
        var validator = spawn('python',["./test.py", "../python/jobs/f"+ frame + ".jpg", frame]);
        validator.stdout.on('data', function(data) {
            response = data.toString().replace(/(\r\n|\n|\r)/gm, "");
            validatorResponse = response.toString().split(",");
            checksum = validatorResponse[0];
            correct_frame = validatorResponse[1];
            
            if(!(correct_frame in checksums)){
                checksums[correct_frame] = [];
            }
            checksums[correct_frame].push([replica, checksum]);
            if (checksums[correct_frame].length > 1){
                if (delivered[correct_frame].length == 2) {
                    if(checksums[correct_frame][0][1] == checksums[correct_frame][1][1]){
                        success[correct_frame].push(delivered[correct_frame][0]);
                        success[correct_frame].push(delivered[correct_frame][1]);
                        console.log("Checksums matching!!");
                        sendUDPImage(correct_frame);
                    }
                }
                else if(checksums[correct_frame].length == 3){
                    if(checksums[correct_frame][0][1] == checksums[correct_frame][2][1]){
                        if(checksums[correct_frame][0][1] != checksums[correct_frame][1][1]){
                            fail[correct_frame].push(delivered[correct_frame][1]); 
                            checkFailQueue(delivered[correct_frame][1]);
                        }
                        success[correct_frame].push(delivered[correct_frame][0]);
                        success[correct_frame].push(delivered[correct_frame][2]);
                    }
                    else{
                        if(checksums[correct_frame][1][1] == checksums[correct_frame][2][1]){
                            fail[correct_frame].push(delivered[correct_frame][0]);
                            checkFailQueue(delivered[correct_frame][0]);
                        }
                        else{
                            if((checksums[correct_frame][0][1] != checksums[correct_frame][1][1])){
                                console.log("WARNING! No replicas matching. There might be an issue with image retrieval/processing.")
                                fail[correct_frame].push(delivered[correct_frame][0]);
                                fail[correct_frame].push(delivered[correct_frame][1]);
                                fail[correct_frame].push(delivered[correct_frame][2]);
                            }
                            else{
                                fail[correct_frame].push(delivered[correct_frame][2]);
                                checkFailQueue(delivered[correct_frame][2]);
                            }
                        }
                    }
                }
            }
        });
    }
});

http.createServer(function (req, res) {
    res.setHeader('Content-Type', 'text/plain');
    var requestString = req.url.split("/");
    frame = requestString[1];
    method = requestString[2];
    if(method == "canny" || method == "contour"){
        requestStart = new Date();
        const message = Buffer.from(SEQ_NUM + "," + method);
        socket.send(message, 0, message.length, REPLICA_PORT, MULTICAST_ADDR, function() {
            console.info(`Sending message "${message}"`);
        });
        SEQ_NUM += 1;
        res.write("Message Received!");
    }
    res.end();
}).listen(8085);

function checkFailQueue(replica){
    fail_count = 0;
    for (var fail_list in fail){
        if (replica in fail_list){
            counter += 1;
        }
        else{
            counter = 0;
        }

        if(counter == 3){
            // Add p2 code to restart faulty process============================
        }
    }
}

function sendUDPImage(frame) {
    // PYTHON = 1, JAVA = 2, NODEJS = 3
    var file_path = "";
    var message = "IMAGE_TEXT";
    for (i = 0; i < success[frame].length; ++i) {
        if(success[frame][i]){
            if(success[frame][i] == 1){
                file_path = "../python/jobs/f" + frame + ".jpg"
            }
            else if(success[frame][i] == 2){
                file_path = "../resources/Java/f" + frame + ".jpg"
            }
            else{
                // Get via url request instead
                // file_path = "../js-v2/jobs/f" + frame + ".jpg"
            }
        }

        if(file_path != ""){
            fs.readFile(file_path, function (err,data) {
                if (err) {
                    return console.log(err);
                }
                ioClient.emit('show image', { image: true, buffer: data.toString('base64') });
            });
        }
    }
}