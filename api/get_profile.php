<?php
header('Content-Type: application/json');
require_once('config/db.php'); 
$uid = $_POST['uid'] ?? '';
if (!$uid) {
    echo json_encode(['status' => 'fail', 'message' => 'Missing UID']);
    exit;
}

$stmt = $conn->prepare("SELECT first_name, last_name, username, bio FROM users WHERE id = ?");
$stmt->bind_param("i", $uid);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(['status' => 'fail', 'message' => 'User not found']);
    exit;
}

$data = $result->fetch_assoc();
$data['display_name'] = $data['first_name'] . ' ' . $data['last_name'];

echo json_encode(['status' => 'success', 'user' => $data]);
$conn->close();
?>