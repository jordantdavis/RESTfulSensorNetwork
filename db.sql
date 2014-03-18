CREATE TABLE Devices (
    id CHAR(32) NOT NULL,
    ipAddr CHAR(15) NOT NULL,
    connected BOOLEAN NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE EnvironmentalSensors (
    id CHAR(32) NOT NULL,
    humidity BOOLEAN NOT NULL,
    light BOOLEAN NOT NULL,
    pressure BOOLEAN NOT NULL,
    temp BOOLEAN NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE PositionSensors (
    id CHAR(32) NOT NULL,
    gps BOOLEAN NOT NULL,
    magnetic BOOLEAN NOT NULL,
    orientation BOOLEAN NOT NULL,
    proximity BOOLEAN NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE MotionSensors (
    id CHAR(32) NOT NULL,
    accel BOOLEAN NOT NULL,
    grav BOOLEAN NOT NULL,
    gyro BOOLEAN NOT NULL,
    PRIMARY KEY(id)
);
