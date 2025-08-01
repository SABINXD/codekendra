<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Database connection
include(__DIR__ . "/config/db.php");

try {
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
    
    $userId = $_POST['user_id'] ?? '';
    $email = $_POST['email'] ?? '';
    $firstName = $_POST['firstName'] ?? '';
    $lastName = $_POST['lastName'] ?? '';
    $username = $_POST['username'] ?? '';
    $gender = $_POST['gender'] ?? '';
    $bio = $_POST['bio'] ?? '';
    
    if (!$userId || !$email || !$firstName || !$lastName || !$username) {
        echo json_encode(['status' => 'fail', 'error' => 'Missing required fields']);
        exit;
    }
    
    // Check if username is taken by another user
    $stmt = $conn->prepare("SELECT id FROM users WHERE username = ? AND id != ?");
    $stmt->bind_param("si", $username, $userId);
    $stmt->execute();
    $stmt->store_result();
    
    if ($stmt->num_rows > 0) {
        echo json_encode(['status' => 'fail', 'error' => 'Username already taken']);
        exit;
    }
    $stmt->close();
    
    // Update user profile including bio
    $stmt = $conn->prepare("UPDATE users SET email = ?, first_name = ?, last_name = ?, username = ?, gender = ?, bio = ? WHERE id = ?");
    $stmt->bind_param("ssssssi", $email, $firstName, $lastName, $username, $gender, $bio, $userId);
    
    if ($stmt->execute()) {
        echo json_encode(['status' => 'success', 'message' => 'Profile updated successfully']);
    } else {
        echo json_encode(['status' => 'fail', 'error' => 'Database update failed']);
    }
    
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Edit profile error: " . $e->getMessage());
    echo json_encode(['status' => 'fail', 'error' => $e->getMessage()]);
}
?>
