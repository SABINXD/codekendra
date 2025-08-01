<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 0); // Disable error display but keep error logging

// Include files - use the correct path
require_once __DIR__ . "/config/db.php";

try {
    $conn = getDbConnection();
    
    $post_id = isset($_POST['post_id']) ? (int)$_POST['post_id'] : 0;
    
    if ($post_id <= 0) {
        throw new Exception("Invalid post ID: $post_id");
    }
    
    // Get comments with user info
    $sql = "SELECT 
                c.id,
                c.user_id,
                c.comment as comment_text,
                c.created_at,
                u.username,
                COALESCE(u.first_name, '') as first_name,
                COALESCE(u.last_name, '') as last_name,
                COALESCE(u.profile_pic, 'default_profile.jpg') as profile_pic
            FROM comments c
            LEFT JOIN users u ON c.user_id = u.id
            WHERE c.post_id = ?
            ORDER BY c.created_at ASC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $post_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $comments = array();
    
    while ($row = $result->fetch_assoc()) {
        // Create display name
        $display_name = trim($row['first_name'] . ' ' . $row['last_name']);
        if (empty($display_name)) {
            $display_name = $row['username'] ?: 'Unknown User';
        }
        
        // Handle profile picture
        $profile_pic_filename = null;
        if (!empty($row['profile_pic']) && $row['profile_pic'] !== 'default_profile.jpg') {
            $profile_pic_filename = $row['profile_pic'];
        }
        
        $comments[] = array(
            'id' => (int)$row['id'],
            'user_id' => (int)$row['user_id'],
            'user_name' => $display_name,
            'comment_text' => $row['comment_text'],
            'created_at' => $row['created_at'],
            'profile_pic' => $profile_pic_filename
        );
    }
    
    $response = [
        'status' => true,
        'comments' => $comments,
        'total_comments' => count($comments)
    ];
    
    echo json_encode($response);
    
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Error in get_comments.php: " . $e->getMessage());
    echo json_encode([
        'status' => false,
        'message' => $e->getMessage()
    ]);
}
?>