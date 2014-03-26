
var validation = require("../models/registrationValidation.js");

exports.registerJsonValidationTest = function(test) {
    json = {
        "registrationId": "0",
        "availableSensors": [
            "gps", "network", "temperature"
        ]
    }

    test.expect(1);
    test.equals(true, validation.validateRegisterRequest(json));
    test.done();
};

exports.deregisterJsonValidationTest = function(test) {
    json = {
        "registrationId": "0"
    }

    test.expect(1);
    test.equals(true, validation.validateDeregisterRequest(json));
    test.done();
};
