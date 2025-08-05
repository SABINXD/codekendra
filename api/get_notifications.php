<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Disable HTML error output to prevent JSON corruption
ini_set('display_errors', 0);
error_reporting(0);

include __DIR__ . '/config/db.php';

try {
    $conn = getDbConnection();
    
    $user_id = (int)($_GET['user_id'] ?? 0);
    
    if ($user_id <= 0) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid user ID']);
        exit;
    }
    
    // Get notifications with user info
    $notifications_sql = "SELECT n.*, u.username as user_name, u.profile_pic 
                         FROM notifications n 
                         LEFT JOIN users u ON n.from_user_id = u.id 
                         WHERE n.to_user_id = ? 
                         ORDER BY n.created_at DESC 
                         LIMIT 50";
    
    $notifications_stmt = $conn->prepare($notifications_sql);
    $notifications_stmt->bind_param("i", $user_id);
    $notifications_stmt->execute();
    $result = $notifications_stmt->get_result();
    
    $notifications = [];
    while ($row = $result->fetch_assoc()) {
        $notifications[] = [
            'id' => (int)$row['id'],
            'from_user_id' => (int)$row['from_user_id'],
            'message' => $row['message'] ?? '',
            'type' => $row['type'] ?? 'general',
            'read_status' => (int)$row['read_status'],
            'created_at' => $row['created_at'],
            'user_name' => $row['user_name'] ?? 'Unknown User',
            'profile_pic' => $row['profile_pic']
        ];
    }
    
    echo json_encode([
        'status' => 'success',
        'notifications' => $notifications
    ]);
    
    $conn->close();
    
} catch (Exception $e) {
    echo json_encode(['status' => 'error', 'message' => 'Server error occurred']);
}
?>