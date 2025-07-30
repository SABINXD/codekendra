<?php
header('Content-Type: application/json');
require_once('config/db.php');

$uid         = $_POST['uid'] ?? '';
$displayName = $_POST['display_name'] ?? null;
$username    = $_POST['username'] ?? null;
$bio         = $_POST['bio'] ?? null;
$imageBase64 = $_POST['image'] ?? null;
$imageFilename = $_POST['filename'] ?? null;

if (!$uid) {
    echo json_encode(['status' => 'fail', 'message' => 'Missing UID']);
    exit;
}

// Username conflict check (only if username provided)
if ($username) {
    $stmt = $db->prepare("SELECT id FROM users WHERE username = ? AND id != ?");
    $stmt->bind_param("si", $username, $uid);
    $stmt->execute();
    if ($stmt->get_result()->num_rows > 0) {
        echo json_encode(['status' => 'fail', 'message' => 'Username already taken']);
        exit;
    }
    $stmt->close();
}

// Name splitting only if displayName provided
$firstName = $lastName = null;
if ($displayName) {
    $parts = explode(' ', $displayName, 2);
    $firstName = $parts[0];
    $lastName  = $parts[1] ?? '';
}

// Handle image if provided
if ($imageBase64 && $imageFilename) {
    $targetDir = "../web/assets/img/profile/";
    $finalPath = $targetDir . basename($imageFilename);
    $imageData = base64_decode($imageBase64);

    if (!$imageData || file_put_contents($finalPath, $imageData) === false) {
        echo json_encode(['status' => 'fail', 'message' => 'Failed to save image']);
        exit;
    }

    $stmt = $db->prepare("UPDATE users SET 
        first_name = COALESCE(?, first_name), 
        last_name  = COALESCE(?, last_name), 
        username   = COALESCE(?, username), 
        bio        = COALESCE(?, bio), 
        profile_pic= ?
        WHERE id = ?");
    $stmt->bind_param("sssssi", $firstName, $lastName, $username, $bio, $imageFilename, $uid);
} else {
    $stmt = $db->prepare("UPDATE users SET 
        first_name = COALESCE(?, first_name), 
        last_name  = COALESCE(?, last_name), 
        username   = COALESCE(?, username), 
        bio        = COALESCE(?, bio)
        WHERE id = ?");
    $stmt->bind_param("sssssi", $firstName, $lastName, $username, $bio, $uid);
}

$stmt->execute();
$stmt->close();

echo json_encode(['status' => 'success']);
