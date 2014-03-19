<?php

    include("lib/php/slim/vendor/autoload.php");
    include("model/jsonRequestValidation.php");
    
    //connect to database, need to put your mysql info
    $con=mysqli_connect("localhost","COMP4302jdavis17","password", "COMP4302jdavis17");

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
            $ipAddr = $request->getIp();

            // store in database
            mysqli_query($con, "INSERT INTO Devices (id, ipAddr) VALUES ('$uuid','$ipAddr')");
           
            // response body
            $response->setStatus(200);
            $response->headers->set("Content-Type", "application/json");
            $response->setBody(json_encode(array("uuid" => $uuid)));
        } else {
            echo json_encode(array("message" => "Bad request"));
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
            $ipAddr = $request->getIp();
			
            // change ipAddr and connection status in database
            mysqli_query($con,"UPDATE Devices SET ipAddr = '$ipAddr' WHERE id='$uuid'");
            mysqli_query($con,"UPDATE Devices SET connected = 'True' WHERE id='$uuid'");

            // response body
            $response->setStatus(200);
            $response->headers->set("Content-Type", "application/json");
        } else {

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
            $ipAddr = $request->getIp();

            // change ipAddr in database
            mysqli_query($con,"UPDATE Devices SET ipAddr = '$ipAddr' WHERE id='$uuid'");

            // response body
            $response->setStatus(200);
            $response->headers->set("Content-Type", "application/json");
        } else {

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
            mysqli_query($con,"UPDATE Devices SET connected = 'False' WHERE id='$uuid'");
			
            // response body
            $response->setStatus(200);
            $response->headers->set("Content-Type", "application/json");
        } else {

        }
    });
	
    $app->run();
	
	//close database connection
	mysqli_close($con);
?>
