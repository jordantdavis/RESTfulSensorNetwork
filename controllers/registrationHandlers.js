
var regModel = require("../models/registrationModel.js");

function registerHandler(req, res, next) {
    if (regModel.validateRegisterRequest(req.body)) {
        var registrationId = req.body["registrationId"];
        var availableSensors = req.body["availableSensors"];
        regModel.insertDeviceAndSensors(registrationId, availableSensors);
    } else {
        res.status(400);
    }
    res.end();
};

function deregisterHandler(req, res, next) {
    if (regModel.validateDeregisterRequest(req.body)) {
        var registrationId = req.body["registrationId"];
        regModel.removeDeviceAndSensors(registrationId);
    } else {
        res.status(400);
    }
    res.end();
};

exports.registerHandler = registerHandler;
exports.deregisterHandler = deregisterHandler;
