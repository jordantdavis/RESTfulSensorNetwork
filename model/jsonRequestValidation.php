<?php

    namespace Model;

    /*
        A set of functions for identifying valid json request bodies for each API method.

        valid /connect json request:
        {
            sensors: {
                environmental: [...],
                position: [...],
                motion: [...]
            }
        }

        valid /connect, /update, /disconnect request json:
        {
            uuid: "..."
        }
    */

    function validateRegisterRequestJson($json) {
        if ($json != false) {
            if (is_array($json) && array_key_exists("sensors", $json) && count($json) == 1) {
                $sensors = $json["sensors"];
                $environmentalSensors = array("humidity", "light", "pressure", "temperature");
                $motionSensors = array("accelerometer", "gravity", "gyroscope");
                $positionSensors = array("gps", "magnetometer", "proximity");

                if (count($sensors) == 3 && array_key_exists("environmental", $sensors) &&
                array_key_exists("motion", $sensors) && array_key_exists("position", $sensors)) {

                    if (count($sensors["environmental"]) <= 4) {
                        foreach ($sensors["environmental"] as $sensor) {
                            if (!in_array($sensor, $environmentalSensors)) {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }

                    if (count($sensors["motion"]) <= 3) {
                        foreach ($sensors["motion"] as $sensor) {
                            if (!in_array($sensor, $motionSensors)) {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }

                    if (count($sensors["position"]) <= 3) {
                        foreach ($sensors["position"] as $sensor) {
                            if (!in_array($sensor, $positionSensors)) {
                                return false;
                            }
                        }
                    } else {
                        return false;
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
