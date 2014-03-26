
var mysql = require("mysql");
var validator = require("../models/registrationValidation.js");

exports.register = function(req, res, next) {
    if (validator.validateRegisterRequest(req.body)) {
        var registrationId = req.body.registrationId;

    } else {
        res.status(400);
    }
    res.end();
};

exports.deregister = function(req, res, next) {
    if (validator.validateDeregisterRequest(req.body)) {
        // remove id and sensors from db
    } else {
        res.status(400);
    }
    res.end();
};
