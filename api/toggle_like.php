<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "codekendra";

try {
    $conn = new mysqli($servername, $username, $password, $dbname);
    
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
    
    $post_id = isset($_POST['post_id']) ? (int)$_POST['post_id'] : 0;
    $user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0;
    
    if ($post_id <= 0 || $user_id <= 0) {
        throw new Exception("Invalid post_id or user_id");
    }
    
    // Check if already liked
    $check_sql = "SELECT id FROM likes WHERE post_id = ? AND user_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->bind_param("ii", $post_id, $user_id);
    $check_stmt->execute();
    $result = $check_stmt->get_result();
    
    $is_liked = $result->num_rows > 0;
    
    if ($is_liked) {
        // Unlike - remove the like
        $delete_sql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";
        $delete_stmt = $conn->prepare($delete_sql);
        $delete_stmt->bind_param("ii", $post_id, $user_id);
        $delete_stmt->execute();
        $delete_stmt->close();
        $new_is_liked = false;
    } else {
        // Like - add the like
        $insert_sql = "INSERT INTO likes (post_id, user_id, created_at) VALUES (?, ?, NOW())";
        $insert_stmt = $conn->prepare($insert_sql);
        $insert_stmt->bind_param("ii", $post_id, $user_id);
        $insert_stmt->execute();
        $insert_stmt->close();
        $new_is_liked = true;
    }
    
    // Get updated like count
    $count_sql = "SELECT COUNT(*) as like_count FROM likes WHERE post_id = ?";
    $count_stmt = $conn->prepare($count_sql);
    $count_stmt->bind_param("i", $post_id);
    $count_stmt->execute();
    $count_result = $count_stmt->get_result();
    $count_row = $count_result->fetch_assoc();
    $like_count = (int)$count_row['like_count'];
    
    echo json_encode([
        'status' => true,
        'is_liked' => $new_is_liked,
        'like_count' => $like_count,
        'message' => $new_is_liked ? 'Post liked' : 'Post unliked'
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