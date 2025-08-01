<?php
// Database configuration
$servername = "localhost"; // Or use IP_ADDRESS below
$username = "root";
$password = "";
$dbname = "codekendra";
$port = 3306; // Change this only if MySQL uses a different port

// Function to get local server IPv4 (Windows)
function getLocalIPv4() {
    $output = [];
    exec("ipconfig", $output);
    foreach ($output as $line) {
        if (preg_match("/IPv4 Address.*?: ([\d.]+)/", $line, $matches)) {
            return $matches[1];
        }
    }
    return '127.0.0.1'; // fallback
}

// Assign the fetched IP to a variable
$IP_ADDRESS = getLocalIPv4();

// Define IP address constant for profile images or other uses
define('IP_ADDRESS', $IP_ADDRESS);

// Create connection function
function getDbConnection() {
    global $servername, $username, $password, $dbname, $port;

    $conn = new mysqli($servername, $username, $password, $dbname, $port);

    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }

    return $conn;
}

// Optional: print the IP address to verify
// echo "Server IPv4: " . IP_ADDRESS;
?>
