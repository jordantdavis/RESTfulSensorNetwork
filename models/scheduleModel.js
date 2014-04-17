
var async = require("async");
var jsv = require("JSV").JSV;
var mysql = require("mysql");
var gcm = require("node-gcm");

var db = require("./dbConfig.js");

module.exports = {
    /*
    *    Checks if incoming schedule request body is in the correct format.
    *    @param {json} json - Body of the schedule request.
    */
    validateScheduleCreateRequest: function(json) {
        var schema = {
            "type": "object",
            "properties": {
                "schedules": {
                    "type": "array",
                    "uniqueItems": true,
                    "items": {
                        "type": "object",
                        "properties": {
                            "sensorName": {
                                "type": "string",
                                "enum": [
                                    "accelerometer", "gyroscope", "humidity", "light", "location",
                                    "magnetometer", "pressure", "proximity", "temperature"
                                ],
                                "required": true
                            },
                            "startTime": {
                                "type": "int",
                                "required": true
                            },
                            "endTime": {
                                "type": "int",
                                "required": true
                            },
                            "frequency": {
                                "type": "number",
                                "required": true
                            }
                        }
                    },
                    "required": true
                }
            },
            "additionalProperties": false
        };

        var env = jsv.createEnvironment();
        var report = env.validate(json, schema);

        if (report.errors.length !== 0) {
            return false;
        }

        return true;
    },

    /*
    *    Gets registration IDs of devices with a given sensor.
    *    @param {string} sensorName - Name of a sensor.
    *    @param {function} callback - Callback used to obtain the result of the query.
    */
    getDevicesWithSensor: function(sensorName, callback) {
        async.waterfall([
            // task 1 - open database connection
            function(callback) {
                db.openConnection(callback);
            },
            // task 2 - query for registration IDs of devices with given sensor and destroy database connection
            function(connection, callback) {
                var statement = "SELECT registrationId FROM Devices INNER JOIN AvailableSensors ON " +
                    "Devices.shortId = AvailableSensors.shortId WHERE sensorName = ? AND isOnline = true;";
                connection.query(statement, [sensorName], function(err, rows) {
                    db.closeConnection(connection);
                    if (err) {
                        console.log(err);
                        callback(err, null);
                    } else {
                        var registrationIds = [];
                        for (var i in rows) {
                            registrationIds.push(rows[i]["registrationId"]);
                        }
                        callback(null, registrationIds);
                    }
                });
            }
        ], function(err, registrationIds) {
            if (err) {
                console.log(err);
                callback(err, null);
            } else {
                callback(null, registrationIds);
            }
        });
    },

    getAllApplicableDevices: function(schedule, callback) {
        async.map(schedule, function(schedItem, callback) {
            module.exports.getDevicesWithSensor(schedItem["sensorName"], callback);
        }, function(err, registrationIds) {
            if (err) {
                callback(err, null);
            } else {
                callback(null, registrationIds);
            }
        });
    },

    insertSchedules: function(schedules, callback) {
        var statement = "INSERT INTO Schedules (sensorName, startTime, endTime, frequency) VALUES ?;";
        var valuesClause = [];

        for (var i in schedules) {
            valuesClause.push([schedules[i]["sensorName"], schedules[i]["startTime"], schedules[i]["endTime"], schedules[i]["frequency"]]);
        }

        async.waterfall([
            function(callback) {
                db.openConnection(callback);
            },
            function(connection, callback) {
                connection.query(statement, [valuesClause], function(err, rows) {
                    db.closeConnection(connection);
                    if (err) {
                        console.log(err);
                        callback(err);
                    } else {
                        callback(null);
                    }
                });
            }
        ], function(err) {
            if (err) {
                console.log(err);
                callback(err);
            } else {
                callback(null);
            }
        });
    },

    sendSchedules: function(registrationIds, schedules, callback) {
        var sensorLevelSchedules = [];

        for (var i = 0; i < registrationIds.length; i++) {
            if (registrationIds[i].length != 0) {
                sensorLevelSchedules[i] = {
                    "registrationIds": registrationIds[i],
                    "schedule": schedules[i]
                };
            }
        }

        var sender = new gcm.Sender("AIzaSyCqGpnqbfdsyr9OhQ4_5bmlDukBo3XSEik");

        async.each(sensorLevelSchedules, function(messageItems, callback) {
            var message = new gcm.Message({
                // timeToLive: 30,
                data: {
                    "schedule": messageItems["schedule"]
                }
            });

            sender.sendNoRetry(message, messageItems["registrationIds"], function(err, result) {
                if (err) {
                    callback(err);
                } else {
                    callback(null);
                }
            });
        }, function(err) {
            if (err) {
                callback(err);
            } else {
                callback(null);
            }
        });
    }
};
