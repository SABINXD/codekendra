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
        echo json_encode(['status' => false, 'error' => 'Cannot unblock yourself']);
        exit;
    }
    
    // Remove block relationship using correct table and columns
    $delete_sql = "DELETE FROM block_list WHERE user_id = ? AND blocked_user_id = ?";
    $delete_stmt = $conn->prepare($delete_sql);
    $delete_stmt->bind_param("ii", $blocker_id, $blocked_id);
    
    if ($delete_stmt->execute() && $delete_stmt->affected_rows > 0) {
        echo json_encode([
            'status' => true,
            'message' => 'User unblocked successfully'
        ]);
    } else {
        echo json_encode(['status' => false, 'error' => 'User was not blocked or unblock failed']);
    }
    
    $conn->close();
    
} catch (Exception $e) {
    echo json_encode(['status' => false, 'error' => $e->getMessage()]);
}
?>