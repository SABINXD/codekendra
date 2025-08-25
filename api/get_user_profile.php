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

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method.']);
    exit;
}

$user_id = isset($_GET['user_id']) ? (int)$_GET['user_id'] : 0;
if ($user_id <= 0) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid user ID.']);
    exit;
}

try {
    // Get database connection using the function from db.php
    $conn = getDbConnection();
    
    // Query to get user profile
    $query = "SELECT id, first_name, last_name, username, bio, profile_pic, email 
              FROM users 
              WHERE id = ?";
              
    $stmt = $conn->prepare($query);
    if (!$stmt) {
        throw new Exception("Database query preparation failed: " . $conn->error);
    }
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    
    // Bind result variables
    $stmt->bind_result($id, $first_name, $last_name, $username, $bio, $profile_pic, $email);
    
    if ($stmt->fetch()) {
        // Get post count
        $postCountQuery = "SELECT COUNT(*) as post_count FROM posts WHERE user_id = ?";
        $postCountStmt = $conn->prepare($postCountQuery);
        $postCountStmt->bind_param("i", $user_id);
        $postCountStmt->execute();
        $postCountResult = $postCountStmt->get_result();
        $postCountRow = $postCountResult->fetch_assoc();
        $postCount = $postCountRow['post_count'];
        $postCountStmt->close();
        
        $user = [
            'id' => (int)$id,
            'first_name' => $first_name,
            'last_name' => $last_name,
            'username' => $username,
            'bio' => $bio,
            'profile_pic' => $profile_pic,
            'email' => $email,
            'post_count' => (int)$postCount
        ];

        echo json_encode(['status' => 'success', 'user' => $user]);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'User not found']);
    }
    
    $stmt->close();
    $conn->close();
} catch (Exception $e) {
    error_log("User profile error: " . $e->getMessage());
    echo json_encode(['status' => 'error', 'message' => 'Database error occurred']);
}
?>