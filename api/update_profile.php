<?php
header('Content-Type: application/json');
require_once('config/db.php');

$uid        = $_POST['uid'] ?? '';
$firstName  = $_POST['first_name'] ?? null;
$lastName   = $_POST['last_name'] ?? null;
$username   = $_POST['username'] ?? null;
$bio        = $_POST['bio'] ?? null;

if (!$uid || !is_numeric($uid)) {
    echo json_encode(['status' => 'fail', 'message' => 'Missing or invalid UID']);
    exit;
}

// Check for username conflict
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

// Prepare update statement
$stmt = $db->prepare("UPDATE users SET 
    first_name = COALESCE(?, first_name),
    last_name  = COALESCE(?, last_name),
    username   = COALESCE(?, username),
    bio        = COALESCE(?, bio)
    WHERE id   = ?");
$stmt->bind_param("ssssi", $firstName, $lastName, $username, $bio, $uid);

if ($stmt->execute()) {
    echo json_encode(['status' => 'success']);
} else {
    echo json_encode(['status' => 'fail', 'message' => 'Database update error: ' . $stmt->error]);
}
$stmt->close();
?>
