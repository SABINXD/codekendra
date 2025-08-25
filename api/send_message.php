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
    $message = $_POST['message'] ?? '';

    // Validate
    if (empty($from_user_id) || empty($to_user_id) || empty($message)) {
        throw new Exception("Missing required fields");
    }

    // Check if recipient exists
    $user_check = "SELECT id FROM users WHERE id = ?";
    $user_stmt = $conn->prepare($user_check);
    $user_stmt->bind_param("i", $to_user_id);
    $user_stmt->execute();
    $user_result = $user_stmt->get_result();
    
    if ($user_result->num_rows === 0) {
        throw new Exception("Recipient does not exist");
    }
    $user_stmt->close();

    // Insert message
    $query = "INSERT INTO messages (from_user_id, to_user_id, message, read_status) VALUES (?, ?, ?, 0)";
    $stmt = $conn->prepare($query);
    if (!$stmt) {
        throw new Exception("Prepare failed: " . $conn->error);
    }

    $stmt->bind_param("iis", $from_user_id, $to_user_id, $message);

    if ($stmt->execute()) {
        // Get the inserted message ID
        $message_id = $stmt->insert_id;

        // Close connection
        $stmt->close();
        $conn->close();

        // Return success response
        echo json_encode([
            'status' => 'success',
            'message_id' => $message_id
        ]);
    } else {
        throw new Exception("Execute failed: " . $stmt->error);
    }

} catch (Exception $e) {
    // Log the error
    error_log("Error in send_message.php: " . $e->getMessage());

    // Return error in JSON format
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?>