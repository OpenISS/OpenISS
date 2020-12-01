const PORT = 20000;
const REPLICA_PORT = 20000;
const MULTICAST_ADDR = "230.255.255.255";

const dgram = require("dgram");
const process = require("process");

const socket = dgram.createSocket({ type: "udp4", reuseAddr: true });

socket.bind(PORT);

socket.on("listening", function() {
    socket.addMembership(MULTICAST_ADDR);
    sendMessage();
    const address = socket.address();
    console.log(
        `UDP socket listening on ${address.address}:${address.port} pid: ${
            process.pid
        }`
    );
});

function sendMessage() {
    const message = Buffer.from(`1,canny`);
    socket.send(message, 0, message.length, REPLICA_PORT, MULTICAST_ADDR, function() {
        console.info(`Sending message "${message}"`);
    });
}

socket.on("message", function(message, rinfo) {
    console.info(`Message from: ${rinfo.address}:${rinfo.port} - ${message}`);
});