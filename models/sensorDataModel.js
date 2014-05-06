
var async = require("async");
var jsv = require("JSV").JSV;
var mysql = require("mysql");

var db = require("./dbConfig.js");

module.exports = {
    validateSampleUploadRequest: function(json) {
          var schema = {
            "type": "object",
            "properties": {
                "registrationId": {
                    "type": "string",
                    "required": true
                },
                "samples": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "sensorName": {
                                "type": "string",
                                "enum": [
                                    "accelerometerX", "accelerometerY", "accelerometerZ", "gyroscopeX",
                                    "gyroscopeY", "gyroscopeZ", "humidity", "light", "locationLat", "locationLng",
                                    "magnetometerX", "magnetometerY", "magnetometerZ", "pressure", "proximity",
                                    "temperature"
                                ],
                                "required": true
                            },
                            "timestamp": {
                                "type": "number",
                                "required": true
                            },
                            "sampleValue": {
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

         var schema = {
            "type": "object",
            "properties": {
                "registrationId": {
                    "type": "string",
                    "required": true
                },
                "samples": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "sensorName": {
                                "type": "string",
                                "enum": [
                                    "accelerometerX", "accelerometerY", "accelerometerZ", "gyroscopeX",
                                    "gyroscopeY", "gyroscopeZ", "humidity", "light", "locationLat", "locationLng",
                                    "magnetometerX", "magnetometerY", "magnetometerZ", "pressure", "proximity",
                                    "temperature"
                                ],
                                "required": true
                            },
                            "timestamp": {
                                "type": "int",
                                "required": true
                            },
                            "sampleValue": {
                                "type": "int",
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

    validateSampleDownloadRequest: function(json) {
        var schema = {
            "type": "object",
            "properties": {
                "sensorNames": {
                    "type": "array",
                    "uniqueItems": true,
                    "items": {
                        "type": "string",
                        "enum": [
                            "accelerometerX", "accelerometerY", "accelerometerZ", "gyroscopeX",
                            "gyroscopeY", "gyroscopeZ", "humidity", "light", "locationLat", "locationLng",
                            "magnetometerX", "magnetometerY", "magnetometerZ", "pressure", "proximity",
                            "temperature"
                        ]
                    },
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
                "shortIds": {
                    "type": "array",
                    "uniqueItems": true,
                    "items": {
                        "type": "string"
                    },
                    "required": false
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

    insertUploadedSamples: function(registrationId, samples, callback) {
        async.waterfall([
            // task 1 - open database connection
            function(callback) {
                db.openConnection(callback);
            },
            // task 2 - get short ID for specified registration ID from database
            function(connection, callback) {
                var statement = "SELECT shortId FROM Devices WHERE registrationId = ?;";
                connection.query(statement, [registrationId], function(err, rows) {
                    if (err) {
                        db.closeConnection(connection);
                        console.log(err);
                        // pass the database error back to the main callback
                        callback(err, null, null);
                    } else {
                        // pass the database connection and short ID to task 3
                        callback(null, connection, rows[0]["shortId"]);
                    }
                });
            },
            function(connection, shortId, callback) {
                var statement = "INSERT INTO SensorSamples (shortId, sensorName, timestamp, sampleValue) VALUES ?;";
                var valuesClause = [];

                for (var i = 0; i < samples.length; i++) {
                    valuesClause.push([shortId, samples[i]["sensorName"], samples[i]["timestamp"], samples[i]["sampleValue"]])
                }

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
                callback(err);
            } else {
                callback(null);
            }
        });
    },

    getSamples: function(sensorNames, startTime, endTime, shortIds, callback) {
        async.waterfall([
            function(callback) {
                db.openConnection(callback);
            },
            function(connection, callback) {
                var statement = "SELECT DISTINCT shortId, sensorName, timestamp, sampleValue FROM " +
                    "SensorSamples WHERE sensorName IN ? AND timestamp BETWEEN ? AND ?";
                if (shortIds) {
                    statement += " AND shortId IN ? ORDER BY timestamp;";
                    connection.query(statement, [sensorNames], [startTime], [endTime], [shortIds], function(err, rows) {
                        db.closeConnection(connection);

                        if (err) {
                            console.log(err);
                            callback(err, null, null);
                        } else {
                            callback(null, rows.length, rows);
                        }
                    });
                } else {
                    statement += " ORDER BY timestamp;";
                    connection.query(statement, [sensorNames], [startTime], [endTime], function(err, rows) {
                        db.closeConnection(connection);

                        if (err) {
                            console.log(err);
                            callback(err, null, null);
                        } else {
                            callback(null, rows.length, rows);
                        }
                    });
                }
            }
        ], function(err, numSamples, samples) {
            if (err) {
                callback(err, null, null);
            } else {
                callback(null, numSamples, samples);
            }
        });
    }
}
