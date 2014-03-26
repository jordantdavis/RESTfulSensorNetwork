CREATE TABLE Devices (
    shortId INT NOT NULL AUTO_INCREMENT,
    registrationId CHAR(4096) NOT NULL,
    PRIMARY KEY(shortId)
);

CREATE TABLE AvailableSensors (
    shortId INT NOT NULL,
    sensorName CHAR(50) NOT NULL
    PRIMARY KEY(shortId, sensorName)
);
