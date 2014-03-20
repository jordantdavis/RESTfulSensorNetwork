<?php

    namespace Model;

    function validateRegisterRequestJson($json) {
        if ($json != false) {
            if (is_array($json) && array_key_exists("sensors", $json) && count($json) == 1) {
                $deviceSensors = $json["sensors"];

                if (is_array($deviceSensors) && count($deviceSensors) > 0 && count($deviceSensors) <= 10) {
                    $allSensors = array("accelerometer", "gps", "gravity", "gyroscope", "humidity",
                    "light", "magnetometer", "pressure", "proximity", "temperature");

                    foreach ($deviceSensors as $sensor) {
                        if (!in_array($sensor, $allSensors)) {
                            return false;
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    function validateConnectRequestJson($json) {
        return validateConnectionManagementRequestJson($json);
    }

    function validateUpdateRequestJson($json) {
        return validateConnectionManagementRequestJson($json);
    }

    function validateDisconnectRequestJson($json) {
        return validateConnectionManagementRequestJson($json);
    }

    // Connect, update, and disconnect validation condensed into
    // one method because they CURRENTLY share the same format.
    function validateConnectionManagementRequestJson($json) {
        if ($json != false) {
            if (is_array($json) && count($json) == 1) {
                if (array_key_exists("uuid", $json)) {
                    $uuid = $json["uuid"];
                    if (is_string($uuid)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

?>
