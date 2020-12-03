const http = require('http')
const fs = require('fs')
const PORT = 20000;
const REPLICA_PORT = 20000;
const MULTICAST_ADDR = "230.255.255.255";
const spawn = require("child_process").spawn;
var pythonProcess = spawn('python',["../python/udp_receiver.py"]);
var nodeProcess = spawn('node',["../js-v2/app.js"])

var SEQ_NUM = 1
var success = {};
var fail = {};
var delivered = {};
var checksums = {};

const dgram = require("dgram");
const process = require("process");
const { stringify } = require('querystring');

const socket = dgram.createSocket({ type: "udp4", reuseAddr: true });

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
        delivered[frame].push(replica);
        var validator = spawn('python',["./test.py", "../python/jobs/f"+ frame + ".jpg"]);
        validator.stdout.on('data', function(data) {
            if(!(frame in checksums)){
                checksums[frame] = [];
            }
            checksum = data.toString().replace(/(\r\n|\n|\r)/gm, "");
            checksums[frame].push([replica, checksum]);
            if (delivered[frame].length > 1){
                if (checksums[frame].length == 2) {
                    if(checksums[frame][0][1] == checksums[frame][1][1]){
                        success[frame].push(delivered[frame][0]);
                        success[frame].push(delivered[frame][1]);
                        sendUDPImage(frame);
                    }
                }
                else if(checksums[frame].length == 3){
                    if(checksums[frame][0][1] == checksums[frame][2][1]){
                        if(checksums[frame][0][1] != checksums[frame][1][1]){
                            fail[frame].push(delivered[frame][1]);
                            success[frame].push(delivered[frame][0]);
                        }
                        success[frame].push(delivered[frame][2]);
                    }
                    else if (checksums[frame][0][1] == checksums[frame][1][1]){
                        fail[frame].push(delivered[frame][2]);
                    }
                    else if (checksums[frame][1][1] == checksums[frame][2][1]){
                        success[frame].push(delivered[frame][1]);
                        success[frame].push(delivered[frame][2]);
                    }
                    else{
                        console.log("WARNING! No replicas matching. There might be an issue with image retrieval/processing.")
                        fail[frame].push(delivered[frame][0]);
                        fail[frame].push(delivered[frame][1]);
                        fail[frame].push(delivered[frame][2]);
                    }
                    checkFailQueue();
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
        const message = Buffer.from(SEQ_NUM + "," + method);
        socket.send(message, 0, message.length, REPLICA_PORT, MULTICAST_ADDR, function() {
            console.info(`Sending message "${message}"`);
        });
        SEQ_NUM += 1;
        res.write("Message Received!");
    }
    res.end();
}).listen(8085);


pythonProcess.stdout.on('data', function(data) {
    console.log("==============PYTHON REPLICA==============\n" + data.toString() + "\n==============PYTHON REP END=============="); 
});

pythonProcess.on("close", function() {
    // Wait for process to exit, then run again
    pythonProcess = spawn('python',["../python/udp_receiver.py"]);
});

nodeProcess.stdout.on('data', function(data) {
    console.log("===============NODE REPLICA===============\n" + data.toString() + "\n===============NODE REP END==============="); 
});

function checkFailQueue(){
    //
    // TODO: Check failed queues and restart replicas if 3 fails in a row
    //
    // Could use this: pythonProcess.kill("SIGINT");
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
                file_path = "../resources/jobs/f" + frame + ".jpg"
            }
            else{
                file_path = "../js-v2/jobs/f" + frame + ".jpg"
            }
        }

        if(file_path != ""){
            fs.readFile(file_path, function (err,data) {
                if (err) {
                    return console.log(err);
                }
                //
                // TODO: SEND VIA SOCKET IO INSTEAD OR FIX UDP
                //
                socket.send(message, 0, message.length, REPLICA_PORT, MULTICAST_ADDR, function(err, bytes) {
                    if (err) 
                        throw err;
                    console.info(`Sending message "${message}"`);
                });
              });
        }
    }
}