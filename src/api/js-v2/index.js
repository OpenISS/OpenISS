var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var port = process.env.PORT || 3000;

app.get('/', function(req, res){
  res.sendFile(__dirname + '/frontend.html');
});

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

http.listen(port, function(){
  console.log('listening on *:' + port);
});