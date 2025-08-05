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
        echo json_encode(['status' => false, 'error' => 'Cannot unfollow yourself']);
        exit;
    }
    
    // Remove follow relationship using correct table and columns
    $delete_sql = "DELETE FROM follow_list WHERE follower_id = ? AND user_id = ?";
    $delete_stmt = $conn->prepare($delete_sql);
    $delete_stmt->bind_param("ii", $follower_id, $following_id);
    
    if ($delete_stmt->execute() && $delete_stmt->affected_rows > 0) {
        echo json_encode([
            'status' => true,
            'message' => 'Unfollowed successfully'
        ]);
    } else {
        echo json_encode(['status' => false, 'error' => 'Not following this user or unfollow failed']);
    }
    
    $conn->close();
    
} catch (Exception $e) {
    echo json_encode(['status' => false, 'error' => $e->getMessage()]);
}
?>