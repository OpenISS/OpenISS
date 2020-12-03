const express = require('express')
const replica = require('./lib/replica.js')
const app = express()
const port = 3000

let seq_id = 0
let requests_awaiting = {}
let requests_finished = []

const MULTICAST_PORT = 20000;
const MULTICAST_ADDR = "230.255.255.255";;
var dgram = require('dgram');
const client = dgram.createSocket({ type: "udp4", reuseAddr: true });

client.on('listening', function () {
    var address = client.address();
    console.log('UDP Client listening on ' + address.address + ":" + address.port);
    client.setBroadcast(true)
    client.setMulticastTTL(128); 
    client.addMembership(MULTICAST_ADDR);
});

client.on('message', function (message, remote) {   
    console.log('A: Epic Command Received. Preparing Relay.');
    console.log('B: From: ' + remote.address + ':' + remote.port +' - ' + message);
});

client.bind(MULTICAST_PORT);

app.get('/', (req, res) => {
    seq_id++
    res.send('Hello World: ' + seq_id)
})

app.get('/getCanny', (req, res) => {
    seq_id++
    replica.doCanny(seq_id)
    res.send('Canny Request: ' + seq_id)
})

app.get('/getContour', (req, res) => {
    seq_id++
    replica.doContour(seq_id)
    res.send('Contour Request: ' + seq_id)
})

app.listen(port, () => {
  console.log(`OpenISS OpenCV replica listening at http://localhost:${port}`)
})