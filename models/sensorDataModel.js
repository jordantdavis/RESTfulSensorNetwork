
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
                                    "gyroscopeY", "gyroscopeZ", "humidity", "light", "location",
                                    "magnetometerX", "magnetometerY", "magnetometerZ", "pressure", "proximity",
                                    "temperature"
                                ],
                                "required": true
                            },
                            "timestamp": {
                                "type": "int",
                                "required": true
                            },
                            "sensorValue": {
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
                "sensorName": {
                    "type": "string",
                    "enum": [
                        "accelerometerX", "accelerometerY", "accelerometerZ", "gyroscopeX",
                        "gyroscopeY", "gyroscopeZ", "humidity", "light", "location",
                        "magnetometerX", "magnetometerY", "magnetometerZ", "pressure", "proximity",
                        "temperature"
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
    }
}
