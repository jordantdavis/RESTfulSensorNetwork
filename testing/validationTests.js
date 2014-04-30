
var deviceModel = require("./../models/deviceModel.js");
var schedModel = require("./../models/scheduleModel.js");
var sensorDataModel = require("./../models/sensorDataModel.js");

exports.registerJsonValidationTest = function(test) {
    var json = {
        "registrationId": "0",
        "availableSensors": [
            "location", "temperature"
        ]
    };

    test.expect(1);
    test.equals(true, deviceModel.validateRegisterRequest(json));
    test.done();
}

exports.unregisterJsonValidationTest = function(test) {
    var json = {
        "registrationId": "0"
    };

    test.expect(1);
    test.equals(true, deviceModel.validateUnregisterRequest(json));
    test.done();
}

exports.scheduleJsonValidationTest = function(test) {
    var json = {
        "schedules": [
            {
                "sensorName": "location",
                "startTime": 1396454392,
                "endTime": 1396454692,
                "frequency": 1
            },
            {
                "sensorName": "pressure",
                "startTime": 1396454392,
                "endTime": 1396454692,
                "frequency": 1
            }
        ]
    };

    test.expect(1);
    test.equals(true, schedModel.validateScheduleCreateRequest(json));
    test.done();
}

exports.sensorSampleUploadValidationTest = function(test) {
    var json = {
        "registrationId": "0",
        "samples": [
            {
                "sensorName": "accelerometerZ",
                "timestamp": 1396454692,
                "sensorValue": -1.0
            },
            {
                "sensorName": "temperature",
                "timestamp": 1396454692,
                "sensorValue": 75
            }
        ]
    };

    test.expect(1);
    test.equals(true, sensorDataModel.validateSampleUploadRequest(json));
    test.done();
}

exports.sensorSampleDownloadValidationTest = function(test) {
    var json = {
        "sensorName": "magnetometerX",
        "startTime": 1396454392,
        "endTime": 1396454692,
        "shortIds": ["0", "1", "2"]
    };

    test.expect(1);
    test.equals(true, sensorDataModel.validateSampleDownloadRequest(json));
    test.done();
}
