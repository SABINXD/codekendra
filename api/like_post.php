<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 0);

require_once __DIR__ . "/config/db.php";

try {
    $conn = getDbConnection();
    
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception('Invalid request method');
    }
    
    $post_id = isset($_POST['post_id']) ? (int)$_POST['post_id'] : 0;
    $user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0;
    
    if ($post_id <= 0 || $user_id <= 0) {
        throw new Exception('Invalid parameters: post_id=' . $post_id . ', user_id=' . $user_id);
    }
    
    // Check if user already liked this post
    $check_stmt = $conn->prepare("SELECT id FROM likes WHERE post_id = ? AND user_id = ?");
    $check_stmt->bind_param("ii", $post_id, $user_id);
    $check_stmt->execute();
    $result = $check_stmt->get_result();
    
    if ($result->num_rows > 0) {
        // User already liked, so unlike
        $delete_stmt = $conn->prepare("DELETE FROM likes WHERE post_id = ? AND user_id = ?");
        $delete_stmt->bind_param("ii", $post_id, $user_id);
        
        if ($delete_stmt->execute()) {
            // Get updated like count
            $count_stmt = $conn->prepare("SELECT COUNT(*) AS like_count FROM likes WHERE post_id = ?");
            $count_stmt->bind_param("i", $post_id);
            $count_stmt->execute();
            $count_result = $count_stmt->get_result();
            $like_count = $count_result->fetch_assoc()['like_count'];
            
            echo json_encode([
                'status' => 'success',
                'action' => 'unliked',
                'message' => 'Post unliked successfully',
                'like_count' => (int)$like_count,
                'is_liked' => false
            ]);
            
            $count_stmt->close();
        } else {
            throw new Exception('Failed to unlike post');
        }
        
        $delete_stmt->close();
    } else {
        // User hasn't liked, so like
        $insert_stmt = $conn->prepare("INSERT INTO likes (post_id, user_id) VALUES (?, ?)");
        $insert_stmt->bind_param("ii", $post_id, $user_id);
        
        if ($insert_stmt->execute()) {
            // Get updated like count
            $count_stmt = $conn->prepare("SELECT COUNT(*) AS like_count FROM likes WHERE post_id = ?");
            $count_stmt->bind_param("i", $post_id);
            $count_stmt->execute();
            $count_result = $count_stmt->get_result();
            $like_count = $count_result->fetch_assoc()['like_count'];
            
            echo json_encode([
                'status' => 'success',
                'action' => 'liked',
                'message' => 'Post liked successfully',
                'like_count' => (int)$like_count,
                'is_liked' => true
            ]);
            
            $count_stmt->close();
        } else {
            throw new Exception('Failed to like post');
        }
        
        $insert_stmt->close();
    }
    
    $check_stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Error in like_post.php: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?>