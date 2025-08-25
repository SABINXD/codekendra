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
    $from_user_id = $_POST['from_user_id'] ?? 0;
    $to_user_id = $_POST['to_user_id'] ?? 0;

    // Validate
    if (empty($from_user_id) || empty($to_user_id)) {
        throw new Exception("Missing user IDs");
    }

    // Get messages between the two users
    $query = "SELECT * FROM messages
              WHERE (from_user_id = ? AND to_user_id = ?)
              OR (from_user_id = ? AND to_user_id = ?)
              ORDER BY created_at ASC";

    $stmt = $conn->prepare($query);
    if (!$stmt) {
        throw new Exception("Prepare failed: " . $conn->error);
    }

    $stmt->bind_param("iiii", $from_user_id, $to_user_id, $to_user_id, $from_user_id);
    if (!$stmt->execute()) {
        throw new Exception("Execute failed: " . $stmt->error);
    }

    $result = $stmt->get_result();

    $messages = [];
    while ($row = $result->fetch_assoc()) {
        $messages[] = $row;
    }

    // Mark messages as read (where the current user is the recipient)
    $update_query = "UPDATE messages SET read_status = 1 
                    WHERE from_user_id = ? AND to_user_id = ? AND read_status = 0";
    $update_stmt = $conn->prepare($update_query);
    $update_stmt->bind_param("ii", $to_user_id, $from_user_id);
    $update_stmt->execute();
    $update_stmt->close();

    // Close connection
    $stmt->close();
    $conn->close();

    // Return JSON response
    echo json_encode(['status' => 'success', 'messages' => $messages]);

} catch (Exception $e) {
    // Log the error
    error_log("Error in get_messages.php: " . $e->getMessage());

    // Return error in JSON format
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?>