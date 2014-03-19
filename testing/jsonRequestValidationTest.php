<?php

    include("../model/jsonRequestValidation.php");

    class JsonValidationTest extends PHPUnit_Framework_TestCase {
        public function testValidateRegisterRequestJson() {
            $json = '
                {
                    "sensors": {
                        "environmental": ["light", "temperature"],
                        "motion": ["gyroscope"],
                        "position": ["gps"]
                    }
                }';

            $this->assertTrue(Model\validateRegisterRequestJson(json_decode($json, true)));
        }

        public function testValidateConnectRequestJson() {
            $json = '
                {
                    "uuid": "123"
                }
            ';

            $this->assertTrue(Model\validateConnectRequestJson(json_decode($json, true)));
        }

        public function testValidateUpdateRequestJson() {
            $json = '
                {
                    "uuid": "123"
                }
            ';

            $this->assertTrue(Model\validateUpdateRequestJson(json_decode($json, true)));
        }

        public function testValidateDisconnectRequestJson() {
            $json = '
                {
                    "uuid": "123"
                }
            ';

            $this->assertTrue(Model\validateUpdateRequestJson(json_decode($json, true)));
        }
    }

?>
