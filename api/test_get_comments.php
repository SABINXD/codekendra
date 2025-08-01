<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 0);

// Include files - use the correct path
require_once __DIR__ . "/config/db.php";

try {
    $conn = getDbConnection();
    
    // Test parameters
    $post_id = 1; // Use a valid post ID
    
    // Get comments
    $stmt = $conn->prepare("SELECT * FROM comments WHERE post_id = ? ORDER BY created_at DESC");
    $stmt->bind_param("i", $post_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $comments = array();
    while ($row = $result->fetch_assoc()) {
        $comments[] = $row;
    }
    
    echo json_encode([
        'status' => true,
        'post_id' => $post_id,
        'comments' => $comments,
        'total_comments' => count($comments)
    ]);
    
    $stmt->close();
    $conn->close();
} catch (Exception $e) {
    echo json_encode([
        'status' => false,
        'error' => $e->getMessage()
    ]);
}
?>