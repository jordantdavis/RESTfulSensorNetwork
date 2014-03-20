<?php

    include("lib/php/slim/vendor/autoload.php");
    include("model/jsonRequestValidation.php");
    include("model/deviceManagementCalls.php");

    $app = new Slim\Slim();

    // register a new device
    $app->post("/register", function() {
        $request = Slim\Slim::getInstance()->request;
        $requestBody = json_decode($request->getBody(), true);
        $response = Slim\Slim::getInstance()->response;

        // validate json input
        if (Model\validateRegisterRequestJson($requestBody)) {
            // values for database
            $uuid = uniqid($more_entropy=true);
            $addr = $request->getIp();
            $port = $request->getPort();

            // store in database
            $insertSuccess = Model\insertNewDevice($uuid, $addr, $port);
            $insertSuccess = $insertSuccess && Model\insertSensors($uuid, $requestBody["sensors"]);

            if (!$insertSuccess) {
                $response->setStatus(500);
            } else {
                $response->setStatus(200);
                $response->headers->set("Content-Type", "application/json");
                $response->setBody(json_encode(array("uuid" => $uuid)));
            }
        } else {
            $response->setStatus(400);
        }
    });

    // connect a device
    $app->post("/connect", function() {
        $request = Slim\Slim::getInstance()->request;
        $requestBody = json_decode($request->getBody(), true);
        $response = Slim\Slim::getInstance()->response;

        // validate json input
        if (Model\validateConnectRequestJson($requestBody)) {
            $uuid = $requestBody["uuid"];

            // change ipAddr and connection status in database
            $updateSuccess = Model\updateConnectionStatus($uuid, true);

            if (!updateSuccess) {
                $response->setStatus(500);
            } else {
                $response->setStatus(200);
            }
        } else {
            $response->setStatus(400);
        }
    });

    // update a device's address
    $app->post("/update", function() {
        $request = Slim\Slim::getInstance()->request;
        $requestBody = json_decode($request->getBody(), true);
        $response = Slim\Slim::getInstance()->response;

        // validate json input
        if (Model\validateUpdateRequestJson($requestBody)) {
            $uuid = $requestBody["uuid"];
            $addr = $request->getIp();
            $port = $request->getPort();

            // change ipAddr in database
            $updateSuccess = Model\updateConnectionAddressPort($uuid, $addr, $port);

            if (!updateSuccess) {
                $response->setStatus(500);
            } else {
                $response->setStatus(200);
            }
        } else {
            $response->setStatus(400);
        }
    });

    // gracefully disconnect a device
    $app->post("/disconnect", function() {
        $request = Slim\Slim::getInstance()->request;
        $requestBody = json_decode($request->getBody(), true);
        $response = Slim\Slim::getInstance()->response;

        // validate json input
        if (Model\validateDisconnectRequestJson($requestBody)) {
            $uuid = $requestBody["uuid"];

            // change connection status in database
            $updateSuccess = Model\updateConnectionStatus($uuid, false);

            if (!updateSuccess) {
                $response->setStatus(500);
            } else {
                $response->setStatus(200);
            }
        } else {
            $response->setStatus(400);
        }
    });

    $app->run();

?>
