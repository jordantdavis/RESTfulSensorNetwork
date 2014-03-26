
var restify = require("restify");

var registration = require("./controllers/registrationHandlers.js");

var server = restify.createServer();
server.use(restify.bodyParser());
server.post("/register", registration.register);
server.post("/deregister", registration.deregister);

server.listen(9263, function() {
    console.log("Server started on port 9263.");
});
