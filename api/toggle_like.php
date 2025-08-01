<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

include(__DIR__ . "/config/db.php");

try {
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
    
    $post_id = isset($_POST['post_id']) ? (int)$_POST['post_id'] : 0;
    $user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0;
    
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
    
    if ($check_result->num_rows > 0) {
        // User already liked - remove like
        $delete_sql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";
        $delete_stmt = $conn->prepare($delete_sql);
        $delete_stmt->bind_param("ii", $post_id, $user_id);
        $delete_stmt->execute();
        $is_liked = false;
        $delete_stmt->close();
    } else {
        // User hasn't liked - add like (FIXED: removed created_at)
        $insert_sql = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";
        $insert_stmt = $conn->prepare($insert_sql);
        $insert_stmt->bind_param("ii", $post_id, $user_id);
        $insert_stmt->execute();
        $is_liked = true;
        $insert_stmt->close();
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
        'message' => $is_liked ? 'Post liked' : 'Post unliked'
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