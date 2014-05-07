
var http = require('http');
var util = require('util');
var qs = require('querystring');
var plotly = require('plotly')('gr8kamon','11vum83slu');

http.createServer(function (req, res) {
//console.log(JSON.stringify(req["body"]));
    var body = "";   
    var store = ""; 

    res.writeHead(200, { 'Content-Type': 'text/plain' });
    req.on('data', function (chunk) {
        console.log("Got it");
	body += chunk;
    });
    
    
    req.on('end', function() {
	var stuff = qs.parse(body);
	var array = [];
	
	for(samples in stuff){
    		array.push(stuff[samples]);
	}
	array.shift();
	array.shift();
	
	var id = [];
	var sensor = [];
	var time = [];
	var sample = [];

	for (var i = 0; i <= array.length; i++)
	{
		id.push(array.shift());
		sensor.push(array.shift());
		time.push(parseInt(array.shift()));
		sample.push(parseFloat(array.shift()));
	}

	console.log(id);
	console.log(sensor);
	console.log(time);
	console.log(sample);	
    

	var sensorName = sensor.shift();

	var data = [
	{
		"name": sensorName,
		x:time, 
		y:sample,
		"type" : "scatter"
	}
	];

	var layout = {
	"fileopt" : "overwrite", 
	"filename" : sensorName,
	"layout" : {
		"title" : sensorName,
		"xaxis" : {"title" : "Time"},
		"yaxis" : {"title" : "Samples"}
	},
	"world_readable" : true
	};

	plotly.plot(data, layout, function (err, msg) {  
  	var message = msg.url

  	//res.writeHead(301, {Location: message});   
 
  	res.write("Hey");
	res.end();
  	//var message = $.parseJSON(msg);
  	//window.location.replace("message");

	});
    	
    });
}).listen(9085);
console.log("Server listening on 9085");
