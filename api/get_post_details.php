<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 0);

require_once __DIR__ . "/config/db.php";

try {
    $conn = getDbConnection();
    
    $post_id = isset($_POST['post_id']) ? (int)$_POST['post_id'] : 0;
    
    if ($post_id <= 0) {
        throw new Exception("Invalid post ID: $post_id");
    }
    
    // Get post details with user info
    $sql = "SELECT 
                p.id,
                p.user_id,
                p.post_text as post_description,
                p.post_img,
                p.code_content,
                p.code_language,
                p.tags,
                p.created_at,
                u.username,
                COALESCE(u.first_name, '') as first_name,
                COALESCE(u.last_name, '') as last_name,
                COALESCE(u.profile_pic, 'default_profile.jpg') as profile_pic,
                COUNT(DISTINCT l.id) as like_count,
                COUNT(DISTINCT c.id) as comment_count
            FROM posts p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN likes l ON p.id = l.post_id
            LEFT JOIN comments c ON p.id = c.post_id
            WHERE p.id = ?
            GROUP BY p.id, p.user_id, p.post_text, p.post_img, p.code_content, p.code_language, p.tags, p.created_at, u.username, u.first_name, u.last_name, u.profile_pic";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $post_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($row = $result->fetch_assoc()) {
        // Create display name
        $display_name = trim($row['first_name'] . ' ' . $row['last_name']);
        if (empty($display_name)) {
            $display_name = $row['username'] ?: 'Unknown User';
        }
        
        // Handle profile picture
        $profile_pic_url = null;
        if (!empty($row['profile_pic']) && $row['profile_pic'] !== 'default_profile.jpg') {
            $profile_pic_url = "http://" . IP_ADDRESS . "/codekendra/web/assets/img/profile/" . $row['profile_pic'];
        }
        
        // Handle post image
        $post_image_url = "";
        if (!empty($row['post_img']) && $row['post_img'] !== 'null') {
            $image_path = __DIR__ . "/../web/assets/img/posts/" . $row['post_img'];
            if (file_exists($image_path)) {
                $post_image_url = "http://" . IP_ADDRESS . "/codekendra/web/assets/img/posts/" . $row['post_img'];
            }
        }
        
        // Parse tags
        $tags_array = array();
        if (!empty($row['tags'])) {
            $tags_array = explode(',', $row['tags']);
            $tags_array = array_map('trim', $tags_array);
        }
        
        $post_data = array(
            'id' => (int)$row['id'],
            'user_id' => (int)$row['user_id'],
            'user_name' => $display_name,
            'username' => $row['username'],
            'post_description' => $row['post_description'],
            'post_image' => $post_image_url,
            'profile_pic' => $profile_pic_url,
            'like_count' => (int)$row['like_count'],
            'comment_count' => (int)$row['comment_count'],
            'created_at' => $row['created_at'],
            'code_content' => $row['code_content'],
            'code_language' => $row['code_language'],
            'tags' => $tags_array
        );
        
        $response = [
            'status' => true,
            'post' => $post_data
        ];
        
        echo json_encode($response, JSON_UNESCAPED_SLASHES);
    } else {
        throw new Exception("Post not found");
    }
    
    $stmt->close();
    $conn->close();
    
} catch (Exception $e) {
    error_log("Error in get_post_details.php: " . $e->getMessage());
    echo json_encode([
        'status' => false,
        'message' => $e->getMessage()
    ]);
}
?>