<?php

    namespace Model;

    define("HOST", "127.0.0.1");
    define("USER", "root");
    define("PASS", "password");
    define("DB", "RSN");

    function insertNewDevice($uuid, $addr, $port) {
        $cxn = new \mysqli(HOST, USER, PASS, DB);

        if (mysqli_connect_errno()) {
            return false;
        }

        $connected = 0;

        $stmt = $cxn->prepare("INSERT INTO Devices VALUES (?, ?, ?, ?);");
        $stmt->bind_param("sssi", $uuid, $addr, $port, $connected);
        $stmt->execute();
        $stmt->close();
        $cxn->close();

        return true;
    }

    function insertEnvironmentalSensors($uuid, $sensors) {
        $environmentalSensors = array("humidity", "light", "pressure", "temperature");
        $isAvailable = array();

        foreach ($environmentalSensors as $sensor) {
            if (in_array($sensor, $sensors)) {
                array_push($isAvailable, true);
            } else {
                array_push($isAvailable, false);
            }
        }

        $humidity = $isAvailable[0];
        $light = $isAvailable[1];
        $pressure = $isAvailable[2];
        $temperature = $isAvailable[3];

        $cxn = new \mysqli(HOST, USER, PASS, DB);

        if (mysqli_connect_errno()) {
            return false;
        }

        $stmt = $cxn->prepare("INSERT INTO EnvironmentalSensors VALUES (?, ?, ?, ?, ?);");
        $stmt->bind_param("siiii", $uuid, $humidity, $light, $pressure, $temperature);
        $stmt->execute();
        $stmt->close();
        $cxn->close();

        return true;
    }

    function insertMotionSensors($uuid, $sensors) {
        $motionSensors = array("accelerometer", "gravity", "gyroscope");
        $isAvailable = array();

        foreach ($motionSensors as $sensor) {
            if (in_array($sensor, $sensors)) {
                array_push($isAvailable, true);
            } else {
                array_push($isAvailable, false);
            }
        }

        $accelerometer = $isAvailable[0];
        $gravity = $isAvailable[1];
        $gyroscope = $isAvailable[2];

        $cxn = new \mysqli(HOST, USER, PASS, DB);

        if (mysqli_connect_errno()) {
            return false;
        }

        $stmt = $cxn->prepare("INSERT INTO MotionSensors VALUES (?, ?, ?, ?);");
        $stmt->bind_param("siii", $uuid, $accelerometer, $gravity, $gyroscope);
        $stmt->execute();
        $stmt->close();
        $cxn->close();

        return true;
    }

    function insertPositionSensors($uuid, $sensors) {
        $positionSensors = array("gps", "magnetometer", "proximity");
        $isAvailable = array();

        foreach ($positionSensors as $sensor) {
            if (in_array($sensor, $sensors)) {
                array_push($isAvailable, true);
            } else {
                array_push($isAvailable, false);
            }
        }

        $gps = $isAvailable[0];
        $magnetometer = $isAvailable[1];
        $proximity = $isAvailable[2];

        $cxn = new \mysqli(HOST, USER, PASS, DB);

        if (mysqli_connect_errno()) {
            return false;
        }

        $stmt = $cxn->prepare("INSERT INTO PositionSensors VALUES (?, ?, ?, ?);");
        $stmt->bind_param("siii", $uuid, $gps, $magnetometer, $proximity);
        $stmt->execute();
        $stmt->close();
        $cxn->close();

        return true;
    }

    function updateConnectionStatus($uuid, $status) {
        $cxn = new \mysqli(HOST, USER, PASS, DB);

        if (mysqli_connect_errno()) {
            return false;
        }

        $stmt = $cxn->prepare("UPDATE Devices SET connected = ? WHERE id = ?;");
        $stmt->bind_param("is", $status, $uuid);
        $stmt->execute();
        $stmt->close();
        $cxn->close();

        return true;
    }

    function updateConnectionAddressPort($uuid, $addr, $port) {
        $cxn = new \mysqli(HOST, USER, PASS, DB);

        if (mysqli_connect_errno()) {
            return false;
        }

        $stmt = $cxn->prepare("UPDATE Devices SET addr = ?, port = ? WHERE id = ?;");
        $stmt->bind_param("sss", $addr, $port, $uuid);
        $stmt->execute();
        $stmt->close();
        $cxn->close();

        return true;
    }

?>
