php
<?php
// Set headers
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

// Enable error logging but disable display
error_reporting(E_ALL);
ini_set('display_errors', 0);
ini_set('log_errors', 1);
ini_set('error_log', __DIR__ . '/php_errors.log');

// Include database connection
include __DIR__ . '/config/db.php';

try {
    // Get database connection
    $conn = getDbConnection();

    // Get POST data
    $user_id = $_POST['user_id'] ?? 0;

    // Validate
    if (empty($user_id)) {
        throw new Exception("Missing user ID");
    }

    // Get all users that the current user has chatted with
    $query = "SELECT DISTINCT
                CASE
                    WHEN from_user_id = ? THEN to_user_id
                    ELSE from_user_id
                END AS chat_user_id,
                MAX(created_at) AS last_message_time
              FROM messages
              WHERE from_user_id = ? OR to_user_id = ?
              GROUP BY chat_user_id
              ORDER BY last_message_time DESC";

    $stmt = $conn->prepare($query);
    if (!$stmt) {
        throw new Exception("Prepare failed: " . $conn->error);
    }

    $stmt->bind_param("iii", $user_id, $user_id, $user_id);
    if (!$stmt->execute()) {
        throw new Exception("Execute failed: " . $stmt->error);
    }

    $result = $stmt->get_result();

    $chat_list = [];
    while ($row = $result->fetch_assoc()) {
        $chat_user_id = $row['chat_user_id'];

        // Get user details
        $user_query = "SELECT id, first_name, last_name, username, profile_pic FROM users WHERE id = ?";
        $user_stmt = $conn->prepare($user_query);
        if (!$user_stmt) {
            throw new Exception("User query prepare failed: " . $conn->error);
        }
        $user_stmt->bind_param("i", $chat_user_id);
        if (!$user_stmt->execute()) {
            throw new Exception("User query execute failed: " . $user_stmt->error);
        }
        $user_result = $user_stmt->get_result();
        $user_data = $user_result->fetch_assoc();

        // Get last message
        $message_query = "SELECT message, created_at FROM messages
                         WHERE (from_user_id = ? AND to_user_id = ?) OR (from_user_id = ? AND to_user_id = ?)
                         ORDER BY created_at DESC LIMIT 1";
        $message_stmt = $conn->prepare($message_query);
        if (!$message_stmt) {
            throw new Exception("Message query prepare failed: " . $conn->error);
        }
        $message_stmt->bind_param("iiii", $user_id, $chat_user_id, $chat_user_id, $user_id);
        if (!$message_stmt->execute()) {
            throw new Exception("Message query execute failed: " . $message_stmt->error);
        }
        $message_result = $message_stmt->get_result();
        $message_data = $message_result->fetch_assoc();

        // Get unread count
        $unread_query = "SELECT COUNT(*) as unread_count FROM messages
                        WHERE from_user_id = ? AND to_user_id = ? AND read_status = 0";
        $unread_stmt = $conn->prepare($unread_query);
        if (!$unread_stmt) {
            throw new Exception("Unread query prepare failed: " . $conn->error);
        }
        $unread_stmt->bind_param("ii", $chat_user_id, $user_id);
        if (!$unread_stmt->execute()) {
            throw new Exception("Unread query execute failed: " . $unread_stmt->error);
        }
        $unread_result = $unread_stmt->get_result();
        $unread_data = $unread_result->fetch_assoc();

        // Create chat user object with null checks
        $chat_user = [
            'user_id' => $chat_user_id,
            'user_name' => ($user_data['first_name'] ?? '') . ' ' . ($user_data['last_name'] ?? ''),
            'profile_pic' => $user_data['profile_pic'] ?? 'default_profile.jpg',
            'last_message' => $message_data['message'] ?? '',
            'timestamp' => $message_data['created_at'] ?? '',
            'unread_count' => $unread_data['unread_count'] ?? 0
        ];

        $chat_list[] = $chat_user;

        // Close statements
        $user_stmt->close();
        $message_stmt->close();
        $unread_stmt->close();
    }

    // Close connection
    $stmt->close();
    $conn->close();

    // Return JSON response
    echo json_encode(['status' => 'success', 'chat_list' => $chat_list]);

} catch (Exception $e) {
    // Log the error
    error_log("Error in get_chat_list.php: " . $e->getMessage());

    // Return error in JSON format
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?>
