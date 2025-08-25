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

$uid = isset($_POST['uid']) ? (int)$_POST['uid'] : 0;
if ($uid <= 0) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid user ID.']);
    exit;
}

try {
    // Get database connection using the function from db.php
    $conn = getDbConnection();
    
    // Query to get followers count - updated to use follow_list table
    $followersQuery = "SELECT COUNT(*) as followers FROM follow_list WHERE user_id = ?";
    $stmt = $conn->prepare($followersQuery);
    if (!$stmt) {
        throw new Exception("Database query preparation failed for followers: " . $conn->error);
    }
    $stmt->bind_param("i", $uid);
    $stmt->execute();
    
    // Bind result variables
    $stmt->bind_result($followers);
    $stmt->fetch();
    $stmt->close();

    // Query to get following count - updated to use follow_list table
    $followingQuery = "SELECT COUNT(*) as following FROM follow_list WHERE follower_id = ?";
    $stmt = $conn->prepare($followingQuery);
    if (!$stmt) {
        throw new Exception("Database query preparation failed for following: " . $conn->error);
    }
    $stmt->bind_param("i", $uid);
    $stmt->execute();
    
    // Bind result variables
    $stmt->bind_result($following);
    $stmt->fetch();
    $stmt->close();

    echo json_encode([
        'status' => 'success',
        'followers' => (int)$followers,
        'following' => (int)$following
    ]);
    
    $conn->close();
} catch (Exception $e) {
    error_log("Follow stats error: " . $e->getMessage());
    echo json_encode(['status' => 'error', 'message' => 'Database error occurred']);
}
?>