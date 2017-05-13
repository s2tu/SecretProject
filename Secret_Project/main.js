
function activate_command(){
	var command = $("#commands").val();
	var value = $("#commandvalue").val();
	var toyid = $("#toyid").html();
	var localip = $("#localip").html();
	var port = $("#port").html();
	var url = "";

	if(value){
		url = "http://" + localip+":" + port + "/"+ command +"?v="+ value + "&t=" + toyid;
	}else{
		url = "http://" + localip+":" + port + "/"+ command +"?v="+ 0 + "&t=" + toyid;
	}
	if(command == "GetToys"){
		url = "http://" + localip+":" + port + "/"+ command
	}
	console.log(url);
	$.ajax({
	    type : "get",
	    url : url,
	    dataType : "jsonp",
	    success : function(data){
	    	console.log("Sending Command: " + command + " Value: " + value);
	    	console.log(data);
	        $("#output").html(data);
	    },
	    error:function(e){
	        console.log(e);
	    }
	});	

}

function handlers(){
	var toyid = $("#toyid").html();
	var localip = $("#localip").html();
	var port = $("#port").html();


	$("#activate_commands").click(function(){
		activate_command();
	});
}



function init(){
	$.ajax({
		    type : "get",
		    url : "https://lovense.com/api/getLanToys",
		    dataType : "jsonp",
		    success : function(data){
		        var json_data = JSON.parse(data);
		        console.log(json_data);
		        var localip = Object.keys(json_data)[0];
		        var port = json_data[localip]["port"]
		        var toyid = Object.keys(json_data[localip]["toys"])[0];;
		        $("#toyid").html(toyid);
		        $("#localip").html(localip);
		        $("#port").html(port);
		        handlers();
		        
		    },
		    error:function(e){
		        console.error(e);
		    }
	});

}
$(function(){
	init();
});