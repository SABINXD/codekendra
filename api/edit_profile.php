<?php
header('Content-Type: application/json');
require_once('config/db.php'); 
$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    echo json_encode(['status' => 'fail', 'error' => 'Database connection failed']);
    exit;
}

$userId    = $_POST['user_id'] ?? '';
$email     = $_POST['email'] ?? '';
$firstName = $_POST['firstName'] ?? '';
$lastName  = $_POST['lastName'] ?? '';
$username  = $_POST['username'] ?? '';
$gender    = $_POST['gender'] ?? '';

if (!$userId || !$email || !$firstName || !$lastName || !$username || !$gender) {
    echo json_encode(['status' => 'fail', 'error' => 'Missing one or more required fields']);
    exit;
}

// Check if new username is taken by another user
$stmt = $conn->prepare("SELECT id FROM users WHERE username = ? AND id != ?");
$stmt->bind_param("si", $username, $userId);
$stmt->execute();
$stmt->store_result();
if ($stmt->num_rows > 0) {
    echo json_encode(['status' => 'fail', 'error' => 'Username already taken']);
    exit;
}
$stmt->close();

// Update user profile
$stmt = $conn->prepare("UPDATE users SET email = ?, first_name = ?, last_name = ?, username = ?, gender = ? WHERE id = ?");
$stmt->bind_param("sssssi", $email, $firstName, $lastName, $username, $gender, $userId);
if ($stmt->execute()) {
    echo json_encode(['status' => 'success']);
} else {
    echo json_encode(['status' => 'fail', 'error' => $stmt->error]);
}
$stmt->close();
$conn->close();
?>