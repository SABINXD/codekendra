<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
header('Content-Type: application/json');
require_once "../config/db.php";

$uid         = $_POST['uid'] ?? '';
$displayName = $_POST['display_name'] ?? '';
$username    = $_POST['username'] ?? '';
$bio         = $_POST['bio'] ?? '';
$imageBase64 = $_POST['image'] ?? '';
$imageFilename = $_POST['filename'] ?? '';

if (!$uid || !$displayName || !$username || !$imageBase64 || !$imageFilename) {
    echo json_encode(['status' => 'fail', 'message' => 'Missing required data']);
    exit;
}

$stmt = $conn->prepare("SELECT id FROM users WHERE username = ? AND id != ?");
$stmt->bind_param("si", $username, $uid);
$stmt->execute();
if ($stmt->get_result()->num_rows > 0) {
    echo json_encode(['status' => 'fail', 'message' => 'Username already taken']);
    exit;
}
$stmt->close();


$nameParts = explode(' ', $displayName, 2);
$firstName = $nameParts[0];
$lastName  = $nameParts[1] ?? '';

$targetDir = "../web/assets/img/profile/";
$finalPath = $targetDir . basename($imageFilename);
$imageData = base64_decode($imageBase64);
if (!$imageData || file_put_contents($finalPath, $imageData) === false) {
    echo json_encode(['status' => 'fail', 'message' => 'Failed to save image']);
    exit;
}

$stmt = $conn->prepare("UPDATE users SET first_name=?, last_name=?, username=?, bio=?, profile_pic=? WHERE id=?");
$stmt->bind_param("sssssi", $firstName, $lastName, $username, $bio, $imageFilename, $uid);
$stmt->execute();
$stmt->close();

echo json_encode(['status' => 'success', 'filename' => $imageFilename]);
?>
