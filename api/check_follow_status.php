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
        echo json_encode(['status' => true, 'follow_status' => 'self']);
        exit;
    }
    
    // Check follow status using correct table name and columns
    $check_sql = "SELECT * FROM follow_list WHERE follower_id = ? AND user_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->bind_param("ii", $follower_id, $following_id);
    $check_stmt->execute();
    $result = $check_stmt->get_result();
    
    if ($result->num_rows > 0) {
        echo json_encode(['status' => true, 'follow_status' => 'accepted']);
    } else {
        echo json_encode(['status' => true, 'follow_status' => 'none']);
    }
    
    $conn->close();
    
} catch (Exception $e) {
    echo json_encode(['status' => false, 'error' => $e->getMessage()]);
}
?>