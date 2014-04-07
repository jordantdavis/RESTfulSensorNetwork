
var async = require("async");
var restify = require("restify");

var deviceModel = require("./../models/deviceModel.js");
var schedModel = require("./../models/scheduleModel.js");

module.exports = {
    /*
    *    Handles incoming registration requests.
    *    @param {http.IncomingMessage} request - Incoming request.
    *    @param {http.ServerResponse} response - Outgoing response.
    *    @param {function} next - Runs error handlers.
    */
    deviceRegister: function(request, response, next) {
        // if the request body is valid, continue
        if (deviceModel.validateRegisterRequest(request["body"])) {
            // extract request data
            var registrationId = request["body"]["registrationId"];
            var availableSensors = request["body"]["availableSensors"];

            async.waterfall([
                // task 1 - check to see if registration ID is already in database
                function(callback) {
                    deviceModel.isRegistered(registrationId, callback);
                },
                // task 2 - insert device and sensors if not in database
                function(idExists, callback) {
                    // if the registration ID already exists, throw an HTTP 400 - Bad Request
                    if (idExists) {
                        next(new restify.BadRequestError("The supplied registration ID already exists."));
                    }
                    // otherwise, insert the new device and sensors into the database
                    else {
                        deviceModel.insertNewDeviceAndSensors(registrationId, availableSensors, callback);
                    }
                }
            ],
            // main callback
            function(err) {
                // if a database error occurred, throw an HTTP 500 - Internal Error
                if (err) {
                    next(new restify.InternalError("An error occurred on the server."));
                }
                // otherwise, the registration was successful, send an HTTP 200 - OK
                else {
                    response.send({
                        "code": "OK",
                        "message": "Registration successful."
                    });
                    next();
                }
            });
        }
        // if the request body is not valid, throw an HTTP 400 - Bad Request
        else {
            next(new restify.BadRequestError("Invalid registration request body format."));
        }
    },

    /*
    *    Handles incoming unregistration requests.
    *    @param {http.IncomingMessage} request - Incoming request.
    *    @param {http.ServerResponse} response - Outgoing response.
    *    @param {function} next - Runs error handlers.
    */
    deviceUnregister: function(request, response, next) {
        // if the request body is valid, continue
        if (deviceModel.validateUnregisterRequest(request["body"])) {
            // extract request data
            var registrationId = request["body"]["registrationId"];

            async.waterfall([
                // task 1 - check to see if registration ID is already in database
                function(callback) {
                    deviceModel.isRegistered(registrationId, callback);
                },
                // task 2 - remove device and sensors if in database
                function(idExists, callback) {
                    // if the registration exists, remove the device and sensors
                    if (idExists) {
                        deviceModel.removeDeviceAndSensors(registrationId, callback);
                    }
                    // otherwise, throw an HTTP 400 - Bad Request
                    else {
                        next(new restify.BadRequestError("The supplied registration ID does not exist."));
                    }
                }
            ],
            // main callback
            function(err) {
                // if a database error occurred, throw an HTTP 500 - Internal Error
                if (err) {
                    next(new restify.InternalError("An error occurred on the server."));
                }
                // otherwise, the removal was successful, send an HTTP 200 - OK
                else {
                    response.send({
                        "code": "OK",
                        "message": "Unregistration successful."
                    });
                }
            });
        }
        // if the request body is not valid, throw an HTTP 400 - Bad Request
        else {
            next(new restify.BadRequestError("Invalid unregistration request body format."))
        }
    },

    /*
    *    Handles incoming schedule creation request.
    *    @param {http.IncomingMessage} request - Incoming request.
    *    @param {http.ServerResponse} response - Outgoing response.
    *    @param {function} next - Runs error handlers.
    */
    scheduleCreate: function(request, response, next) {
        // if the request body is valid, continue
        if (schedModel.validateScheduleRequest(request["body"])) {
            // extract request data
            var requireAllSensors = request["body"]["requireAllSensors"];
            var schedules = request["body"]["schedules"];

            async.waterfall([
                // task #1 - get device ids for devices with given sensors
                function(callback) {
                    schedModel.getAllApplicableDevices(requireAllSensors, schedules, callback);
                },
                // task #2 - get schedules to appropriate devices
                function(registrationIds, callback) {
                    schedModel.sendSchedules(registrationIds, schedules, callback);
                },
                // task #3 - log schedules in database
                function(callback) {
                    schedModel.insertSchedules(schedules, callback);
                }
            ],
            // main callback
            function(err) {
                // if a database error occurred, throw an HTTP 500 - Internal Error
                if (err) {
                    next(new restify.InternalError("An error occurred on the server."));
                }
                // otherwise, the scheduling was successful, send an HTTP 200 - OK
                else {
                    response.send({
                        "code": "OK",
                        "message": "Scheduling successful."
                    });
                    next();
                }
            });
        }
        // if the request body is not valid, throw an HTTP 400 - Bad Request
        else {
            next(new restify.BadRequestError("Invalid schedule request body format."));
        }
    }
};
