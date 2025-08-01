<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Include database configuration
require_once(__DIR__ . '/config/db.php');

try {
    $conn = getDbConnection();
    
    // Get user_id from GET or POST parameters
    $current_user_id = 0;
    if (isset($_GET['user_id'])) {
        $current_user_id = (int)$_GET['user_id'];
    } elseif (isset($_POST['user_id'])) {
        $current_user_id = (int)$_POST['user_id'];
    }
    
    error_log("Get posts - User ID: " . $current_user_id);
    error_log("Request method: " . $_SERVER['REQUEST_METHOD']);
    
    // Get posts with user info and like status
    $sql = "SELECT 
                p.id,
                p.user_id,
                p.post_text,
                p.post_img,
                p.created_at,
                u.username,
                COALESCE(u.first_name, '') as first_name,
                COALESCE(u.last_name, '') as last_name,
                COALESCE(u.profile_pic, 'default_profile.jpg') as profile_pic,
                COUNT(DISTINCT l.id) as like_count,
                COUNT(DISTINCT c.id) as comment_count,
                MAX(CASE WHEN l.user_id = ? THEN 1 ELSE 0 END) as is_liked
            FROM posts p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN likes l ON p.id = l.post_id
            LEFT JOIN comments c ON p.id = c.post_id
            GROUP BY p.id, p.user_id, p.post_text, p.post_img, p.created_at, u.username, u.first_name, u.last_name, u.profile_pic
            ORDER BY p.created_at DESC";
    
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        throw new Exception("Prepare failed: " . $conn->error);
    }
    
    $stmt->bind_param("i", $current_user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $posts = array();
    
    while ($row = $result->fetch_assoc()) {
        // Create display name
        $display_name = trim($row['first_name'] . ' ' . $row['last_name']);
        if (empty($display_name)) {
            $display_name = $row['username'] ?: 'Unknown User';
        }
        
        // Handle profile picture URL
        $profile_pic_filename = null;
        if (!empty($row['profile_pic']) && $row['profile_pic'] !== 'default_profile.jpg') {
            $profile_pic_filename = $row['profile_pic'];
        }
        
        // Handle post image URL
        $post_image_url = "http://" . IP_ADDRESS . "/codekendra/web/assets/img/posts/" . $row['post_img'];
        
        $posts[] = array(
            'id' => (int)$row['id'],
            'user_id' => (int)$row['user_id'],
            'user_name' => $display_name,
            'username' => $row['username'],
            'post_description' => $row['post_text'],
            'post_image' => $post_image_url,
            'profile_pic' => $profile_pic_filename,
            'like_count' => (int)$row['like_count'],
            'comment_count' => (int)$row['comment_count'],
            'is_liked' => (bool)$row['is_liked'],
            'created_at' => $row['created_at']
        );
    }
    
    error_log("Returning " . count($posts) . " posts");
    
    echo json_encode([
        'status' => 'success',
        'posts' => $posts,
        'total_posts' => count($posts),
        'debug_user_id' => $current_user_id,
        'debug_method' => $_SERVER['REQUEST_METHOD']
    ], JSON_UNESCAPED_SLASHES);
    
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Get posts error: " . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'file' => __FILE__,
        'debug_method' => $_SERVER['REQUEST_METHOD']
    ]);
}
?>