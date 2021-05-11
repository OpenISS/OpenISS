const express = require('express')
const replica = require('./lib/replica.js')
const app = express()
const port = 8003
const io = require('socket.io-client');
const images = __dirname + '/images/';

let imgArray = new Array();

// @TODO make queues work
let requests_awaiting = {}
let requests_finished = []

let SEQ_NUM = 1;
const REPLICA_NO = 3
const MULTICAST_PORT = 20000;
const MULTICAST_ADDR = "230.255.255.255";;
var dgram = require('dgram');
const { Console } = require('console');
const client = dgram.createSocket({ type: "udp4", reuseAddr: true });

ioClient = io.connect("http://localhost:3000")

client.on('listening', function () {
    var address = client.address();
    console.log('UDP Client Group Multicast on ' + address.address + ":" + address.port)
    client.setBroadcast(true)
    client.setMulticastTTL(128)
    client.addMembership(MULTICAST_ADDR)
    ioClient.emit('chat message', 'UDP Client Group Multicast on ' + address.address + ":" + address.port)

});

client.on('message', function (message, remote) {   
    console.log('A: Epic Command Received. Preparing Relay.');
    console.log('B: From: ' + remote.address + ':' + remote.port +' - ' + message);

    var msg = message.toString().split(',')

    if (msg[1]) {
        if (msg[1].toLowerCase() === 'canny' || msg[1].toLowerCase() === 'contour') {
            if(!(msg[0] in requests_awaiting) && !(msg[0] in requests_finished)){
                requests_awaiting[msg[0]] = msg[1];
                //processFrame(msg[0], msg[1].toLowerCase());
                checkRequestsAwaiting();
            }
        }
        else {
            console.log('C: Ignoring message: '+ message)
        }
    }
    else {
        console.log('C: Ignoring message: '+ message)
    }
});

client.bind(MULTICAST_PORT);

app.get('/', (req, res) => {
    res.send('Hello World: ' + seq_id)
})

app.get('/getJob/:id', function(req, res, next) {

    if (imgArray[req.params.id]) {
        var img = Buffer.from(imgArray[req.params.id], 'base64')
        res.contentType('image/jpeg')
        return res.send(img)    
    }
    else {
        res.status(404).send("Sorry can't find that!")
    }
})

// var doFrames = setInterval(myTimer, 3000);

// function myTimer() {
//     seq_id++
//     console.log("sequence = " + seq_id)
//     sendAwaitMessage(seq_id)
//     let args = {type: 'color', seq_id: seq_id}
//     replica.getCanny(args, (function (error, response, body) {

//         if(error) {
//             console.log("got error")
//             console.log(error);
//         }
//         else {
//             imgArray[seq_id] = body
//             sendDeliveredMessage(seq_id)
//         }
//     }))    
//     if (seq_id === 3) { // Stop at given Frame
//         console.log('stopping timer')
//         return clearInterval(doFrames);
//     }
// }

function sendDeliveredMessage(id) {
    const message = Buffer.from(id+`,delivered,`+REPLICA_NO);
    client.send(message, 0, message.length, MULTICAST_PORT, MULTICAST_ADDR, function() {
        console.info(`Sending message "${message}"`);
    });
}

function checkRequestsAwaiting(){
    while(SEQ_NUM in requests_awaiting){
        processFrame(SEQ_NUM, requests_awaiting[SEQ_NUM]);
        sendDeliveredMessage(SEQ_NUM);
        delete requests_awaiting[SEQ_NUM];
        requests_finished.push(SEQ_NUM);
        SEQ_NUM += 1;
    }
}

function processFrame(frame, method){
    if(method.toLowerCase() === 'canny'){
        doCanny(frame);
    }
    else{
        doContour(frame);
    }
}

function doCanny(frame){
    console.log('C: processing canny for sequence: ' + frame)
    let args = {type: 'color', seq_id: frame}
    replica.getCanny(args, (function (error, response, body) {

        if(error) {
            console.log("got error")
            console.log(error);
        }
        else {
            console.log("Canny succesfully completed");
            imgArray[frame] = body
            // sendDeliveredMessage(frame)

            // // @TODO this will be sent by RM
            // console.log("sending image to FE using WS")
            // ioClient.emit('chat message', 'sending image')

            // ioClient.emit('show image', { image: true, buffer: body.toString('base64') });

        }
    }))
}

function doContour(frame){
    console.log('C: processing contour for sequence: ' + frame)
    let args = {type: 'color', seq_id: frame}
    replica.getContour(args, (function (error, response, body) {

        if(error) {
            console.log("got error")
            console.log(error);
        }
        else {
            console.log("Contour succesfully completed");
            imgArray[frame] = body
            // sendDeliveredMessage(frame)

            // // @TODO this will be sent by RM

            // ioClient.emit('show image', { image: true, buffer: body.toString('base64') });
        }
    }))
}

app.listen(port, () => {
  console.log(`OpenISS OpenCV replica listening at http://localhost:${port}`)
})