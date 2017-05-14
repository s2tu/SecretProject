var server = require('http').createServer();
var io = require('socket.io')(server);
console.log("Server Started");
io.on('connection', function(client){
  client.on('event', function(data){});
  client.on('disconnect', function(){});
  client.on('echo', function(data) {
  	console.log("echo back");
    client.emit('echo back', "I am server.  Client ID:" + client.id );
  });

});




var port = 3001;
console.log("Listening on port: " + port);
server.listen(port);


