CREATE TABLE Devices (
    uuid CHAR(32) NOT NULL,
    addr CHAR(15) NOT NULL,
    port CHAR(5) NOT NULL,
    connected BOOLEAN NOT NULL,
    PRIMARY KEY(uuid)
);

CREATE TABLE Sensors (
    id INT(11) NOT NULL AUTO_INCREMENT,
    uuid CHAR(32) NOT NULL,
    name CHAR(50) NOT NULL,
    PRIMARY KEY(id)
);
