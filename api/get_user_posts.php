<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 0);
ini_set('log_errors', 1);
ini_set('error_log', __DIR__ . '/php_errors.log');

// Include database configuration
$db_config_path = __DIR__ . "/config/db.php";
if (!file_exists($db_config_path)) {
    echo json_encode(['status' => 'error', 'message' => 'Database configuration file not found']);
    exit;
}

include($db_config_path);

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method.']);
    exit;
}

$user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0;
if ($user_id <= 0) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid user ID.']);
    exit;
}

try {
    // Get database connection using the function from db.php
    $conn = getDbConnection();
    
    // Query to get user's posts
    $query = "SELECT p.id, p.user_id, p.post_text, p.post_img, p.created_at, 
                     p.code_content, p.code_language, p.tags,
                     u.username, u.profile_pic
              FROM posts p
              LEFT JOIN users u ON p.user_id = u.id
              WHERE p.user_id = ?
              ORDER BY p.created_at DESC";
              
    $stmt = $conn->prepare($query);
    if (!$stmt) {
        throw new Exception("Database query preparation failed: " . $conn->error);
    }
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    
    // Bind result variables
    $stmt->bind_result($id, $user_id_result, $post_text, $post_img, $created_at, 
                      $code_content, $code_language, $tags, $username, $profile_pic);
    
    $posts = [];
    while ($stmt->fetch()) {
        // Process tags into an array if they exist
        $tagsArray = [];
        if (!empty($tags)) {
            $tagsArray = explode(',', $tags);
        }
        
        $post = [
            'id' => (int)$id,
            'user_id' => (int)$user_id_result,
            'user_name' => $username,
            'profile_pic' => $profile_pic,
            'post_description' => $post_text, // Map post_text to post_description for compatibility
            'post_image' => $post_img, // Map post_img to post_image for compatibility
            'created_at' => $created_at,
            'code_content' => $code_content,
            'code_language' => $code_language,
            'tags' => $tagsArray
        ];
        
        $posts[] = $post;
    }

    echo json_encode(['status' => 'success', 'posts' => $posts]);
    
    $stmt->close();
    $conn->close();
} catch (Exception $e) {
    error_log("User posts error: " . $e->getMessage());
    echo json_encode(['status' => 'error', 'message' => 'Database error occurred']);
}
?>