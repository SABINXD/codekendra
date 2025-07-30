<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

include(__DIR__ . "/config/db.php");
const IP_ADDRESS = "192.168.1.5"; // ✅ Defined constant

$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    echo json_encode(['status' => 'error', 'message' => 'Database connection failed']);
    exit;
}

$baseUrl = 'http://' . IP_ADDRESS . '/codekendra/web/assets/img/posts/';

$query = "
    SELECT 
        posts.id,
        posts.user_id,
        users.username AS user_name,
        posts.post_text,
        posts.post_img,
        posts.created_at
    FROM posts
    JOIN users ON posts.user_id = users.id
    ORDER BY posts.created_at DESC
";

$result = $conn->query($query);
if (!$result) {
    echo json_encode(['status' => 'error', 'message' => 'Query failed: ' . $conn->error]);
    exit;
}

$feed = [];
while ($row = $result->fetch_assoc()) {
    $imagePath = $row['post_img'];

    if (strpos($imagePath, 'http') !== 0) {
        $imagePath = $baseUrl . basename($imagePath);
    }

    $feed[] = [
        "id"         => (int) $row['id'],
        "user_id"    => (int) $row['user_id'],
        "user_name"  => $row['user_name'],
        "post_text"  => $row['post_text'],
        "post_img"   => $imagePath,
        "created_at" => date("Y-m-d H:i:s", strtotime($row['created_at'])),
        "like_count" => 0, // ✅ Placeholder default
        "comment_count" => 0 // ✅ Placeholder default
    ];
}

echo json_encode([
    "status" => "success",
    "posts" => $feed
], JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);

$conn->close();
?>
