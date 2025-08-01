<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 0); // Disable error display but keep error logging

// Include files - use the correct path
require_once __DIR__ . "/config/db.php";
require_once __DIR__ . "/PieSocketPublisher.php";

try {
    $conn = getDbConnection();
    
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception('Invalid request method');
    }
    
    $post_id = isset($_POST['post_id']) ? (int)$_POST['post_id'] : 0;
    $user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0;
    $comment_text = isset($_POST['comment_text']) ? trim($_POST['comment_text']) : '';
    
    if ($post_id <= 0 || $user_id <= 0 || empty($comment_text)) {
        throw new Exception('Invalid parameters');
    }
    
    // Use 'comment' column to match database schema
    $insert_stmt = $conn->prepare("INSERT INTO comments (post_id, user_id, comment) VALUES (?, ?, ?)");
    $insert_stmt->bind_param("iis", $post_id, $user_id, $comment_text);
    
    if ($insert_stmt->execute()) {
        // Get the new comment ID
        $new_comment_id = $conn->insert_id;
        
        // Get user info for the comment
        $user_stmt = $conn->prepare("SELECT username, first_name, last_name, profile_pic FROM users WHERE id = ?");
        $user_stmt->bind_param("i", $user_id);
        $user_stmt->execute();
        $user_result = $user_stmt->get_result();
        $user_data = $user_result->fetch_assoc();
        
        // Create display name
        $display_name = trim($user_data['first_name'] . ' ' . $user_data['last_name']);
        if (empty($display_name)) {
            $display_name = $user_data['username'] ?: 'Unknown User';
        }
        
        // Handle profile picture
        $profile_pic_filename = null;
        if (!empty($user_data['profile_pic']) && $user_data['profile_pic'] !== 'default_profile.jpg') {
            $profile_pic_filename = $user_data['profile_pic'];
        }
        
        // Get updated comment count
        $count_stmt = $conn->prepare("SELECT COUNT(*) AS comment_count FROM comments WHERE post_id = ?");
        $count_stmt->bind_param("i", $post_id);
        $count_stmt->execute();
        $count_result = $count_stmt->get_result();
        $comment_count = $count_result->fetch_assoc()['comment_count'];
        
        // Prepare comment data for WebSocket broadcast
        $comment_data = [
            'id' => $new_comment_id,
            'user_id' => $user_id,
            'user_name' => $display_name,
            'comment_text' => $comment_text,
            'created_at' => date('Y-m-d H:i:s'),
            'profile_pic' => $profile_pic_filename,
            'post_id' => $post_id
        ];
        
        // Broadcast new comment via WebSocket
        $publish_result = PieSocketPublisher::publish('new_comment', [
            'post_id' => $post_id,
            'comment' => $comment_data,
            'comment_count' => (int)$comment_count
        ]);
        
        $response = [
            "status" => true,
            "message" => "Comment added successfully",
            "comment_count" => (int)$comment_count,
            "comment" => $comment_data
        ];
        
        echo json_encode($response);
        
        $count_stmt->close();
        $user_stmt->close();
        $insert_stmt->close();
    } else {
        throw new Exception('Failed to add comment: ' . $insert_stmt->error);
    }
    
    $conn->close();
    
} catch (Exception $e) {
    error_log("Error in add_comment.php: " . $e->getMessage());
    echo json_encode([
        'status' => false,
        'error' => $e->getMessage()
    ]);
}
?>