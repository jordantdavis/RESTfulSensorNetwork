var plotly = require('plotly')('gr8kamon','11vum83slu');
var http = require('http');

//http.createServer(function (request, response) {
var data = [
	{
		x:[0,1,7], 
		y:[3,2,1],
		"type" : "scatter"
	}, 
	{
		x:[4,5,6],
		y:[9,7,8],
		"type" : "scatter"
	}
];

var layout = {
	"fileopt" : "overwrite", 
	"filename" : "Line Example Seven",
	"layout" : {
		"title" : "THis thing",
		"xaxis" : {"title" : "Time"},
		"yaxis" : {"title" : "Variables"}
	},
	"world_readable" : true
};

plotly.plot(data, layout, function (err, msg) {
http.createServer(function(request, response) {  
  var message = msg.url

  response.writeHead(301, {Location: message});  
  //response.write();  
  response.end();
  //var message = msg.url;
  
  //var message = $.parseJSON(msg);
  //window.location.replace("message");
}).listen(7896);
console.log('Server is listening to 7986');
});

//}).listen(7986);
//console.log("Listening on 7896");
