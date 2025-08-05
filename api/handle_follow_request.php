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
    
    $input = json_decode(file_get_contents('php://input'), true);
    
    $follower_id = (int)($input['follower_id'] ?? $_POST['follower_id'] ?? 0);
    $following_id = (int)($input['following_id'] ?? $_POST['following_id'] ?? 0);
    $action = $input['action'] ?? $_POST['action'] ?? '';
    
    if ($follower_id <= 0 || $following_id <= 0 || !in_array($action, ['accept', 'decline'])) {
        echo json_encode(['status' => false, 'error' => 'Invalid parameters']);
        exit;
    }
    
    if ($action == 'accept') {
        // Update follow status to accepted
        $update_sql = "UPDATE follows SET status = 'accepted' WHERE follower_id = ? AND following_id = ?";
        $update_stmt = $conn->prepare($update_sql);
        $update_stmt->bind_param("ii", $follower_id, $following_id);
        $update_stmt->execute();
        
        // Create notification for follower
        $notification_sql = "INSERT INTO notifications (to_user_id, from_user_id, message, type, read_status, created_at) VALUES (?, ?, 'accepted your follow request', 'follow_accepted', 0, NOW())";
        $notification_stmt = $conn->prepare($notification_sql);
        $notification_stmt->bind_param("ii", $follower_id, $following_id);
        $notification_stmt->execute();
        
        echo json_encode(['status' => true, 'message' => 'Follow request accepted']);
    } else {
        // Delete follow request
        $delete_sql = "DELETE FROM follows WHERE follower_id = ? AND following_id = ?";
        $delete_stmt = $conn->prepare($delete_sql);
        $delete_stmt->bind_param("ii", $follower_id, $following_id);
        $delete_stmt->execute();
        
        echo json_encode(['status' => true, 'message' => 'Follow request declined']);
    }
    
    $conn->close();
    
} catch (Exception $e) {
    echo json_encode(['status' => false, 'error' => $e->getMessage()]);
}
?>