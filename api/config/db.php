<?php
// Database configuration
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "codekendra";

// ✅ Smart Function to get local IPv4, skipping VPNs (like Radmin)
function getLocalIPv4() {
    $output = [];
    exec("ipconfig", $output);
    $currentAdapter = '';
    $preferredIp = '';
    foreach ($output as $line) {
        $line = trim($line);
        // Detect adapter header
        if (preg_match('/adapter (.+):/', $line, $matches)) {
            $currentAdapter = strtolower($matches[1]);
            continue;
        }
        // Skip known VPN, virtual, or loopback adapters
        if (
            str_contains($currentAdapter, 'radmin') ||
            str_contains($currentAdapter, 'virtual') ||
            str_contains($currentAdapter, 'loopback') ||
            str_contains($currentAdapter, 'vmware') ||
            str_contains($currentAdapter, 'tunnel') ||
            str_contains($currentAdapter, 'bluetooth')
        ) {
            continue;
        }
        // Match IPv4 address from preferred adapter
        if (preg_match("/IPv4 Address.*?: ([\d.]+)/", $line, $matches)) {
            $ip = $matches[1];
            // Only use private IP ranges
            if (preg_match('/^(192\.168|10\.|172\.(1[6-9]|2[0-9]|3[0-1]))\./', $ip)) {
                $preferredIp = $ip;
                break; // First valid IP is enough
            }
        }
    }
    return $preferredIp ?: '127.0.0.1'; // fallback
}

// Assign the fetched IP to a variable
$IP_ADDRESS = getLocalIPv4();
// Define IP address constant for public use (like image URLs)
define('IP_ADDRESS', $IP_ADDRESS);

// Create connection function with better error handling
function getDbConnection() {
    global $servername, $username, $password, $dbname;
    
    try {
        // Create connection
        $conn = new mysqli($servername, $username, $password, $dbname);
        
        // Check connection
        if ($conn->connect_error) {
            throw new Exception("Connection failed: " . $conn->connect_error);
        }
        
        // Set charset to utf8mb4
        if (!$conn->set_charset("utf8mb4")) {
            throw new Exception("Error loading character set utf8mb4: " . $conn->error);
        }
        
        return $conn;
    } catch (Exception $e) {
        // Log error for debugging
        error_log("Database connection error: " . $e->getMessage());
        throw $e;
    }
}

// ✅ Optional for debugging:
// echo "Server IPv4: " . IP_ADDRESS;
?>