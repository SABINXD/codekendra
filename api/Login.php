<?php
ini_set('display_errors', 0);
error_reporting(0);
header('Content-Type: application/json');
include(__DIR__ . "/config/db.php");

try {
    $db = getDbConnection();
} catch (Exception $e) {
    error_log("DB connection failed: " . $e->getMessage());
    echo json_encode(["status" => "error", "message" => "Database connection failed. Please try again later."]);
    exit;
}

$email = trim($_POST['email'] ?? '');
$password = trim($_POST['password'] ?? '');

if ($email === '' || $password === '') {
    echo json_encode(["status" => "error", "message" => "Missing credentials"]);
    exit;
}

$hashed = md5($password);
$stmt = $db->prepare("SELECT id, email, username, first_name, last_name, gender, profile_pic FROM users WHERE email = ? AND password = ? AND ac_status = 1");

if (!$stmt) {
    error_log("Prepare statement failed: " . $db->error);
    echo json_encode(["status" => "error", "message" => "Internal server error. Please try again."]);
    exit;
}

$stmt->bind_param('ss', $email, $hashed);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid credentials or account not verified"
    ]);
    exit;
}

$user = $result->fetch_assoc();

// FIXED: Return filename only, not full URL
$profilePicFilename = '';
if (!empty($user['profile_pic'])) {
    // Extract just the filename
    $profilePicFilename = basename($user['profile_pic']);
} else {
    $profilePicFilename = 'default_profile.jpg';
}

error_log("Login successful - User ID: " . $user['id'] . ", Profile pic: " . $profilePicFilename);

echo json_encode([
    "status" => "success",
    "message" => "Login successful",
    "user" => [
        "user_id" => $user['id'],
        "email" => $user['email'],
        "username" => $user['username'],
        "firstName" => $user['first_name'],
        "lastName" => $user['last_name'],
        "gender" => $user['gender'],
        "profile_pic" => $profilePicFilename  // FIXED: filename only
    ]
], JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);

$stmt->close();
$db->close();
exit;
?>