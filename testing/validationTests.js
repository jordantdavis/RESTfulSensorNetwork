
var deviceModel = require("./../models/deviceModel.js");
var schedModel = require("./../models/scheduleModel.js");

exports.registerJsonValidationTest = function(test) {
    json = {
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
    json = {
        "registrationId": "0"
    };

    test.expect(1);
    test.equals(true, deviceModel.validateUnregisterRequest(json));
    test.done();
}

exports.scheduleJsonValidationTest = function(test) {
    json = {
        "requireAllSensors": true,
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
    test.equals(true, schedModel.validateScheduleRequest(json));
    test.done();
}
