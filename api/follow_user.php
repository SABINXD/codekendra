<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

error_reporting(E_ALL);
ini_set('display_errors', 1);

include __DIR__ . '/config/db.php';

try {
    $conn = getDbConnection();
    
    $follower_id = (int)($_POST['follower_id'] ?? 0);
    $following_id = (int)($_POST['following_id'] ?? 0);
    
    if ($follower_id <= 0 || $following_id <= 0) {
        echo json_encode(['status' => false, 'error' => 'Invalid user IDs']);
        exit;
    }
    
    if ($follower_id == $following_id) {
        echo json_encode(['status' => false, 'error' => 'Cannot follow yourself']);
        exit;
    }
    
    // Check if already following
    $check_sql = "SELECT * FROM follow_list WHERE follower_id = ? AND user_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->bind_param("ii", $follower_id, $following_id);
    $check_stmt->execute();
    $result = $check_stmt->get_result();
    
    if ($result->num_rows > 0) {
        echo json_encode(['status' => false, 'error' => 'Already following this user']);
        exit;
    }
    
    // Check if blocked
    $block_check_sql = "SELECT * FROM block_list WHERE user_id = ? AND blocked_user_id = ?";
    $block_stmt = $conn->prepare($block_check_sql);
    $block_stmt->bind_param("ii", $following_id, $follower_id);
    $block_stmt->execute();
    $block_result = $block_stmt->get_result();
    
    if ($block_result->num_rows > 0) {
        echo json_encode(['status' => false, 'error' => 'Cannot follow this user']);
        exit;
    }
    
    // Add follow relationship
    $insert_sql = "INSERT INTO follow_list (follower_id, user_id) VALUES (?, ?)";
    $insert_stmt = $conn->prepare($insert_sql);
    $insert_stmt->bind_param("ii", $follower_id, $following_id);
    
    if ($insert_stmt->execute()) {
        // Create notification
        $notification_sql = "INSERT INTO notifications (to_user_id, from_user_id, message, read_status, created_at) 
                            VALUES (?, ?, 'started following you', 0, NOW())";
        $notification_stmt = $conn->prepare($notification_sql);
        $notification_stmt->bind_param("ii", $following_id, $follower_id);
        $notification_stmt->execute();
        
        echo json_encode([
            'status' => true,
            'message' => 'Follow request sent successfully'
        ]);
    } else {
        echo json_encode(['status' => false, 'error' => 'Failed to follow user']);
    }
    
    $conn->close();
    
} catch (Exception $e) {
    echo json_encode(['status' => false, 'error' => $e->getMessage()]);
}
?>