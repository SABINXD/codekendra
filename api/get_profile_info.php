<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Database connection
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "codekendra";

try {
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
    
    $uid = $_POST['uid'] ?? '';
    
    if (!$uid || !is_numeric($uid)) {
        echo json_encode(['status' => 'fail', 'message' => 'Missing or invalid UID']);
        exit;
    }
    
    // Log for debugging
    error_log("Profile request for UID: $uid");
    
    // Get user profile with all fields
    $stmt = $conn->prepare("SELECT first_name, last_name, username, bio, profile_pic, email, gender FROM users WHERE id = ?");
    $stmt->bind_param("i", $uid);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows === 0) {
        echo json_encode(['status' => 'fail', 'message' => 'User not found']);
        exit;
    }
    
    $data = $result->fetch_assoc();
    
    // Create display name
    $data['display_name'] = trim($data['first_name'] . ' ' . $data['last_name']);
    
    // Handle empty bio
    if (empty($data['bio'])) {
        $data['bio'] = 'No bio available';
    }
    
    // Handle profile pic
    if (empty($data['profile_pic']) || $data['profile_pic'] === 'default_profile.jpg') {
        $data['profile_pic'] = 'default_profile.jpg';
    }
    
    // Add follower/following counts (placeholder for now)
    $data['followers'] = '0';
    $data['following'] = '0';
    
    error_log("Profile data retrieved: " . json_encode($data));
    
    echo json_encode([
        'status' => 'success', 
        'user' => $data
    ]);
    
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Profile error: " . $e->getMessage());
    echo json_encode([
        'status' => 'fail', 
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?>
