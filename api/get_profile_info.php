<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

require_once(__DIR__ . '/config/db.php');

try {
    // Get database connection using your existing function
    $conn = getDbConnection();
    
    // Get user ID from POST parameter (as sent by ProfileActivity)
    $uid = $_POST['uid'] ?? '';
    
    error_log("=== GET PROFILE INFO DEBUG ===");
    error_log("Received UID: " . $uid);
    error_log("POST data: " . print_r($_POST, true));
    
    if (empty($uid) || !is_numeric($uid)) {
        echo json_encode([
            'status' => 'error', 
            'message' => 'Missing or invalid user_id'
        ]);
        exit;
    }
    
    // FIXED: Include profile_pic and all needed fields
    $sql = "SELECT 
        id,
        first_name, 
        last_name, 
        username, 
        email,
        bio,
        profile_pic,
        CONCAT(first_name, ' ', last_name) as display_name
        FROM users 
        WHERE id = ?";
    
    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        throw new Exception("Prepare failed: " . $conn->error);
    }
    
    $stmt->bind_param("i", $uid);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows === 0) {
        echo json_encode([
            'status' => 'error', 
            'message' => 'User not found'
        ]);
        exit;
    }
    
    $user = $result->fetch_assoc();
    
    // Handle profile picture - ensure we return filename only
    if (empty($user['profile_pic']) || $user['profile_pic'] === 'null') {
        $user['profile_pic'] = 'default_profile.jpg';
    } else {
        // Extract filename if it's a full URL
        $user['profile_pic'] = basename($user['profile_pic']);
    }
    
    // Handle bio
    if (empty($user['bio'])) {
        $user['bio'] = 'No bio available';
    }
    
    // Add followers/following counts (set to 0 if no followers table)
    $user['followers'] = 0;
    $user['following'] = 0;
    
    error_log("Profile data retrieved for user: " . $user['username']);
    error_log("Profile pic: " . $user['profile_pic']);
    
    echo json_encode([
        'status' => 'success', 
        'user' => $user
    ]);
    
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("❌ Get profile info error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?>