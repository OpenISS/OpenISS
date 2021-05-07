var app = require('express')();
var https = require('https');
const fs = require('fs');
var port = process.env.PORT || 443;

var key = fs.readFileSync(__dirname + '/./certs/selfsigned.key');
var cert = fs.readFileSync(__dirname + '/./certs/selfsigned.crt');
var options = {
  key: key,
  cert: cert
};

app.get('/', function(req, res){
  res.sendFile(__dirname + '/frontend.html');
});

var server = https.createServer(options, app);
var io = require('socket.io')(server);

io.on('connection', function(socket){
  console.info(`Client connected [id=${socket.id}]`);
  socket.on('chat message', function(msg){
    console.log('Sending message to Web FE:' + msg)
    io.emit('chat message', msg);
  });
  socket.on('show image', function(msg){
    console.log('sending image to Web FE')
    io.emit('show image', msg);
  });
});


server.listen(port, () => {
  console.log("server starting on port : " + port)
});
