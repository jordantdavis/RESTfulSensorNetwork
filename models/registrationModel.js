
var jsv = require("JSV").JSV;
var mysql = require("mysql");

function validateRegisterRequest(json) {
    var schema = {
        "type": "object",
        "properties": {
            "registrationId": {
                "type": "string",
                "required": true
            },
            "availableSensors": {
                "type": "array",
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
    }

    var env = jsv.createEnvironment();
    var report = env.validate(json, schema);

    if (report.errors.length !== 0) {
        return false;
    }

    return true;
}

function validateDeregisterRequest(json) {
    var schema = {
        "type": "object",
        "properties": {
            "registrationId": {
                "type": "string",
                "required": true
            }
        },
        "additionalProperties": false
    }

    var env = jsv.createEnvironment();
    var report = env.validate(json, schema);

    if (report.errors.length === 0) {
        return true;
    } else {
        return false;
    }
}

function openConnection() {
    // var connection = mysql.createConnection({
    //     "host": "localhost",
    //     "user": "COMP4302jdavis17",
    //     "pass": "YTM4NTM4YWNmYzA4Y2Nm",
    //     "database": "COMP4302jdavis17"
    // });

    var connection = mysql.createConnection({
        "host": "localhost",
        "user": "root",
        "password": "password",
        "database": "RSN"
    });

    connection.connect(function(err) {
        if (err) {
            console.log(err);
            return null;
        }
    });

    return connection;
}

function closeConnection(connection) {
    if (connection) {
        connection.end(function(err) {
            if (err) console.log(err);
        });
    }
}

function insertDeviceAndSensors(registrationId, availableSensors) {
    var connection = openConnection();

    if (connection) {
        var deviceInsertStatement = "INSERT INTO Devices (registrationId) VALUES (?);";

        connection.query(deviceInsertStatement, [registrationId], function(err, rows) {
            if (err) console.log(err);

            var shortId = rows.insertId;

            var sensorInsertStatement = "INSERT INTO AvailableSensors (shortId, sensorName) VALUES (?, ?);";

            for (var i in availableSensors) {
                connection.query(sensorInsertStatement, [shortId, availableSensors[i]], function(err, rows) {
                    if (err) console.log(err);
                });
            }

            closeConnection(connection);
        });
    }
}

function removeDeviceAndSensors(registrationId) {
    var connection = openConnection();

    if (connection) {
        var deviceSelectShortIdStatement = "SELECT shortId FROM Devices WHERE registrationId = ?;";

        connection.query(deviceSelectShortIdStatement, [registrationId], function(err, rows) {
            if (err) console.log(err);

            var shortId = rows[0]["shortId"];

            var deviceDeleteStatement = "DELETE FROM Devices WHERE shortId = ?;";

            connection.query(deviceDeleteStatement, [shortId], function(err, rows) {
                if (err) console.log(err);
            });

            var sensorDeleteStatement = "DELETE FROM AvailableSensors WHERE shortId = ?;";

            connection.query(sensorDeleteStatement, [shortId], function(err, rows) {
                if (err) console.log(err);
            });

            closeConnection(connection);
        });
    }
}

exports.validateRegisterRequest = validateRegisterRequest;
exports.validateDeregisterRequest = validateDeregisterRequest;
exports.insertDeviceAndSensors = insertDeviceAndSensors;
exports.removeDeviceAndSensors = removeDeviceAndSensors;
