
var regModel = require("../models/registrationModel.js");

exports.registerJsonValidationTest = function(test) {
    json = {
        "registrationId": "0",
        "availableSensors": [
            "location", "temperature"
        ]
    }

    test.expect(1);
    test.equals(true, regModel.validateRegisterRequest(json));
    test.done();
};

exports.unregisterJsonValidationTest = function(test) {
    json = {
        "registrationId": "0"
    }

    test.expect(1);
    test.equals(true, regModel.validateUnregisterRequest(json));
    test.done();
};
