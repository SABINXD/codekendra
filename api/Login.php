<?php
// IMPORTANT: Turn off error reporting and display errors for production API responses
// This prevents PHP warnings/notices from corrupting the JSON output.
ini_set('display_errors', 0);
error_reporting(0);

header('Content-Type: application/json');

// Include the database configuration to use the $db variable
include(__DIR__ . "/config/db.php");

// Check if connection was successful (using $db from db.php)
if (!$db) { // $db will be false if mysqli_connect failed in db.php
    // Log the error internally for debugging, but don't display to client
    error_log("DB connection failed: " . mysqli_connect_error());
    echo json_encode(["status" => "error", "message" => "Database connection failed. Please try again later."]);
    exit; // Crucial: Stop execution after sending JSON
}

$email    = trim($_POST['email'] ?? '');
$password = trim($_POST['password'] ?? '');

if ($email === '' || $password === '') {
    echo json_encode(["status" => "error", "message" => "Missing credentials"]);
    exit; // Crucial: Stop execution after sending JSON
}

$hashed = md5($password); // Consider using password_hash and password_verify for better security

// Select profile_pic along with other user details
// Using $db for the connection
$stmt = $db->prepare("SELECT id, email, username, first_name, last_name, gender, profile_pic FROM users WHERE email = ? AND password = ? AND ac_status = 1");

if ($stmt === false) {
    error_log("Prepare statement failed: " . $db->error);
    echo json_encode(["status" => "error", "message" => "Internal server error. Please try again."]);
    exit; // Crucial: Stop execution after sending JSON
}

$stmt->bind_param('ss', $email, $hashed);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid credentials or account not verified"
    ]);
    exit; // Crucial: Stop execution after sending JSON
}

$user = $result->fetch_assoc();

// Construct the full profile picture URL using 'profile/'
$profilePicUrl = '';
if (!empty($user['profile_pic'])) {
    // Ip_address is defined in config/db.php
    $profilePicBaseUrl = 'http://' . Ip_address . '/codekendra/web/assets/img/profile/';
    $profilePicUrl = $profilePicBaseUrl . basename($user['profile_pic']);
}

echo json_encode([
    "status" => "success",
    "message" => "Login successful",
    "user" => [
        "user_id"   => $user['id'],
        "email"     => $user['email'],
        "username"  => $user['username'],
        "firstName" => $user['first_name'],
        "lastName"  => $user['last_name'],
        "gender"    => $user['gender'],
        "profile_pic" => $profilePicUrl
    ]
], JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);

$stmt->close();
$db->close(); // Close the connection using $db
exit; // Ensure no further output after successful response
?>