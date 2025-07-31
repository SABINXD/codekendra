<?php
// api/add_comment.php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

include(__DIR__ . "/config/db.php");

$conn = new mysqli('localhost', 'root', '', 'codekendra');

if ($conn->connect_error) {
    echo json_encode(['status' => 'error', 'message' => 'Database connection failed']);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $post_id = isset($_POST['post_id']) ? (int)$_POST['post_id'] : 0;
    $user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0;
    $comment_text = isset($_POST['comment_text']) ? $_POST['comment_text'] : '';

    if ($post_id <= 0 || $user_id <= 0 || empty($comment_text)) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid post_id, user_id, or empty comment.']);
        exit;
    }

    $insert_stmt = $conn->prepare("INSERT INTO comments (post_id, user_id, comment) VALUES (?, ?, ?)");
    $insert_stmt->bind_param("iis", $post_id, $user_id, $comment_text);

    if ($insert_stmt->execute()) {
        $count_stmt = $conn->prepare("SELECT COUNT(*) AS comment_count FROM comments WHERE post_id = ?");
        $count_stmt->bind_param("i", $post_id);
        $count_stmt->execute();
        $count_result = $count_stmt->get_result();
        $comment_count = 0;
        if ($count_row = $count_result->fetch_assoc()) {
            $comment_count = (int)$count_row['comment_count'];
        }
        $count_stmt->close();

        echo json_encode([
            "status" => "success",
            "message" => "Comment added successfully.",
            "comment_count" => $comment_count,
            "post_id" => $post_id // Include post_id for real-time updates
        ]);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Failed to add comment: ' . $insert_stmt->error]);
    }
    $insert_stmt->close();

} else {
    echo json_encode(["status" => "error", "message" => "Invalid request method."]);
}

$conn->close();
?>