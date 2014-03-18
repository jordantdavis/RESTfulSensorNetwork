<?php

    include("lib/php/slim/vendor/autoload.php");

    $app = new Slim\Slim();

    /*
        /connect input format:
        {
            sensors: {
                environmental: [...],
                position: [...],
                motion: [...]
            }
        }

        /update or /disconnect input format:
        {
            uuid: ...
        }
    */

    // connect a device
    $app->post("/connect", function() {
        $req = Slim\Slim::getInstance()->request;
        $reqBody = json_decode($req->getBody(), true);

        // validate json input
        // if ($reqBody) {
        //     if (array_key_exists("sensors", $reqBody) && count($reqBody) == 1) {
        //         $allSensors = $reqBody["sensors"];
        //
        //         if (is_array($allSensors)) {
        //
        //         } else {
        //
        //         }
        //     } else {
        //
        //     }
        // } else {
        //
        // }

        // values for database
        $uuid = uniqid($more_entropy=true);
        $ipAddr = $req->getIp();

        // store in database

        // response body
        echo json_encode(array("uuid" => $uuid));
    });

    // update a device's address
    $app->post("/update", function() {
        $req = Slim\Slim::getInstance()->request;
        $reqBody = json_decode($req->getBody(), true);

        // validate json input

        // change ipAddr in Device table

        // response body
        echo "{}";
    });

    // gracefully disconnect a device
    $app->post("/disconnect", function() {
        $req = Slim\Slim::getInstance()->request;
        $reqBody = json_decode($req->getBody(), true);

        // validate json input

        // change connection status of uuid to false

        // response body
        echo "{}";
    });

    $app->run();

?>
