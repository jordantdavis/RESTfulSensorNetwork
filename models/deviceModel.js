
var async = require("async");
var jsv = require("JSV").JSV;
var mysql = require("mysql");

var db = require("./dbConfig.js");

module.exports = {
    /*
    *    Checks if incoming registration request body is in the correct format.
    *    @param {json} json - Body of the registration request.
    */
    validateRegisterRequest: function(json) {
        // structure of proper request
        var schema = {
            "type": "object",
            "properties": {
                "registrationId": {
                    "type": "string",
                    "required": true
                },
                "availableSensors": {
                    "type": "array",
                    "uniqueItems": true,
                    "items": {
                        "type": "string",
                        "enum": [
                            "accelerometer", "gyroscope", "humidity", "light", "location",
                            "magnetometer", "pressure", "proximity", "temperature"
                        ]
                    },
                    "required": true
                }
            },
            "additionalProperties": false
        };

        // initialize and compare
        var env = jsv.createEnvironment();
        var report = env.validate(json, schema);

        // if there are errors to report, the request format is invalid
        if (report.errors.length !== 0) {
            return false;
        }

        return true;
    },

    /*
    *    Checks if incoming unregistration request body is in the correct format.
    *    @param {json} json - Body of the unregistration request.
    */
    validateUnregisterRequest: function(json) {
        // structure of proper request
        var schema = {
            "type": "object",
            "properties": {
                "registrationId": {
                    "type": "string",
                    "required": true
                }
            },
            "additionalProperties": false
        };

        // initialize and compare
        var env = jsv.createEnvironment();
        var report = env.validate(json, schema);

        // if there are errors to report, the request format is invalid
        if (report.errors.length !== 0) {
            return false;
        }

        return true;
    },

    /*
    *    Checks to see if a registration ID is already in the database.
    *    @param {string} registrationId - GCM supplied registration ID.
    *    @param {function} callback - Callback used to obtain the result of the query.
    */
    isRegistered: function(registrationId, callback) {
        async.waterfall([
            // task 1 - open database connection
            function(callback) {
                db.openConnection(callback);
            },
            // task 2 - query for count of rows with specified registration ID and destroy database connection
            function(connection, callback) {
                var statement = "SELECT COUNT(*) AS count FROM Devices WHERE registrationId = ?;"
                connection.query(statement, [registrationId], function(err, rows) {
                    db.closeConnection(connection);
                    if (err) {
                        console.log(err);
                        // pass the database error to the main callback
                        callback(err, false);
                    } else {
                        // pass the existence of the ID to the main callback
                        callback(null, rows[0]["count"] === 1);
                    }
                });
            },
        ],
        // main callback
        function(err, idExists) {
            if (err) {
                // pass error back to the caller
                callback(err, null);
            } else {
                // pass the existence of the ID back to the caller
                callback(null, idExists);
            }
        });
    },

    /*
    *    Inserts the registration ID and available sensors of a new device.
    *    @param {string} registrationId - GCM supplied device ID.
    *    @param {array} availableSensors - Names of the sensors on the device.
    *    @param {function} callback - Callback used to obtain the success or failure of the inserts.
    */
    insertNewDeviceAndSensors: function(registrationId, availableSensors, callback) {
        async.waterfall([
            // task 1 - open database connection
            function(callback) {
                db.openConnection(callback);
            },
            // task 2 - insert registration ID into database
            function(connection, callback) {
                var statement = "INSERT INTO Devices (registrationId, isOnline) VALUES (?, true);";
                connection.query(statement, [registrationId], function(err, rows) {
                    if (err) {
                        console.log(err);
                        db.closeConnection(connection);
                        // pass the database error to the main callback
                        callback(err, null, null);
                    } else {
                        // pass the insertId (auto increment id) to task 3
                        callback(null, connection, rows["insertId"]);
                    }
                });
            },
            // task 3 - insert sensor names into database
            function(connection, shortId, callback) {
                var statement = "INSERT INTO AvailableSensors (shortId, sensorName) VALUES ?;";
                // 2d array representing data for the VALUES clause
                var valuesClause = [];
                // build VALUES clause array
                for (var i in availableSensors) {
                    valuesClause.push([shortId, availableSensors[i]]);
                }

                connection.query(statement, [valuesClause], function(err, rows) {
                    db.closeConnection(connection);
                    if (err) {
                        console.log(err);
                        // pass the database error to the main callback
                        callback(err);
                    } else {
                        // pass nothing to the main callback
                        callback(null);
                    }
                });
            }
        ],
        // main callback
        function(err) {
            if (err) {
                // pass error back to the caller (failure)
                callback(err);
            } else {
                // pass nothing back to the caller (success)
                callback(null);
            }
        });
    },

    /*
    *    Removes the registration ID and available sensors of an existing device.
    *    @param {string} registrationId - GCM supplied device ID.
    *    @param {function} callback - Callback used to obtain the success or failure of the removals.
    */
    removeDeviceAndSensors: function(registrationId, callback) {
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
            // task 3 - delete rows from Devices table containing given short ID
            function(connection, shortId, callback) {
                var statement = "DELETE FROM Devices WHERE shortId = ?;";
                connection.query(statement, [shortId], function(err, rows) {
                    if (err) {
                        db.closeConnection(connection);
                        console.log(err);
                        // pass the database error back to the main callback
                        callback(err, null, null);
                    } else {
                        // pass the database connection and short ID to task 4
                        callback(null, connection, shortId);
                    }
                });
            },
            // task 4 - delete rows from AvailableSensors table containing given short ID
            function(connection, shortId, callback) {
                var statement = "DELETE FROM AvailableSensors WHERE shortId = ?;";
                connection.query(statement, [shortId], function (err, rows) {
                    db.closeConnection(connection);
                    if (err) {
                        console.log(err);
                        // pass the database error back to the main callback
                        callback(err);
                    } else {
                        // pass nothing back to the main callback
                        callback(null);
                    }
                });
            }
        ], function(err) {
            if (err) {
                // pass error back to the caller (failure)
                callback(err);
            } else {
                // pass nothing back to the caller (success)
                callback(null);
            }
        });
    }
};
