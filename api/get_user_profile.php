<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

error_reporting(E_ALL);
ini_set('display_errors', 1);

include __DIR__ . '/config/db.php';

try {
    $conn = getDbConnection();
    
    $user_id = (int)($_GET['user_id'] ?? $_POST['user_id'] ?? 0);
    
    if ($user_id <= 0) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid user ID']);
        exit;
    }
    
    // Get user profile information
    $user_sql = "SELECT id, first_name, last_name, username, email, bio, profile_pic, created_at 
                 FROM users WHERE id = ?";
    $user_stmt = $conn->prepare($user_sql);
    $user_stmt->bind_param("i", $user_id);
    $user_stmt->execute();
    $user_result = $user_stmt->get_result();
    
    if ($user_result->num_rows === 0) {
        echo json_encode(['status' => 'error', 'message' => 'User not found']);
        exit;
    }
    
    $user = $user_result->fetch_assoc();
    
    // Get post count
    $post_count_sql = "SELECT COUNT(*) as post_count FROM posts WHERE user_id = ?";
    $post_count_stmt = $conn->prepare($post_count_sql);
    $post_count_stmt->bind_param("i", $user_id);
    $post_count_stmt->execute();
    $post_count_result = $post_count_stmt->get_result();
    $post_count = $post_count_result->fetch_assoc()['post_count'];
    
    // Get followers count using correct table and columns
    $followers_sql = "SELECT COUNT(*) as followers FROM follow_list WHERE user_id = ?";
    $followers_stmt = $conn->prepare($followers_sql);
    $followers_stmt->bind_param("i", $user_id);
    $followers_stmt->execute();
    $followers_result = $followers_stmt->get_result();
    $followers = $followers_result->fetch_assoc()['followers'];
    
    // Get following count using correct table and columns
    $following_sql = "SELECT COUNT(*) as following FROM follow_list WHERE follower_id = ?";
    $following_stmt = $conn->prepare($following_sql);
    $following_stmt->bind_param("i", $user_id);
    $following_stmt->execute();
    $following_result = $following_stmt->get_result();
    $following = $following_result->fetch_assoc()['following'];
    
    // Prepare response
    $user['post_count'] = (int)$post_count;
    $user['followers'] = (int)$followers;
    $user['following'] = (int)$following;
    
    echo json_encode([
        'status' => 'success',
        'user' => $user
    ]);
    
    $conn->close();
    
} catch (Exception $e) {
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
?>