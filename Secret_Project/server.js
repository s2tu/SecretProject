

var express = require('express'),
    app = express(),
//    server = require('https').createServer(app),
    //server = app.listen(3001)
 
server = app.listen(process.env.PORT || 3001),
io = require('socket.io').listen(server);

    //io = require('socket.io')(server);

var users = {}
io.sockets.on('connection', function(client){
  client.on('event', function(data){});
  client.on('disconnect', function(){
  	console.log("client has disconnected" + client.id);
  	delete users[client.id]
  });

  client.on('echo', function(data) {
  	console.log("echo back");
  	var message=  "I am server. Client ID:" + client.id ;
    //finding user
    var userids = Object.keys(users); 
   
    if(userids.length == 2){
    	var user = ""
    	for(var i = 0; i <userids.length; i++){
    		if(userids[i] != client.id){1
    			user = userids[i]; 
    		}
    	}
    	console.log("sending to other user")
    	users[user].emit('echo back',data);
    }else if(userids.length < 2){
	    if(client.id in users){
	    		message = message + "  user already connected"
	    }else{
	    		message = message + " user added"
	    		users[client.id] = client;
	    }
	    client.emit('echo back', message);
	    console.log(message);    	
    }

    console.log(data);
    console.log(Object.keys(users));

    //you could scedule a job that runs every certain amount of time
    //pings the url client 
    //and give x amount of time to respond otherwise remove the client.
    
  });

});



var port = process.env.PORT || 3001;
console.log("Listening on port: " + port);
//server.listen(port);



