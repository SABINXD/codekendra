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
        echo json_encode(['status' => true, 'is_blocked' => false]);
        exit;
    }
    
    // Check block status using correct table name and columns
    $check_sql = "SELECT * FROM block_list WHERE user_id = ? AND blocked_user_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->bind_param("ii", $blocker_id, $blocked_id);
    $check_stmt->execute();
    $result = $check_stmt->get_result();
    
    $is_blocked = $result->num_rows > 0;
    
    echo json_encode([
        'status' => true,
        'is_blocked' => $is_blocked
    ]);
    
    $conn->close();
    
} catch (Exception $e) {
    echo json_encode(['status' => false, 'error' => $e->getMessage()]);
}
?>