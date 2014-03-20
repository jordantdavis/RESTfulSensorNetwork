<?php

    namespace Model;

    define("HOST", "127.0.0.1");
    define("USER", "COMP4302jdavis17");
    define("PASS", "YTM4NTM4YWNmYzA4Y2Nm");
    define("DB", "COMP4302jdavis17");

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

    function insertSensors($uuid, $deviceSensors) {
        $allSensors = array("accelerometer", "gps", "gravity", "gyroscope", "humidity",
        "light", "magnetometer", "pressure", "proximity", "temperature");

        $cxn = new \mysqli(HOST, USER, PASS, DB);

        if (mysqli_connect_errno()) {
            return false;
        }

        $stmt = $cxn->prepare("INSERT INTO Sensors (uuid, name) VALUES (?, ?);");
        $stmt->bind_param("ss", $uuid, $sensor);

        foreach ($allSensors as $sensor) {
            if (in_array($sensor, $deviceSensors)) {
                $stmt->execute();
            }
        }

        $stmt->close();
        $cxn->close();

        return true;
    }

    function updateConnectionStatus($uuid, $status) {
        $cxn = new \mysqli(HOST, USER, PASS, DB);

        if (mysqli_connect_errno()) {
            return false;
        }

        $stmt = $cxn->prepare("UPDATE Devices SET connected = ? WHERE uuid = ?;");
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

        $stmt = $cxn->prepare("UPDATE Devices SET addr = ?, port = ? WHERE uuid = ?;");
        $stmt->bind_param("sss", $addr, $port, $uuid);
        $stmt->execute();
        $stmt->close();
        $cxn->close();

        return true;
    }

?>
