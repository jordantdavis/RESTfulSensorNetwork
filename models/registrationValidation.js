
var jsv = require("JSV").JSV;
var fs = require("fs");

var ALL_SENSORS = ["accelerometer", "temperature", "gyroscope", "light", "magnetometer", "pressure",
    "proximity", "humidity", "gps", "network"];

exports.validateRegisterRequest = function(json) {
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
                        "accelerometer", "temperature", "gyroscope", "light", "magnetometer", "pressure",
                        "proximity", "humidity", "gps", "network"
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

    // for (var i in json.availableSensors) {
    //     if (ALL_SENSORS.indexOf(json.availableSensors[i].toLowerCase()) == -1) {
    //         return false;
    //     }
    // }

    return true;
}

exports.validateDeregisterRequest = function(json) {
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
