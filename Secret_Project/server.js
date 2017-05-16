var server = require('http').createServer();
var io = require('socket.io')(server);
var users = [];
console.log("Server Started");
io.on('connection', function(client){
  client.on('event', function(data){});
  client.on('disconnect', function(){});
  client.on('echo', function(data) {
  	console.log("echo back");
  	var message=  "I am server. Client ID:" + client.id ;
    
    if(users.indexOf(client.id) >= 0){
    		message = message + "  user already connected"
    }else{
    		message = message + " user added"
    		users.push(client.id);
    }


    client.emit('echo back',message);
  });

});




var port = 3001;
console.log("Listening on port: " + port);
server.listen(port);


