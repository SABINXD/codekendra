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
    
    $blocker_id = (int)($_POST['blocker_id'] ?? 0);
    $blocked_id = (int)($_POST['blocked_id'] ?? 0);
    
    if ($blocker_id <= 0 || $blocked_id <= 0) {
        echo json_encode(['status' => false, 'error' => 'Invalid user IDs']);
        exit;
    }
    
    if ($blocker_id == $blocked_id) {
        echo json_encode(['status' => false, 'error' => 'Cannot block yourself']);
        exit;
    }
    
    // Check if already blocked
    $check_sql = "SELECT * FROM block_list WHERE user_id = ? AND blocked_user_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->bind_param("ii", $blocker_id, $blocked_id);
    $check_stmt->execute();
    $result = $check_stmt->get_result();
    
    if ($result->num_rows > 0) {
        echo json_encode(['status' => false, 'error' => 'User already blocked']);
        exit;
    }
    
    // Remove any existing follow relationships
    $unfollow_sql1 = "DELETE FROM follow_list WHERE follower_id = ? AND user_id = ?";
    $unfollow_stmt1 = $conn->prepare($unfollow_sql1);
    $unfollow_stmt1->bind_param("ii", $blocker_id, $blocked_id);
    $unfollow_stmt1->execute();
    
    $unfollow_sql2 = "DELETE FROM follow_list WHERE follower_id = ? AND user_id = ?";
    $unfollow_stmt2 = $conn->prepare($unfollow_sql2);
    $unfollow_stmt2->bind_param("ii", $blocked_id, $blocker_id);
    $unfollow_stmt2->execute();
    
    // Add block relationship
    $insert_sql = "INSERT INTO block_list (user_id, blocked_user_id) VALUES (?, ?)";
    $insert_stmt = $conn->prepare($insert_sql);
    $insert_stmt->bind_param("ii", $blocker_id, $blocked_id);
    
    if ($insert_stmt->execute()) {
        echo json_encode([
            'status' => true,
            'message' => 'User blocked successfully'
        ]);
    } else {
        echo json_encode(['status' => false, 'error' => 'Failed to block user']);
    }
    
    $conn->close();
    
} catch (Exception $e) {
    echo json_encode(['status' => false, 'error' => $e->getMessage()]);
}
?>