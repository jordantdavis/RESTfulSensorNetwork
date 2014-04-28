CREATE TABLE Devices (
    shortId INTEGER NOT NULL AUTO_INCREMENT,
    registrationId TEXT NOT NULL,
    isOnline BOOLEAN NOT NULL,
    PRIMARY KEY(shortId)
);

CREATE TABLE AvailableSensors (
    shortId INTEGER NOT NULL,
    sensorName CHAR(50) NOT NULL,
    PRIMARY KEY(shortId, sensorName)
);

CREATE TABLE Schedules (
    id INTEGER NOT NULL AUTO_INCREMENT,
    sensorName VARCHAR(50) NOT NULL,
    startTime INTEGER NOT NULL,
    endTime INTEGER NOT NULL,
    frequency FLOAT NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE SensorSamples (
    shortId INTEGER NOT NULL,
    sensorName VARCHAR(50) NOT NULL,
    timestamp INTEGER NOT NULL,
    sampleValue INTEGER NOT NULL,
    PRIMARY KEY(shortId, sensorName, timestamp);
);
