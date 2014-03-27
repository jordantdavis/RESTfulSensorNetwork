
var restify = require("restify");

var regHandlers = require("./controllers/registrationHandlers.js");

var server = restify.createServer();
server.use(restify.bodyParser());
server.post("/register", regHandlers.registerHandler);
server.post("/deregister", regHandlers.deregisterHandler);

server.listen(9263, function() {
    console.log("Server started on port 9263.");
});
