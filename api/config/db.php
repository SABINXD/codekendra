<?php
// Database configuration
$servername = "localhost"; // Or "192.168.1.17" if connecting remotely
$username = "root";
$password = "";
$dbname = "codekendra";
$port = 3306; // Change this only if MySQL uses a different port

// Create connection function
function getDbConnection() {
    global $servername, $username, $password, $dbname, $port;

    $conn = new mysqli($servername, $username, $password, $dbname, $port);

    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }

    return $conn;
}

// Define IP address constant for profile images
define('IP_ADDRESS', '192.168.1.17');
?>
