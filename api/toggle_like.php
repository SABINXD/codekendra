<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

error_reporting(E_ALL);
ini_set('display_errors', 1);

require_once(__DIR__ . "/config/db.php");

try {
    $conn = getDbConnection();
    
    // Handle both POST data and JSON input
    $input = json_decode(file_get_contents('php://input'), true);
    
    $post_id = 0;
    $user_id = 0;
    
    if (isset($_POST['post_id'])) {
        $post_id = (int)$_POST['post_id'];
        $user_id = (int)$_POST['user_id'];
    } elseif (isset($input['post_id'])) {
        $post_id = (int)$input['post_id'];
        $user_id = (int)$input['user_id'];
    }
    
    error_log("Toggle like - Post ID: $post_id, User ID: $user_id");
    
    if ($post_id <= 0 || $user_id <= 0) {
        throw new Exception("Invalid post ID or user ID");
    }
    
    // Check if user already liked this post
    $check_sql = "SELECT id FROM likes WHERE post_id = ? AND user_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->bind_param("ii", $post_id, $user_id);
    $check_stmt->execute();
    $check_result = $check_stmt->get_result();
    
    $is_liked = false;
    $action = "";
    
    if ($check_result->num_rows > 0) {
        // User already liked - remove like
        $delete_sql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";
        $delete_stmt = $conn->prepare($delete_sql);
        $delete_stmt->bind_param("ii", $post_id, $user_id);
        $delete_stmt->execute();
        $is_liked = false;
        $action = "unliked";
        $delete_stmt->close();
    } else {
        // User hasn't liked - add like
        $insert_sql = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";
        $insert_stmt = $conn->prepare($insert_sql);
        $insert_stmt->bind_param("ii", $post_id, $user_id);
        $insert_stmt->execute();
        $is_liked = true;
        $action = "liked";
        $insert_stmt->close();
        
        // Add notification for the post owner (if not liking own post)
        $post_owner_sql = "SELECT user_id FROM posts WHERE id = ?";
        $post_owner_stmt = $conn->prepare($post_owner_sql);
        $post_owner_stmt->bind_param("i", $post_id);
        $post_owner_stmt->execute();
        $post_owner_result = $post_owner_stmt->get_result();
        $post_owner = $post_owner_result->fetch_assoc();
        
        if ($post_owner && $post_owner['user_id'] != $user_id) {
            $notification_sql = "INSERT INTO notifications (to_user_id, from_user_id, message, post_id, read_status) VALUES (?, ?, 'liked your post !', ?, 0)";
            $notification_stmt = $conn->prepare($notification_sql);
            $notification_stmt->bind_param("iis", $post_owner['user_id'], $user_id, $post_id);
            $notification_stmt->execute();
            $notification_stmt->close();
        }
        $post_owner_stmt->close();
    }
    
    // Get updated like count
    $count_sql = "SELECT COUNT(*) as like_count FROM likes WHERE post_id = ?";
    $count_stmt = $conn->prepare($count_sql);
    $count_stmt->bind_param("i", $post_id);
    $count_stmt->execute();
    $count_result = $count_stmt->get_result();
    $like_count = $count_result->fetch_assoc()['like_count'];
    
    echo json_encode([
        'status' => true,
        'is_liked' => $is_liked,
        'like_count' => (int)$like_count,
        'message' => "Post $action successfully",
        'action' => $action
    ]);
    
    $check_stmt->close();
    $count_stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Toggle like error: " . $e->getMessage());
    echo json_encode([
        'status' => false,
        'error' => $e->getMessage()
    ]);
}
?>
