<?php
header('Content-Type: application/json');
require_once('config/db.php'); // This provides $db, not $conn

$uid = $_POST['uid'] ?? '';
if (!$uid || !is_numeric($uid)) {
    echo json_encode(['status' => 'fail', 'message' => 'Missing or invalid UID']);
    exit;
}

// Optional logging for diagnostics
file_put_contents('debug_profile.log', "UID Received: $uid\n", FILE_APPEND);

// Make sure we're using $db instead of $conn
$stmt = $db->prepare("SELECT first_name, last_name, username, bio, profile_pic FROM users WHERE id = ?");
$stmt->bind_param("i", $uid);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(['status' => 'fail', 'message' => 'User not found']);
    exit;
}

$data = $result->fetch_assoc();
$data['display_name'] = trim($data['first_name'] . ' ' . $data['last_name']);

echo json_encode(['status' => 'success', 'user' => $data]);
?>
