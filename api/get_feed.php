<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

$conn = new mysqli('localhost', 'root', '', 'codekendra');
if ($conn->connect_error) {
    echo json_encode(['status' => 'fail', 'error' => 'Database connection failed']);
    exit;
}

$baseUrl = 'http://192.168.1.2/codekendra/web/assets/img/posts/';

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
$feed = [];

while ($row = $result->fetch_assoc()) {
    $imagePath = $row['post_img'];

    if (strpos($imagePath, 'http') !== 0) {
        $imagePath = $baseUrl . basename($imagePath);
    }

    $feed[] = [
        "id"         => $row['id'],
        "user_id"    => $row['user_id'],
        "user_name"  => $row['user_name'],
        "post_text"  => $row['post_text'],
        "post_img"   => $imagePath,
        "created_at" => $row['created_at']
    ];
}

echo json_encode([
    "status" => "success",
    "posts" => $feed
], JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);

$conn->close();
?>
