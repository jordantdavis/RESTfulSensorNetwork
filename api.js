
var restify = require("restify");

var handlers = require("./controllers/handlers.js");

var server = restify.createServer();
server.use(restify.bodyParser());
server.post("/device/register", handlers.deviceRegister);
server.post("/device/unregister", handlers.deviceUnregister);
server.post("/schedule", handlers.schedule);

server.listen(9263, function() {
    console.log("Server started on port 9263.");
});
