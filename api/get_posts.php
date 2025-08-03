<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

error_reporting(E_ALL);
ini_set('display_errors', 1);
require_once(__DIR__ . '/config/db.php');

try {
    $conn = getDbConnection();
    
    $current_user_id = 0;
    if (isset($_GET['user_id'])) {
        $current_user_id = (int)$_GET['user_id'];
    } elseif (isset($_POST['user_id'])) {
        $current_user_id = (int)$_POST['user_id'];
    }

    // Updated SQL query to include code and tags
    $sql = "SELECT 
                p.id,
                p.user_id,
                p.post_text,
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
                COUNT(DISTINCT c.id) as comment_count,
                MAX(CASE WHEN l.user_id = ? THEN 1 ELSE 0 END) as is_liked
            FROM posts p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN likes l ON p.id = l.post_id
            LEFT JOIN comments c ON p.id = c.post_id
            GROUP BY p.id, p.user_id, p.post_text, p.post_img, p.code_content, p.code_language, p.tags, p.created_at, u.username, u.first_name, u.last_name, u.profile_pic
            ORDER BY p.created_at DESC
            LIMIT 50";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $current_user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $posts = array();
    while ($row = $result->fetch_assoc()) {
        $display_name = trim($row['first_name'] . ' ' . $row['last_name']);
        if (empty($display_name)) {
            $display_name = $row['username'] ?: 'Unknown User';
        }

        $profile_pic_url = null;
        if (!empty($row['profile_pic']) && $row['profile_pic'] !== 'null') {
            $profile_pic_url = "http://" . IP_ADDRESS . "/codekendra/web/assets/img/profile/" . $row['profile_pic'];
        }

        $post_image_url = "";
        if (!empty($row['post_img']) && $row['post_img'] !== 'null') {
            $image_path = __DIR__ . "/../web/assets/img/posts/" . $row['post_img'];
            if (file_exists($image_path)) {
                $post_image_url = "http://" . IP_ADDRESS . "/codekendra/web/assets/img/posts/" . $row['post_img'];
            }
        }

        // Parse tags from comma-separated string
        $tags_array = array();
        if (!empty($row['tags'])) {
            $tags_array = explode(',', $row['tags']);
            $tags_array = array_map('trim', $tags_array);
        }

        $posts[] = array(
            'id' => (int)$row['id'],
            'user_id' => (int)$row['user_id'],
            'user_name' => $display_name,
            'username' => $row['username'],
            'post_description' => $row['post_text'],
            'post_image' => $post_image_url,
            'profile_pic' => $profile_pic_url,
            'like_count' => (int)$row['like_count'],
            'comment_count' => (int)$row['comment_count'],
            'is_liked' => (bool)$row['is_liked'],
            'created_at' => $row['created_at'],
            'code_content' => $row['code_content'],
            'code_language' => $row['code_language'],
            'tags' => $tags_array
        );
    }

    echo json_encode([
        'status' => 'success',
        'posts' => $posts,
        'total_posts' => count($posts)
    ], JSON_UNESCAPED_SLASHES | JSON_PRETTY_PRINT);

    $stmt->close();
    $conn->close();

} catch (Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ], JSON_PRETTY_PRINT);
}
?>